package ru.itmo.sd.actors;

import akka.actor.UntypedAbstractActor;
import org.json.JSONObject;
import ru.itmo.sd.web.UrlRequest;

public class RequestActor extends UntypedAbstractActor {
    private final String searchString;
    private final String searchEngine;

    public RequestActor(String searchEngine, String searchString) {
        super();
        this.searchEngine = searchEngine;
        this.searchString = searchString;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            var result = new UrlRequest().requestResponse(searchString + message);
            var json = new JSONObject();
            json.put(searchEngine, new JSONObject(result).getJSONArray("results"));
            sender().tell(json, self());
        }
    }
}
