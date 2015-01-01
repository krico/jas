package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasChangePasswordRequestTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasChangePasswordRequest.class);
    }
}