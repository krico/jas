package com.jasify.schedule.appengine.client.http;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;

/**
 * @author krico
 * @since 29/01/15.
 */
public class HttpTransportFactory {
    private static HttpTransport httpTransport;

    public static HttpTransport getHttpTransport() {
        if (httpTransport == null) {
            httpTransport = new UrlFetchTransport();
        }
        return httpTransport;
    }

    static void setHttpTransport(HttpTransport transport) {
        httpTransport = transport;
    }
}
