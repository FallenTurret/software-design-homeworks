package ru.itmo.sd.actors;

import akka.actor.*;
import org.json.JSONObject;

import java.time.Duration;

public class MasterActor extends UntypedAbstractActor {
    private final Duration timeout;
    private final JSONObject jsonObject = new JSONObject();
    private int successes = 0;

    private final int port;
    private ActorRef parent;

    public MasterActor(Duration timeout, int port) {
        super();
        this.timeout = timeout;
        this.port = port;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof JSONObject) {
            addResult((JSONObject) message);
        } else if (message instanceof String) {
            parent = getSender();
            initiateSearch((String) message);
        } else if (message instanceof ReceiveTimeout) {
            stopProcessing();
        }
    }

    private void addResult(JSONObject result) {
        for (String key: JSONObject.getNames(result)) {
            jsonObject.put(key, result.get(key));
        }
        if (++successes == 3) {
            stopProcessing();
        }
    }

    private void initiateSearch(String query) {
        ActorRef googleActor = getContext().getSystem().actorOf(
                Props.create(RequestActor.class, "Google", "http://localhost:" + port + "/google-"));
        ActorRef yandexActor = getContext().getSystem().actorOf(
                Props.create(RequestActor.class, "Yandex", "http://localhost:" + port + "/yandex-"));
        ActorRef bingActor = getContext().getSystem().actorOf(
                Props.create(RequestActor.class, "Bing", "http://localhost:" + port + "/bing-"));
        googleActor.tell(query, self());
        yandexActor.tell(query, self());
        bingActor.tell(query, self());
        getContext().setReceiveTimeout(timeout);
    }

    private void stopProcessing() {
        parent.tell(jsonObject, self());
        getContext().stop(self());
    }
}
