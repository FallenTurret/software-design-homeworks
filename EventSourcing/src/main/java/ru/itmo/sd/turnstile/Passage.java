package ru.itmo.sd.turnstile;

import java.time.LocalDateTime;

public class Passage {
    private final int subscriptionId;
    private final PassageType passageType;
    private final LocalDateTime passageTime;

    public Passage(int subscriptionId, PassageType passageType, LocalDateTime passageTime) {
        this.subscriptionId = subscriptionId;
        this.passageType = passageType;
        this.passageTime = passageTime;
    }

    public int getSubscriptionId() {
        return subscriptionId;
    }

    public PassageType getPassageType() {
        return passageType;
    }

    public LocalDateTime getPassageTime() {
        return passageTime;
    }

    public enum PassageType {
        IN,
        OUT
    }

    @Override
    public String toString() {
        return "Passage(" + subscriptionId + "," + passageType + "," + passageTime + ")";
    }
}
