package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasLoginRequestTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasLoginRequest.class);
    }

}