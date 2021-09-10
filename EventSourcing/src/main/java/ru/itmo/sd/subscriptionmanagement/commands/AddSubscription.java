package ru.itmo.sd.subscriptionmanagement.commands;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.pattern.StatusReply;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.subscriptionmanagement.Subscription;

public class AddSubscription implements PersistentEventStorage.Command {
    public final Subscription subscription;
    public final ActorRef<StatusReply<Done>> actor;

    public AddSubscription(Subscription subscription, ActorRef<StatusReply<Done>> actor) {
        this.subscription = subscription;
        this.actor = actor;
    }
}
