package ru.itmo.sd.reportservice.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.itmo.sd.eventstorage.PersistentEventStorage;

@JsonSerialize
public class ReportServiceStopped implements PersistentEventStorage.Event {
}
