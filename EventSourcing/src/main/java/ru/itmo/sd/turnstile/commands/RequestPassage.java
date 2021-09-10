package ru.itmo.sd.turnstile.commands;

import akka.actor.ActorRef;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.turnstile.Passage;

public class RequestPassage implements PersistentEventStorage.Command {
    public final Passage passage;
    public final ActorRef turnstile;

    public RequestPassage(Passage passage, ActorRef turnstile) {
        this.passage = passage;
        this.turnstile = turnstile;
    }
}
