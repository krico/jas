package com.jasify.schedule.appengine.client.http;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class HttpTransportFactoryTest {

    @Test
    public void testGetHttpTransport() throws Exception {
        assertNotNull(HttpTransportFactory.getHttpTransport());
    }
}