package ru.itmo.sd.events;

import java.util.Map;

public interface EventsStatistic {
    void incEvent(String name);
    double getEventStatisticByName(String name);
    Map<String, Double> getAllEventStatistic();
    void printStatistic();
}
