package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.List;

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
    public void testGetByActivityWithNullKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByActivity(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivityWithUnknownKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByActivity(Datastore.allocateId(Activity.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByActivity() throws Exception {
        Activity activity1 = TestHelper.createActivity(true);
        for (int i = 0; i < 2; i++) {
            TestHelper.createSubscription(TestHelper.createUser(true), activity1, true);
        }

        Activity activity2 = TestHelper.createActivity(true);
        for (int i = 0; i < 3; i++) {
            TestHelper.createSubscription(TestHelper.createUser(true), activity2, true);
        }

        assertEquals(2, dao.getByActivity(activity1.getId()).size());
        assertEquals(3, dao.getByActivity(activity2.getId()).size());
        assertEquals(2, dao.getByActivity(activity1.getId()).size());
    }
}
