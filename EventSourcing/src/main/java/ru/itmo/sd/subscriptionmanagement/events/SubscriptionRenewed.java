package ru.itmo.sd.subscriptionmanagement.events;

import ru.itmo.sd.eventstorage.PersistentEventStorage;

import java.time.LocalDate;

public class SubscriptionRenewed implements PersistentEventStorage.Event {
    public final int subscriptionId;
    public final LocalDate localDate;

    public SubscriptionRenewed(int subscriptionId, LocalDate localDate) {
        this.subscriptionId = subscriptionId;
        this.localDate = localDate;
    }
}
