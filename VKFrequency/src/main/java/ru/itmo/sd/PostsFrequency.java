package ru.itmo.sd;

import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PostsFrequency {
    private static final String API_VERSION = "5.124";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final int WAIT_TIME_MILLISECONDS = 400;
    private long lastRequestTime;

    private UrlRequest requestSender;

    public PostsFrequency(UrlRequest requestSender) {
        this.requestSender = requestSender;
    }

    public ArrayList<Integer> getDiagram(String hashTag, int N) {
        lastRequestTime = Instant.now().getEpochSecond();
        var result = new ArrayList<Integer>();
        for (int i = 1; i <= N; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(WAIT_TIME_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            var response = requestSender.requestResponse(getRequestUrl(hashTag, i));
            var json = new JSONObject(response);
            result.add(json.getJSONObject("response").getInt("count"));
        }
        return result;
    }

    private String getRequestUrl(String hashTag, int N) {
        var start = lastRequestTime - N * 60 * 60;
        var end = start + 60 * 60;
        return "https://api.vk.com/method/newsfeed.search?" +
                "q=" + hashTag + "&" +
                "start_time=" + start + "&" +
                "end_time=" + end + "&" +
                "access_token=" + ACCESS_TOKEN + "&" +
                "v=" + API_VERSION;
    }
}
