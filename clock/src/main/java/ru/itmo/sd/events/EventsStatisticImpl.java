package ru.itmo.sd.events;

import org.jetbrains.annotations.NotNull;
import ru.itmo.sd.clock.Clock;
import ru.itmo.sd.clock.NormalClock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EventsStatisticImpl implements EventsStatistic {
    Map <String, Queue<Instant>> events = new HashMap<>();
    Clock clock = new NormalClock();

    public EventsStatisticImpl() {}
    public EventsStatisticImpl(@NotNull Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        if (!events.containsKey(name)) {
            events.put(name, new LinkedList<>());
        }
        events.get(name).offer(clock.now());
    }

    private void update(String name, Instant from) {
        if (from == null) {
            from = clock.now().minus(1, ChronoUnit.HOURS);
        }
        var queue = events.get(name);
        while (!queue.isEmpty()) {
            var time = queue.peek();
            if (time.isBefore(from)) {
                queue.poll();
            } else {
                break;
            }
        }
        if (queue.isEmpty()) {
            events.remove(name);
        }
    }

    private void updateAll() {
        var from = clock.now().minus(1, ChronoUnit.HOURS);
        List.copyOf(events.keySet()).forEach(key -> update(key, from));
    }

    @Override
    public double getEventStatisticByName(String name) {
        if (events.containsKey(name)) {
            update(name, null);
            return (double)events.get(name).size() / 60;
        } else {
            return 0;
        }
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        updateAll();
        var res = new HashMap<String, Double>();
        events.entrySet().stream()
                .map(x -> Map.entry(x.getKey(), (double)x.getValue().size() / 60))
                .forEach(x -> res.put(x.getKey(), x.getValue()));
        return res;
    }

    @Override
    public void printStatistic() {
        getAllEventStatistic().forEach((key, value) -> {
            System.out.print(key);
            System.out.print(": ");
            System.out.print(value);
            System.out.println("rpm");
        });
    }
}
