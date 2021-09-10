package ru.itmo.sd;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostsFrequencyTest {
    private PostsFrequency mockDiagram;
    private PostsFrequency vk;

    @Mock
    private UrlRequest mockUrlRequest;

    @BeforeAll
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockDiagram = new PostsFrequency(mockUrlRequest);
        vk = new PostsFrequency(new UrlRequest());
    }

    @Test
    void basicFunctionalTest() {
        when(mockUrlRequest.requestResponse(anyString())).thenAnswer((Answer) invocation -> {
            String arg = (String) invocation.getArguments()[0];
            var time = arg.substring(arg.indexOf("start_time") + 11, arg.indexOf("&end_time"));
            return "{ \"response\" : { \"count\" : " + time.substring(3) + ", \"items\" : [] } }";
        });
        var result = mockDiagram.getDiagram("#hashtag", 24);
        assertEquals(24, result.size());
        for (int i = 0; i < 23; i++) {
            assertTrue(result.get(i) > result.get(i + 1));
        }
    }

    @Test
    void simpleIntegrationTest() {
        var result = vk.getDiagram("vk", 1);
        assertEquals(1, result.size());
        System.out.println(result.get(0));
    }

    @Test
    void complexIntegrationTest() {
        var result = vk.getDiagram("spb", 10);
        assertEquals(10, result.size());
        System.out.println(result);
    }
}