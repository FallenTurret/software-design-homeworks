package ru.itmo.sd.turnstile.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.turnstile.Passage;

public class PassageAllowed implements PersistentEventStorage.Event {
    public final Passage passage;

    @JsonCreator
    public PassageAllowed(Passage passage) {
        this.passage = passage;
    }

    @Override
    public String toString() {
        return "PassageAllowed(" + passage.toString() + ")";
    }
}
