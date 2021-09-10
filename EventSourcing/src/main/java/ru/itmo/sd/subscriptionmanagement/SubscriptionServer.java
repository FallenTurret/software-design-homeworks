package ru.itmo.sd.subscriptionmanagement;

import akka.actor.ActorSystem;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.pattern.Patterns;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.PersistenceQuery;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.subscriptionmanagement.commands.AddSubscription;
import ru.itmo.sd.subscriptionmanagement.commands.RenewSubscription;
import ru.itmo.sd.subscriptionmanagement.events.SubscriptionAdded;
import ru.itmo.sd.subscriptionmanagement.events.SubscriptionRenewed;
import rx.Observable;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SubscriptionServer {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);
    private final ActorRef<PersistentEventStorage.Command> eventStorage;
    private final String storageId;
    private final ActorSystem system;
    private int nextSubscriptionId = 0;

    private final Clock clock;
    private HttpServer<ByteBuf, ByteBuf> server;

    public SubscriptionServer(ActorRef<PersistentEventStorage.Command> eventStorage, String storageId, ActorSystem system, Clock clock) {
        this.eventStorage = eventStorage;
        this.storageId = storageId;
        this.system = system;
        this.clock = clock;
    }

    public void start() {
        server = HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    String query = req.getDecodedPath().substring(1);
                    var response = handleQuery(query, req.getQueryParameters());
                    return resp.writeString(response);
                });
    }

    public void stop() {
        server.shutdown();
    }

    private Observable<String> handleQuery(String query, Map<String, List<String>> parameters) {
        switch (query) {
            case "view":
                if (parameters.containsKey("id")) {
                    Subscription subscription;
                    try {
                        subscription = getSubscription(Integer.parseInt(parameters.get("id").get(0)));
                    } catch (Exception e) {
                        return Observable.just("Error while getting subscription: try again");
                    }
                    if (subscription == null) {
                        return Observable.just("Subscription with given id not found");
                    } else {
                        return Observable.just(subscription.toString());
                    }
                } else {
                    return Observable.just("Subscription id is not specified");
                }
            case "add":
                if (parameters.containsKey("name")) {
                    String name = parameters.get("name").get(0);
                    try {
                        addSubscription(new Subscription(nextSubscriptionId++, name, LocalDate.now(clock)));
                        return Observable.just("Subscription for " + name + " added successfully");
                    } catch (Exception e) {
                        return Observable.just("New subscription error: try again");
                    }
                } else {
                    return Observable.just("Name is not specified");
                }
            case "renew":
                if (parameters.containsKey("id")) {
                    int subscriptionId = Integer.parseInt(parameters.get("id").get(0));
                    try {
                        renewSubscription(subscriptionId);
                        return Observable.just("Subscription with id " + subscriptionId + " renewed successfully");
                    } catch (Exception e) {
                        return Observable.just("Subscription renewal error: check id and try again");
                    }
                } else {
                    return Observable.just("Id is not specified");
                }
            default:
                return Observable.just("Unsupported query requested");
        }
    }

    private Subscription getSubscription(int subscriptionId) throws ExecutionException, InterruptedException {
        var readJournal = PersistenceQuery.get(system)
                .getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());
        var stream = readJournal.currentEventsByPersistenceId(storageId, 0L, Long.MAX_VALUE);
        var events = stream.map(EventEnvelope::event);
        var getSub = events.runFold(
                (Subscription) null,
                (sub, event) -> {
                    if (event instanceof SubscriptionAdded
                            && ((SubscriptionAdded) event).subscription.getId() == subscriptionId) {
                        return ((SubscriptionAdded) event).subscription;
                    }
                    if (event instanceof SubscriptionRenewed
                            && ((SubscriptionRenewed) event).subscriptionId == subscriptionId) {
                        sub.renew(((SubscriptionRenewed) event).localDate);
                        return sub;
                    }
                    return sub;
                },
                system
        ).toCompletableFuture();
        return getSub.get();
    }

    private void addSubscription(Subscription subscription) throws ExecutionException, InterruptedException {
        var request = Patterns.askWithReplyTo(
                Adapter.toClassic(eventStorage),
                replyTo -> new AddSubscription(subscription, Adapter.toTyped(replyTo)),
                REQUEST_TIMEOUT).toCompletableFuture();
        request.get();
    }

    private void renewSubscription(int subscriptionId) throws ExecutionException, InterruptedException {
        var request = Patterns.askWithReplyTo(
                Adapter.toClassic(eventStorage),
                replyTo -> new RenewSubscription(subscriptionId, LocalDate.now(clock), Adapter.toTyped(replyTo)),
                REQUEST_TIMEOUT).toCompletableFuture();
        request.get();
    }
}
