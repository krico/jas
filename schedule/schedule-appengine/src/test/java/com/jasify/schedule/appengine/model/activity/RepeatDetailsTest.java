package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.*;


/**
 * @author wszarmach
 * @since 09/03/15.
 */
public class RepeatDetailsTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testDefaultRepeatType() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertEquals(RepeatType.No, repeatDetails.getRepeatType());
    }

    @Test
    public void testId() {
        RepeatDetails repeatDetails = new RepeatDetails();
        Key id = Datastore.allocateId(RepeatDetails.class);
        repeatDetails.setId(id);
        assertEquals(id, repeatDetails.getId());
    }

    @Test
    public void testRepeatType() {
        RepeatDetails repeatDetails = new RepeatDetails();
        RepeatType repeatType = RepeatType.Daily;
        repeatDetails.setRepeatType(repeatType);
        assertEquals(repeatType, repeatDetails.getRepeatType());
    }

    @Test
    public void testUntilCount() {
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setUntilCount(10);
        assertEquals(10, repeatDetails.getUntilCount());
    }

    @Test
    public void testUntilDate() {
        RepeatDetails repeatDetails = new RepeatDetails();
        Date untilDate = new Date(20150309);
        repeatDetails.setUntilDate(untilDate);
        assertEquals(untilDate, repeatDetails.getUntilDate());
    }

    @Test
    public void testRepeatUntilType() {
        RepeatDetails repeatDetails = new RepeatDetails();
        RepeatDetails.RepeatUntilType repeatUntilType = RepeatDetails.RepeatUntilType.Count;
        repeatDetails.setRepeatUntilType(repeatUntilType);
        assertEquals(repeatUntilType, repeatDetails.getRepeatUntilType());
    }

    @Test
    public void testRepeatEvery() {
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatEvery(4);
        assertEquals(4, repeatDetails.getRepeatEvery());
    }

    @Test
    public void testMondayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isMondayEnabled());
        repeatDetails.setMondayEnabled(true);
        assertTrue(repeatDetails.isMondayEnabled());
    }

    @Test
    public void testTuesdayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isTuesdayEnabled());
        repeatDetails.setTuesdayEnabled(true);
        assertTrue(repeatDetails.isTuesdayEnabled());
    }

    @Test
    public void testWednesdayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isWednesdayEnabled());
        repeatDetails.setWednesdayEnabled(true);
        assertTrue(repeatDetails.isWednesdayEnabled());
    }

    @Test
    public void testThursdayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isThursdayEnabled());
        repeatDetails.setThursdayEnabled(true);
        assertTrue(repeatDetails.isThursdayEnabled());
    }

    @Test
    public void testFridayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isFridayEnabled());
        repeatDetails.setFridayEnabled(true);
        assertTrue(repeatDetails.isFridayEnabled());
    }

    @Test
    public void testSaturdayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isSaturdayEnabled());
        repeatDetails.setSaturdayEnabled(true);
        assertTrue(repeatDetails.isSaturdayEnabled());
    }

    @Test
    public void testSundayEnabled() {
        RepeatDetails repeatDetails = new RepeatDetails();
        assertFalse(repeatDetails.isSundayEnabled());
        repeatDetails.setSundayEnabled(true);
        assertTrue(repeatDetails.isSundayEnabled());
    }
}
