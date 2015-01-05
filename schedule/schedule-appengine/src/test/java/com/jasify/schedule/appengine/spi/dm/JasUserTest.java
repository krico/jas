package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static org.junit.Assert.*;

public class JasUserTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasUser.class);
    }

}