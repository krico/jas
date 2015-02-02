package com.jasify.schedule.appengine.client.http;

import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>{@code
 *   @Before
 *   public void setUp() {
 *     testHttpTransportFactory.setUp();
 *   }
 *   @After
 *   public void tearDown() {
 *     testHttpTransportFactory.tearDown();
 *   }
 *   @Test
 *   public void tearDown() {
 *     MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
 *     mockResponse.setContent("Hello TestHttpTransportFactory world!")
 *     testHttpTransportFactor
 *         .expect(HttpMethods.POST, provider.tokenUrl())
 *         .andReturn(mockResponse);
 *     ...
 *  }
 * }</pre>
 *
 * @author krico
 * @since 29/01/15.
 */
public class TestHttpTransportFactory {
    private final Map<String, MockLowLevelHttpResponse> EXPECTATIONS = new HashMap<>();
    private final Map<String, Throwable> THROWABLES = new HashMap<>();

    private final MockHttpTransport mockHttpTransport = new MockHttpTransport() {
        @Override
        public LowLevelHttpRequest buildRequest(String method, String url) throws IOException {
            String key = key(method, url);
            if (THROWABLES.containsKey(key)) {
                Throwable throwable = THROWABLES.get(key);
                Throwables.propagateIfInstanceOf(throwable, IOException.class);
                throw Throwables.propagate(throwable);
            }

            if (EXPECTATIONS.containsKey(key)) {
                MockLowLevelHttpRequest request = new MockLowLevelHttpRequest(url);
                MockLowLevelHttpResponse response = EXPECTATIONS.get(key);
                request.setResponse(response);
                return request;
            }
            throw new IOException("buildRequest(" + method + ", " + url + ")");
        }
    };

    private static String key(String method, String url) {
        return method + '\0' + url;
    }

    public static void cleanup() {
        HttpTransportFactory.setHttpTransport(null);
    }

    public Expectation expect(String method, String url) {
        final String key = key(method, url);
        EXPECTATIONS.put(key, null);
        return new Expectation() {
            @Override
            public void andReturn(MockLowLevelHttpResponse response) {
                EXPECTATIONS.put(key, response);
            }

            @Override
            public void andThrow(Throwable t) {
                THROWABLES.put(key, t);
            }
        };
    }

    public void setUp() {
        HttpTransportFactory.setHttpTransport(mockHttpTransport);
    }

    public void tearDown() {
        cleanup();
    }

    public static interface Expectation {
        void andReturn(MockLowLevelHttpResponse response);

        void andThrow(Throwable t);
    }
}
