package ru.itmo.sd.subscriptionmanagement.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.itmo.sd.eventstorage.PersistentEventStorage;
import ru.itmo.sd.subscriptionmanagement.Subscription;

public class SubscriptionAdded implements PersistentEventStorage.Event {
    public final Subscription subscription;

    @JsonCreator
    public SubscriptionAdded(Subscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public String toString() {
        return "SubscriptionAdded(" + subscription.toString() + ")";
    }
}
