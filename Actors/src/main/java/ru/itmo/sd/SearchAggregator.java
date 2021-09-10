package ru.itmo.sd;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import org.json.JSONObject;
import ru.itmo.sd.actors.MasterActor;

import java.time.Duration;

public class SearchAggregator {
    public static JSONObject search(String query, Duration timeout, int port) {
        ActorSystem system = ActorSystem.create();
        ActorRef master = system.actorOf(Props.create(MasterActor.class, timeout, port));
        var search = Patterns.ask(master, query, Duration.ofDays(1))
                .toCompletableFuture();
        try {
            return (JSONObject) search.get();
        } catch (Throwable e) {
            return new JSONObject();
        }
    }
}
