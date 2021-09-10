package ru.itmo.sd.reportservice.events;

import akka.actor.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import ru.itmo.sd.eventstorage.PersistentEventStorage;

public class ReportServiceStarted implements PersistentEventStorage.Event {
    public final ActorRef reportService;

    @JsonCreator
    public ReportServiceStarted(ActorRef reportService) {
        this.reportService = reportService;
    }

    @Override
    public String toString() {
        return "ReportServiceStarted(" + reportService + ")";
    }
}
