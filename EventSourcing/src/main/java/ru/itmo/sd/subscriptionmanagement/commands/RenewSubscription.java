package ru.itmo.sd.subscriptionmanagement.commands;

import akka.Done;
import akka.actor.typed.ActorRef;
import akka.pattern.StatusReply;
import ru.itmo.sd.eventstorage.PersistentEventStorage;

import java.time.LocalDate;

public class RenewSubscription implements PersistentEventStorage.Command {
    public final int subscriptionId;
    public final LocalDate localDate;
    public final ActorRef<StatusReply<Done>> actor;

    public RenewSubscription(int subscriptionId, LocalDate localDate, ActorRef<StatusReply<Done>> actor) {
        this.subscriptionId = subscriptionId;
        this.localDate = localDate;
        this.actor = actor;
    }
}
