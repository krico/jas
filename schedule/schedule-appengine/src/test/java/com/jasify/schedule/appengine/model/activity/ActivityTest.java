package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * @author wszarmach
 * @since 15/03/15.
 */
public class ActivityTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testConstructorWithActivityType() {
        ActivityType activityType = new ActivityType("test");
        Activity activity = new Activity(activityType);
        assertEquals(activityType, activity.getActivityTypeRef().getModel());
        assertEquals("test", activity.getName());
    }

    @Test
    public void testId() {
        Activity activity = new Activity();
        Key id = Datastore.allocateId(Activity.class);
        activity.setId(id);
        assertEquals(id, activity.getId());
    }

    @Test
    public void testCreated() {
        Activity activity = new Activity();
        Date date = new Date();
        activity.setCreated(date);
        assertEquals(date, activity.getCreated());
    }

    @Test
    public void testModified() {
        Activity activity = new Activity();
        Date date = new Date();
        activity.setModified(date);
        assertEquals(date, activity.getModified());
    }

    @Test
    public void testStart() {
        Activity activity = new Activity();
        Date date = new Date();
        activity.setStart(date);
        assertEquals(date, activity.getStart());
    }

    @Test
    public void testFinish() {
        Activity activity = new Activity();
        Date date = new Date();
        activity.setFinish(date);
        assertEquals(date, activity.getFinish());
    }

    @Test
    public void testPrice() {
        Activity activity = new Activity();
        Double price = new Double("12.1");
        activity.setPrice(price);
        assertEquals(price, activity.getPrice());
    }

    @Test
    public void testCurrency() {
        Activity activity = new Activity();
        activity.setCurrency("EUR");
        assertEquals("EUR", activity.getCurrency());
    }

    @Test
    public void testActivityRef() {
        ActivityType activityType = new ActivityType("test");
        Activity activity = new Activity(activityType);
        assertEquals(activityType, activity.getActivityTypeRef().getModel());
    }

    @Test
    public void testRepeatDetailsRef() {
        Activity activity = new Activity();
        RepeatDetails repeatDetails = new RepeatDetails();
        activity.setRepeatDetails(repeatDetails);
        assertEquals(repeatDetails, activity.getRepeatDetailsRef().getModel());
    }

    @Test
    public void testLocation() {
        Activity activity = new Activity();
        activity.setLocation("CH");
        assertEquals("CH", activity.getLocation());
    }

    @Test
    public void testMaxSubscriptions() {
        Activity activity = new Activity();
        activity.setMaxSubscriptions(20);
        assertEquals(20, activity.getMaxSubscriptions());
    }

    @Test
    public void testSubscriptionCount() {
        Activity activity = new Activity();
        activity.setSubscriptionCount(15);
        assertEquals(15, activity.getSubscriptionCount());
    }

    @Test
    public void testName() {
        Activity activity = new Activity();
        activity.setName("Super Stuff");
        assertEquals("Super Stuff", activity.getName());
    }

    @Test
    public void testDescription() {
        Activity activity = new Activity();
        activity.setDescription("Fun");
        assertEquals("Fun", activity.getDescription());
    }

    @Test
    public void testSubscriptionListRef() {
        Activity activity = new Activity();
        assertNotNull(activity.getSubscriptionListRef());
    }
}
