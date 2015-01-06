package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

public class JasUserListTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasUserList.class);
    }
}