package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 15/03/15.
 */
public class SubscriptionTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testId() {
        Subscription subscription = new Subscription();
        Key id = Datastore.allocateId(Subscription.class);
        subscription.setId(id);
        assertEquals(id, subscription.getId());
    }

    @Test
    public void testCreated() {
        Subscription subscription = new Subscription();
        Date date = new Date();
        subscription.setCreated(date);
        assertEquals(date, subscription.getCreated());
    }

    @Test
    public void testModified() {
        Subscription subscription = new Subscription();
        Date date = new Date();
        subscription.setModified(date);
        assertEquals(date, subscription.getModified());
    }

    @Test
    public void testActivityRef() {
        Subscription subscription = new Subscription();
        assertNotNull(subscription.getActivityRef());
    }

    @Test
    public void testUserRef() {
        Subscription subscription = new Subscription();
        assertNotNull(subscription.getUserRef());
    }

    @Test
    public void testPaymentRef() {
        Subscription subscription = new Subscription();
        assertNotNull(subscription.getPaymentRef());
    }
}
