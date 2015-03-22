package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.spi.JasifyEndpoint;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JasApiInfoTest {

    @Test
    public void testGetVersion() throws Exception {
        JasApiInfo apiInfo = new JasApiInfo(new JasifyEndpoint());
        assertEquals("v1", apiInfo.getApiVersion());
    }
}