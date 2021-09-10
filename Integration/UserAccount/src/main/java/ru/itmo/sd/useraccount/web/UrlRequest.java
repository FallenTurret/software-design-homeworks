package ru.itmo.sd.useraccount.web;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UrlRequest {
    private final HttpClient client;

    public UrlRequest() {
        client = HttpClient.newHttpClient();
    }

    public String requestResponse(String request) {
        var httpRequest = HttpRequest.newBuilder(URI.create(request)).build();
        HttpResponse<String> response;
        try {
            response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            return requestResponse(request);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return response.body();
    }
}
