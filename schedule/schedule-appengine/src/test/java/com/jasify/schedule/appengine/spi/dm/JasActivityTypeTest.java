package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasActivityTypeTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasActivityType.class);
    }

}