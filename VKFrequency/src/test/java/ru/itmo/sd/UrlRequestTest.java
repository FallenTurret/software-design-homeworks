package ru.itmo.sd;

import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.startsWithUri;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrlRequestTest {
    private static StubServer server;
    private static int PORT;
    private UrlRequest requestSender = new UrlRequest();

    @BeforeAll
    static void setUp() {
        server = new StubServer().secured().run();
        PORT = server.getPort();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    public void correctlyReactsToError() {
        whenHttp(server)
                .match(method(Method.GET), startsWithUri("/ping"))
                .then(status(HttpStatus.NOT_FOUND_404));

        assertThrows(UncheckedIOException.class, () -> requestSender.requestResponse("http://localhost:" + PORT + "/ping"));
    }
}