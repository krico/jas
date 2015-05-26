package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JasActivityTypeTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasActivityType.class);
    }

    @Test
    public void testColourTag() {
        String colourTag = "test";
        JasActivityType jasActivityType = new JasActivityType();
        assertNull(jasActivityType.getColourTag());
        jasActivityType.setColourTag(colourTag);
        assertEquals(colourTag, jasActivityType.getColourTag());
    }
}