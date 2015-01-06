package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasAddUserRequestTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasAddUserRequest.class);
    }

}