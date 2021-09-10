package ru.itmo.sd.reportservice.commands;

import akka.actor.ActorRef;
import ru.itmo.sd.eventstorage.PersistentEventStorage;

public class StartReportService implements PersistentEventStorage.Command {
    public final ActorRef reportService;

    public StartReportService(ActorRef reportService) {
        this.reportService = reportService;
    }
}
