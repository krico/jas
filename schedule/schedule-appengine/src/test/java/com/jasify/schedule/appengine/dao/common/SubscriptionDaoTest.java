package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

/**
 * @author szarmawa
 * @since 21/06/15.
 */
public class SubscriptionDaoTest {

    private SubscriptionDao dao;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        dao = new SubscriptionDao();
    }

    @Test
    public void testGetSubscription() throws Exception {
        Subscription subscription = TestHelper.createSubscription(TestHelper.createUser(true), null, true);
        Subscription result = dao.get(subscription.getId());
        assertNotNull(result);
        assertEquals(subscription.getCreated(), result.getCreated());
        assertEquals(subscription.getModified(), result.getModified());
    }

    @Test
    public void testGetByActivityNullKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByActivity(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivityUnknownKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByActivity(Datastore.allocateId(Activity.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivity() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        Map<Key, Subscription> subscriptionMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            Subscription subscription = TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
            subscriptionMap.put(subscription.getId(), subscription);
        }

        List<Subscription> result = dao.getByActivity(activity.getId());
        assertEquals(subscriptionMap.size(), result.size());
        for (int i = 0; i < result.size(); i++) {
            Subscription actual = result.get(i);
            assertTrue(subscriptionMap.containsKey(actual.getId()));
            Subscription expected = subscriptionMap.get(actual.getId());
            assertEquals(expected.getCreated(), actual.getCreated());
            assertEquals(expected.getModified(), actual.getModified());
        }
    }
}
