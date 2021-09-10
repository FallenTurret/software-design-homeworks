package ru.itmo.sd;

import com.xebialabs.restito.server.StubServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.glassfish.grizzly.http.util.HttpStatus.OK_200;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchAggregatorTest {
    private static StubServer server;
    private static int PORT;

    @BeforeAll
    static void setUp() {
        server = new StubServer().run();
        PORT = server.getPort();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    public void retrievesAllInformation() {
        whenHttp(server)
                .match(get("/google-irrelevant_query"))
                .then(status(OK_200), contentType("application/json"), stringContent(getJSON("qwert")))
                .mustHappen();
        whenHttp(server)
                .match(get("/yandex-irrelevant_query"))
                .then(status(OK_200), contentType("application/json"), stringContent(getJSON("asdfg")))
                .mustHappen();
        whenHttp(server)
                .match(get("/bing-irrelevant_query"))
                .then(status(OK_200), contentType("application/json"), stringContent(getJSON("zxcvb")))
                .mustHappen();

        var res = SearchAggregator.search("irrelevant_query", Duration.ofDays(1), PORT);
        assertEquals(getJSONArray("qwert").toString(), res.getJSONArray("Google").toString());
        assertEquals(getJSONArray("asdfg").toString(), res.getJSONArray("Yandex").toString());
        assertEquals(getJSONArray("zxcvb").toString(), res.getJSONArray("Bing").toString());
    }

    @Test
    public void retrievesInformationFromTwoSources() {
        whenHttp(server)
                .match(get("/google-irrelevant_query"))
                .then(status(OK_200), contentType("application/json"), stringContent(getJSON("qwert")))
                .mustHappen();
        whenHttp(server)
                .match(get("/yandex-irrelevant_query"))
                .then(status(OK_200), contentType("application/json"), stringContent(getJSON("asdfg")))
                .mustHappen();
        whenHttp(server)
                .match(get("/bing-irrelevant_query"))
                .then(delay(2000), status(OK_200), contentType("application/json"), stringContent(getJSON("zxcvb")))
                .mustHappen();

        var res = SearchAggregator.search("irrelevant_query", Duration.ofSeconds(1), PORT);
        assertEquals(getJSONArray("qwert").toString(), res.getJSONArray("Google").toString());
        assertEquals(getJSONArray("asdfg").toString(), res.getJSONArray("Yandex").toString());
        assertEquals(2, res.length());
    }

    private static String getJSON(String a) {
        var res = new JSONObject();
        var list = getJSONArray(a);
        res.put("results", list);
        return res.toString();
    }

    private static JSONArray getJSONArray(String a) {
        return new JSONArray(new String[] {
                String.valueOf(a.charAt(0)),
                String.valueOf(a.charAt(1)),
                String.valueOf(a.charAt(2)),
                String.valueOf(a.charAt(3)),
                String.valueOf(a.charAt(4))
        });
    }
}