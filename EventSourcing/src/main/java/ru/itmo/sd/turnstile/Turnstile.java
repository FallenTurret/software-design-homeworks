package ru.itmo.sd.turnstile;

import akka.actor.UntypedAbstractActor;
import akka.actor.typed.ActorRef;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.turnstile.commands.RequestPassage;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class Turnstile extends UntypedAbstractActor {
    private final Passage.PassageType direction;
    private final ActorRef<PersistentEventStorage.Command> eventStorage;
    private final Consumer<Boolean> handleResponse;

    private final Clock clock;

    public Turnstile(Passage.PassageType direction,
                     ActorRef<PersistentEventStorage.Command> eventStorage,
                     Consumer<Boolean> handleResponse,
                     Clock clock) {
        super();
        this.direction = direction;
        this.eventStorage = eventStorage;
        this.handleResponse = handleResponse;
        this.clock = clock;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Integer) {
            var passage = new Passage((Integer) message, direction, LocalDateTime.now(clock));
            eventStorage.tell(new RequestPassage(passage, self()));
        } else if (message instanceof Boolean) {
            handleResponse.accept((Boolean) message);
        }
    }
}
