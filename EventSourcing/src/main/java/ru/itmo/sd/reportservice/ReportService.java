package ru.itmo.sd.reportservice;

import akka.actor.UntypedAbstractActor;
import akka.actor.typed.ActorRef;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.PersistenceQuery;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.reportservice.commands.StartReportService;
import ru.itmo.sd.reportservice.commands.StopReportService;
import ru.itmo.sd.turnstile.Passage;
import ru.itmo.sd.turnstile.events.PassageAllowed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ReportService extends UntypedAbstractActor {
    private final Map<LocalDate, Integer> visitsByDay = new HashMap<>();
    private final Map<YearMonth, Integer> visitsByMonth = new HashMap<>();
    private final Map<YearMonth, Long> totalMinutesByMonth = new HashMap<>();
    private final Map<YearMonth, Set<Integer>> visitorsByMonth = new HashMap<>();
    private final Map<Integer, LocalDateTime> lastInById = new HashMap<>();

    private final ActorRef<PersistentEventStorage.Command> eventStorage;
    private final String storageId;
    private final JdbcReadJournal readJournal;

    public ReportService(ActorRef<PersistentEventStorage.Command> eventStorage, String storageId) {
        super();
        this.eventStorage = eventStorage;
        this.storageId = storageId;
        this.readJournal = PersistenceQuery.get(getContext().getSystem())
                .getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            if (message == "start") {
                eventStorage.tell(new StartReportService(self()));
            } else if (message == "started") {
                loadEvents();
            } else if (message == "stop") {
                eventStorage.tell(new StopReportService(self()));
            } else if (message == "stopped") {
                getContext().stop(self());
            }
        } else if (message instanceof Passage) {
            updateByEvent(new PassageAllowed((Passage) message));
        } else if (message instanceof VisitsInDay) {
            getSender().tell(visitsInDay(((VisitsInDay) message).date), self());
        } else if (message instanceof AverageVisitsInMonth) {
            getSender().tell(averageVisitsInMonth(((AverageVisitsInMonth) message).month), self());
        } else if (message instanceof AverageVisitMinutesInMonth) {
            getSender().tell(averageVisitMinutesInMonth(((AverageVisitMinutesInMonth) message).month), self());
        }
    }

    private void loadEvents() {
        try {
            readJournal
                    .currentEventsByPersistenceId(storageId, 0, Long.MAX_VALUE)
                    .map(EventEnvelope::event)
                    .takeWhile(event -> {
                        if (event instanceof StartReportService) {
                            return ((StartReportService) event).reportService != self();
                        }
                        return true;
                    })
                    .runForeach(this::updateByEvent, getContext().getSystem())
                    .toCompletableFuture().get();
        } catch (Exception e) {
            loadEvents();
        }
    }

    private void updateByEvent(Object event) {
        if (event instanceof PassageAllowed) {
            var passage = ((PassageAllowed) event).passage;
            if (passage.getPassageType() == Passage.PassageType.IN) {
                var date = passage.getPassageTime().toLocalDate();
                var month = YearMonth.of(date.getYear(), date.getMonth());
                visitsByDay.put(date, visitsByDay.getOrDefault(date, 0) + 1);
                visitsByMonth.put(month, visitsByMonth.getOrDefault(month, 0) + 1);
                if (visitorsByMonth.containsKey(month)) {
                    visitorsByMonth.get(month).add(passage.getSubscriptionId());
                } else {
                    var set = new HashSet<Integer>();
                    set.add(passage.getSubscriptionId());
                    visitorsByMonth.put(month, set);
                }
                lastInById.put(passage.getSubscriptionId(), passage.getPassageTime());
            } else {
                var inTime = lastInById.get(passage.getSubscriptionId());
                var month = YearMonth.of(inTime.getYear(), inTime.getMonth());
                totalMinutesByMonth.put(
                        month,
                        totalMinutesByMonth.getOrDefault(month, 0L)
                                +
                        ChronoUnit.MINUTES.between(inTime, passage.getPassageTime()));
            }
        }
    }

    private int visitsInDay(LocalDate date) {
        var res = visitsByDay.get(date);
        return Objects.requireNonNullElse(res, 0);
    }

    private int averageVisitsInMonth(YearMonth month) {
        if (visitorsByMonth.containsKey(month)) {
            return visitsByMonth.get(month) / visitorsByMonth.get(month).size();
        } else {
            return 0;
        }
    }

    private long averageVisitMinutesInMonth(YearMonth month) {
        if (visitorsByMonth.containsKey(month)) {
            return totalMinutesByMonth.get(month) / visitorsByMonth.get(month).size();
        } else {
            return 0;
        }
    }

    public static class VisitsInDay {
        private final LocalDate date;

        public VisitsInDay(LocalDate date) {
            this.date = date;
        }
    }

    public static class AverageVisitsInMonth {
        private final YearMonth month;

        public AverageVisitsInMonth(YearMonth month) {
            this.month = month;
        }
    }

    public static class AverageVisitMinutesInMonth {
        private final YearMonth month;

        public AverageVisitMinutesInMonth(YearMonth month) {
            this.month = month;
        }
    }
}
