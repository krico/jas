package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;

/**
 * @author wszarmach
 * @since 15/03/15.
 */
public class ActivityTypeTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testConstructorWithName() {
        ActivityType activityType = new ActivityType("Type");
        assertEquals("Type", activityType.getName());
        assertEquals("Type", activityType.getLcName());
    }

    @Test
    public void testId() {
        ActivityType activityType = new ActivityType();
        Key id = Datastore.allocateId(ActivityType.class);
        activityType.setId(id);
        assertEquals(id, activityType.getId());
    }

    @Test
    public void testCreated() {
        ActivityType activityType = new ActivityType();
        Date date = new Date();
        activityType.setCreated(date);
        assertEquals(date, activityType.getCreated());
    }

    @Test
    public void testModified() {
        ActivityType activityType = new ActivityType();
        Date date = new Date();
        activityType.setModified(date);
        assertEquals(date, activityType.getModified());
    }

    @Test
    public void testName() {
        ActivityType activityType = new ActivityType();
        activityType.setName("Jumping");
        assertEquals("Jumping", activityType.getName());
    }

    @Test
    public void testLcName() {
        ActivityType activityType = new ActivityType();
        activityType.setLcName("word");
        assertEquals("word", activityType.getLcName());
    }

    @Test
    public void testLcNameDoesNotChangeName() {
        ActivityType activityType = new ActivityType();
        activityType.setName("Test1");
        assertEquals("Test1", activityType.getLcName());
        activityType.setLcName("Test2");
        assertEquals("Test2", activityType.getLcName());
        assertEquals("Test1", activityType.getName());
    }

    @Test
    public void testDescription() {
        ActivityType activityType = new ActivityType();
        activityType.setDescription("Fun");
        assertEquals("Fun", activityType.getDescription());
    }
}
