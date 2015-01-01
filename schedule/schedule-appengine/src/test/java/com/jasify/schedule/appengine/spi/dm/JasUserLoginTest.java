package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasUserLoginTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasUserLogin.class);
    }
}