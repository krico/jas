package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static org.junit.Assert.*;

public class JasLoginResponseTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasLoginResponse.class);
    }

}