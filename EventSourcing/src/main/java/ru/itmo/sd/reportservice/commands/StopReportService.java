package ru.itmo.sd.reportservice.commands;

import akka.actor.ActorRef;
import ru.itmo.sd.eventstorage.PersistentEventStorage;

public class StopReportService implements PersistentEventStorage.Command {
    public final ActorRef reportService;

    public StopReportService(ActorRef reportService) {
        this.reportService = reportService;
    }
}
