package ru.itmo.sd.eventstorage;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Adapter;
import akka.pattern.StatusReply;
import akka.persistence.typed.PersistenceId;
import akka.persistence.typed.javadsl.CommandHandler;
import akka.persistence.typed.javadsl.Effect;
import akka.persistence.typed.javadsl.EventHandler;
import akka.persistence.typed.javadsl.EventSourcedBehavior;
import ru.itmo.sd.reportservice.commands.StartReportService;
import ru.itmo.sd.reportservice.commands.StopReportService;
import ru.itmo.sd.reportservice.events.ReportServiceStarted;
import ru.itmo.sd.reportservice.events.ReportServiceStopped;
import ru.itmo.sd.subscriptionmanagement.Subscription;
import ru.itmo.sd.subscriptionmanagement.commands.AddSubscription;
import ru.itmo.sd.subscriptionmanagement.commands.RenewSubscription;
import ru.itmo.sd.subscriptionmanagement.events.SubscriptionAdded;
import ru.itmo.sd.subscriptionmanagement.events.SubscriptionRenewed;
import ru.itmo.sd.turnstile.Passage;
import ru.itmo.sd.turnstile.commands.RequestPassage;
import ru.itmo.sd.turnstile.events.PassageAllowed;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PersistentEventStorage extends EventSourcedBehavior<
        PersistentEventStorage.Command,
        PersistentEventStorage.Event,
        PersistentEventStorage.State> {

    public static Behavior<Command> create(String storageId) {
        return new PersistentEventStorage(PersistenceId.ofUniqueId(storageId));
    }

    private PersistentEventStorage(PersistenceId persistenceId) {
        super(persistenceId);
    }

    @Override
    public State emptyState() {
        return new State();
    }

    @Override
    public CommandHandler<Command, Event, State> commandHandler() {
        return newCommandHandlerBuilder()
            .forAnyState()
            .onCommand(AddSubscription.class, command -> Effect()
                    .persist(new SubscriptionAdded(command.subscription))
                    .thenReply(command.actor, state -> StatusReply.ack()))
            .onCommand(RenewSubscription.class, command -> Effect()
                    .persist(new SubscriptionRenewed(command.subscriptionId, command.localDate))
                    .thenReply(command.actor, state -> StatusReply.ack()))
            .onCommand(RequestPassage.class, this::onCheckPassage)
            .onCommand(StartReportService.class, command -> Effect()
                    .persist(new ReportServiceStarted(command.reportService))
                    .thenReply(Adapter.toTyped(command.reportService), state -> "started"))
            .onCommand(StopReportService.class, command -> Effect()
                    .persist(new ReportServiceStopped())
                    .thenReply(Adapter.toTyped(command.reportService), state -> "stopped"))
            .build();
    }

    @Override
    public EventHandler<State, Event> eventHandler() {
        return newEventHandlerBuilder()
            .forAnyState()
            .onEvent(SubscriptionAdded.class, (state, event) -> state.addSubscription(event.subscription))
            .onEvent(SubscriptionRenewed.class, (state, event) -> state.renewSubscription(event.subscriptionId, event.localDate))
            .onEvent(ReportServiceStarted.class, (state, event) -> state.startReportService(event.reportService))
            .onEvent(ReportServiceStopped.class, (state, event) -> state.stopReportService())
            .onEvent(PassageAllowed.class, (state, event) -> state)
            .build();
    }

    public interface Command extends CborSerializable {}
    public interface Event extends CborSerializable {}

    public static final class State implements CborSerializable {
        private final Map<Integer, Subscription> subscriptions = new HashMap<>();
        private ActorRef reportService = null;

        public Subscription getSubscription(int subscriptionId) {
            return subscriptions.get(subscriptionId);
        }

        public boolean isPassageAllowed(Passage passage) {
            if (passage.getPassageType().equals(Passage.PassageType.OUT))
                return true;
            var subscription = getSubscription(passage.getSubscriptionId());
            return subscription.isActive(passage.getPassageTime().toLocalDate());
        }

        public boolean isReportServiceActive() {
            return reportService != null;
        }

        public State addSubscription(Subscription subscription) {
            subscriptions.put(subscription.getId(), subscription);
            return this;
        }

        public State renewSubscription(int subscriptionId, LocalDate localDate) {
            getSubscription(subscriptionId).renew(localDate);
            return this;
        }

        public State startReportService(ActorRef reportService) {
            this.reportService = reportService;
            return this;
        }

        public State stopReportService() {
            this.reportService = null;
            return this;
        }
    }

    private Effect<Event, State> onCheckPassage(State state, RequestPassage requestPassage) {
        if (state.isPassageAllowed(requestPassage.passage)) {
            requestPassage.turnstile.tell(Boolean.TRUE, ActorRef.noSender());
            if (state.isReportServiceActive()) {
                state.reportService.tell(requestPassage.passage, ActorRef.noSender());
            }
            return Effect().persist(new PassageAllowed(requestPassage.passage));
        } else {
            requestPassage.turnstile.tell(Boolean.FALSE, ActorRef.noSender());
            return Effect().none();
        }
    }
}
