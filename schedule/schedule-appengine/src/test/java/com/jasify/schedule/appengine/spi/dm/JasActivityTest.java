package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static org.junit.Assert.*;

public class JasActivityTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasActivity.class);
    }

}