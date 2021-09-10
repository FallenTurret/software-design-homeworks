package ru.itmo.sd.subscriptionmanagement;

import java.time.LocalDate;

public class Subscription {
    private final int id;
    private final String ownerName;
    private LocalDate renewalDate;

    public Subscription(int id, String ownerName, LocalDate renewalDate) {
        this.id = id;
        this.ownerName = ownerName;
        this.renewalDate = renewalDate;
    }

    public boolean isActive(LocalDate currentDate) {
        var expirationDate = renewalDate.plusMonths(1);
        return currentDate.isBefore(expirationDate);
    }

    public int getId() {
        return id;
    }

    public void renew(LocalDate localDate) {
        renewalDate = localDate;
    }

    @Override
    public String toString() {
        return "Subscription(" + id + "," + ownerName + "," + renewalDate + ")";
    }
}
