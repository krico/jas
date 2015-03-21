package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wszarmach
 * @since 16/03/15.
 */
public class JasRepeatDetailsTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasRepeatDetails.class);
    }

    @Test
    public void testId() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertNull(jasRepeatDetails.getId());
        jasRepeatDetails.setId("test");
        assertEquals("test", jasRepeatDetails.getId());
    }

    @Test
    public void testRepeatType() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertNull(jasRepeatDetails.getRepeatType());
        jasRepeatDetails.setRepeatType(RepeatType.Daily);
        assertEquals(RepeatType.Daily, jasRepeatDetails.getRepeatType());
    }

    @Test
    public void testRepeatUntilType() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertNull(jasRepeatDetails.getRepeatUntilType());
        jasRepeatDetails.setRepeatUntilType(RepeatUntilType.Count);
        assertEquals(RepeatUntilType.Count, jasRepeatDetails.getRepeatUntilType());
    }

    @Test
    public void testRepeatEvery() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertEquals(0, jasRepeatDetails.getRepeatEvery());
        jasRepeatDetails.setRepeatEvery(2);
        assertEquals(2, jasRepeatDetails.getRepeatEvery());
    }

    @Test
    public void testUntilCount() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertEquals(0, jasRepeatDetails.getUntilCount());
        jasRepeatDetails.setUntilCount(2);
        assertEquals(2, jasRepeatDetails.getUntilCount());
    }

    @Test
    public void testUntilDate() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertNull(jasRepeatDetails.getUntilDate());
        jasRepeatDetails.setUntilDate(new DateTime(2015, 3, 16, 22, 50).toDate());
        assertEquals(new DateTime(2015, 3, 16, 22, 50).toDate(), jasRepeatDetails.getUntilDate());
    }

    @Test
    public void testMondayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isMondayEnabled());
        jasRepeatDetails.setMondayEnabled(true);
        assertTrue(jasRepeatDetails.isMondayEnabled());
    }

    @Test
    public void testTuesdayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isTuesdayEnabled());
        jasRepeatDetails.setTuesdayEnabled(true);
        assertTrue(jasRepeatDetails.isTuesdayEnabled());
    }

    @Test
    public void testWednesdayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isWednesdayEnabled());
        jasRepeatDetails.setWednesdayEnabled(true);
        assertTrue(jasRepeatDetails.isWednesdayEnabled());
    }

    @Test
    public void testThursdayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isThursdayEnabled());
        jasRepeatDetails.setThursdayEnabled(true);
        assertTrue(jasRepeatDetails.isThursdayEnabled());
    }

    @Test
    public void testFridayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isFridayEnabled());
        jasRepeatDetails.setFridayEnabled(true);
        assertTrue(jasRepeatDetails.isFridayEnabled());
    }

    @Test
    public void testSaturdayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isSaturdayEnabled());
        jasRepeatDetails.setSaturdayEnabled(true);
        assertTrue(jasRepeatDetails.isSaturdayEnabled());
    }

    @Test
    public void testSundayEnabled() {
        JasRepeatDetails jasRepeatDetails = new JasRepeatDetails();
        assertFalse(jasRepeatDetails.isSundayEnabled());
        jasRepeatDetails.setSundayEnabled(true);
        assertTrue(jasRepeatDetails.isSundayEnabled());
    }
}
