package ru.itmo.sd;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.typed.javadsl.Adapter;
import akka.pattern.Patterns;
import akka.persistence.jdbc.testkit.javadsl.SchemaUtils;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.reportservice.ReportService;
import ru.itmo.sd.subscriptionmanagement.Subscription;
import ru.itmo.sd.subscriptionmanagement.SubscriptionServer;
import ru.itmo.sd.turnstile.Passage;
import ru.itmo.sd.turnstile.Turnstile;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SystemTest {
    ActorSystem system = ActorSystem.create("postgres", ConfigFactory.parseFile(new File("/home/danil/SD/EventSourcing/src/test/resources/postgres-application.conf")).resolve());

    private final HttpClient client = HttpClient.newHttpClient();
    private final String prefix = "http://localhost:8080/";
    private akka.actor.ActorRef turnstileIn;
    private akka.actor.ActorRef turnstileOut;
    private volatile Boolean in;
    private volatile Boolean out;
    private akka.actor.ActorRef reportService;
    private SubscriptionServer server;

    private ForwardClock clock;
    private static final AtomicInteger counter = new AtomicInteger();
    private static String newStorageId() {
        return "storage-" + counter.getAndIncrement();
    }

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        SchemaUtils.dropIfExists(system).toCompletableFuture().get();
        SchemaUtils.createIfNotExists(system).toCompletableFuture().get();
        clock = new ForwardClock();
        var storageId = newStorageId();
        var storage = Adapter.toTyped(system).systemActorOf(PersistentEventStorage.create(storageId), "storage", akka.actor.typed.Props.empty());
        turnstileIn = system
                .actorOf(Props.create(
                        Turnstile.class,
                        Passage.PassageType.IN,
                        storage,
                        (Consumer<Boolean>) value -> in = value,
                        clock));
        turnstileOut = system
                .actorOf(Props.create(
                        Turnstile.class,
                        Passage.PassageType.OUT,
                        storage,
                        (Consumer<Boolean>) value -> out = value,
                        clock));
        reportService = system
                .actorOf(Props.create(
                        ReportService.class,
                        storage,
                        storageId
                ));
        server = new SubscriptionServer(storage, storageId, system, clock);
        server.start();
    }

    @AfterEach
    void tearDown() throws ExecutionException, InterruptedException {
        server.stop();
        SchemaUtils.dropIfExists(system).toCompletableFuture().get();
    }

    @Test
    void subscriptionManagementWorks() {
        addSubscription("Vasya");
        var sub = new Subscription(0, "Vasya", LocalDate.now(clock));
        assertEquals(sub.toString(), getSubscription(0));
        clock.nDaysForward(50);
        renewSubscription(0);
        sub = new Subscription(0, "Vasya", LocalDate.now(clock));
        assertEquals(sub.toString(), getSubscription(0));
        addSubscription("Petya");
        sub = new Subscription(1, "Petya", LocalDate.now(clock));
        assertEquals(sub.toString(), getSubscription(1));
        clock.nDaysForward(50);
        renewSubscription(1);
        sub = new Subscription(1, "Petya", LocalDate.now(clock));
        assertEquals(sub.toString(), getSubscription(1));
    }

    @Test
    void turnstilesWorkCorrectly() {
        addSubscription("Vasya");
        passIn(0);
        assertTrue(in);
        clock.nDaysForward(1);
        passOut(0);
        assertTrue(out);
        passIn(0);
        assertTrue(in);
        clock.nDaysForward(50);
        passOut(0);
        assertTrue(out);
        passIn(0);
        assertFalse(in);
        renewSubscription(0);
        passIn(0);
        assertTrue(in);
        passOut(0);
        assertTrue(out);
    }

    @Test
    void reportServiceWorks() throws InterruptedException {
        addSubscription("Vasya");
        passIn(0);
        clock.nDaysForward(1);
        passOut(0);
        reportService.tell("start", ActorRef.noSender());
        Thread.sleep(1000);
        assertEquals(1, visitsInDay(LocalDate.now(clock).minusDays(1)));
        addSubscription("Petya");
        passIn(1);
        clock.nDaysForward(1);
        passIn(0);
        clock.nDaysForward(1);
        passOut(1);
        passOut(0);
        clock.nDaysForward(1);
        passIn(1);
        clock.nDaysForward(3);
        passOut(1);
        assertEquals(2, averageVisitsInMonth(YearMonth.of(LocalDate.now(clock).getYear(), LocalDate.now(clock).getMonth())));
        assertEquals(24 * 60 * 7 / 2, averageVisitMinutesInMonth(YearMonth.of(LocalDate.now(clock).getYear(), LocalDate.now(clock).getMonth())));
        reportService.tell("stop", ActorRef.noSender());
        Thread.sleep(1000);
    }

    private void passIn(int subscriptionId) {
        in = null;
        turnstileIn.tell(subscriptionId, ActorRef.noSender());
        while (in == null) {
            Thread.onSpinWait();
        }
    }

    private void passOut(int subscriptionId) {
        out = null;
        turnstileOut.tell(subscriptionId, ActorRef.noSender());
        while (out == null) {
            Thread.onSpinWait();
        }
    }

    private int visitsInDay(LocalDate date) {
        try {
            return (int) Patterns.ask(reportService, new ReportService.VisitsInDay(date), Duration.ofDays(1))
                    .toCompletableFuture().get();
        } catch (Exception e) {
            return -1;
        }
    }

    private int averageVisitsInMonth(YearMonth month) {
        try {
            return (int) Patterns.ask(reportService, new ReportService.AverageVisitsInMonth(month), Duration.ofDays(1))
                    .toCompletableFuture().get();
        } catch (Exception e) {
            return -1;
        }
    }

    private long averageVisitMinutesInMonth(YearMonth month) {
        try {
            return (long) Patterns.ask(reportService, new ReportService.AverageVisitMinutesInMonth(month), Duration.ofDays(1))
                    .toCompletableFuture().get();
        } catch (Exception e) {
            return -1;
        }
    }

    private String getSubscription(int subscriptionId) {
        return requestResponse(prefix + "view?id=" + subscriptionId);
    }

    private void addSubscription(String name) {
        requestResponse(prefix + "add?name=" + name);
    }

    private void renewSubscription(int subscriptionId) {
        requestResponse(prefix + "renew?id=" + subscriptionId);
    }

    private String requestResponse(String request) {
        var httpRequest = HttpRequest.newBuilder(URI.create(request)).build();
        try {
            return client.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            return null;
        }
    }

    public static class ForwardClock extends Clock {
        private Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneId.of("Asia/Calcutta"));

        @Override
        public ZoneId getZone() {
            return clock.getZone();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return clock.withZone(zone);
        }

        @Override
        public Instant instant() {
            return clock.instant();
        }

        public void nDaysForward(int n) {
            clock = Clock.offset(clock, Duration.ofDays(n));
        }
    }
}