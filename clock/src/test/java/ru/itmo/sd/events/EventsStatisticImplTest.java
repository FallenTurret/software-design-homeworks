package ru.itmo.sd.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.itmo.sd.clock.SettableClock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventsStatisticImplTest {

    private final SettableClock clock = new SettableClock(Instant.now());
    private final EventsStatistic eventsStatistic = new EventsStatisticImpl(clock);

    @AfterEach
    void clearAll() {
        clock.setNow(clock.now().plus(1, ChronoUnit.DAYS));
    }

    @Test
    void eventsAreDeletedAfterOneHour() {
        eventsStatistic.incEvent("1");
        eventsStatistic.incEvent("2");
        eventsStatistic.incEvent("1");
        clock.setNow(clock.now().plus(1, ChronoUnit.HOURS).plus(1, ChronoUnit.SECONDS));
        assertTrue(eventsStatistic.getAllEventStatistic().isEmpty());
    }

    @Test
    void singleEventRpmIsCountedCorrectly() {
        eventsStatistic.incEvent("1");
        eventsStatistic.incEvent("1");
        assertEquals((double)2 / 60, eventsStatistic.getEventStatisticByName("1"));
        clock.setNow(clock.now().plus(30, ChronoUnit.MINUTES));
        assertEquals((double)2 / 60, eventsStatistic.getEventStatisticByName("1"));
        eventsStatistic.incEvent("1");
        assertEquals((double)3 / 60, eventsStatistic.getEventStatisticByName("1"));
        clock.setNow(clock.now().plus(40, ChronoUnit.MINUTES));
        assertEquals((double)1 / 60, eventsStatistic.getEventStatisticByName("1"));
    }

    @Test
    void multipleEventsRpmIsCountedCorrectly() {
        eventsStatistic.incEvent("1");
        eventsStatistic.incEvent("2");
        eventsStatistic.incEvent("3");
        var res = eventsStatistic.getAllEventStatistic();
        assertEquals(3, res.size());
        assertEquals((double)1 / 60, (double)res.get("1"));
        assertEquals((double)1 / 60, (double)res.get("2"));
        assertEquals((double)1 / 60, (double)res.get("3"));
        clock.setNow(clock.now().plus(50, ChronoUnit.MINUTES));
        eventsStatistic.incEvent("2");
        eventsStatistic.incEvent("4");
        res = eventsStatistic.getAllEventStatistic();
        assertEquals(4, res.size());
        assertEquals((double)1 / 60, (double)res.get("1"));
        assertEquals((double)2 / 60, (double)res.get("2"));
        assertEquals((double)1 / 60, (double)res.get("3"));
        assertEquals((double)1 / 60, (double)res.get("4"));
        clock.setNow(clock.now().plus(20, ChronoUnit.MINUTES));
        res = eventsStatistic.getAllEventStatistic();
        assertEquals(2, res.size());
        assertEquals((double)1 / 60, (double)res.get("2"));
        assertEquals((double)1 / 60, (double)res.get("4"));
    }

    @Test
    void printingEventsRpmWorks() {
        eventsStatistic.incEvent("1");
        eventsStatistic.incEvent("2");
        eventsStatistic.incEvent("3");
        eventsStatistic.printStatistic();
    }
}