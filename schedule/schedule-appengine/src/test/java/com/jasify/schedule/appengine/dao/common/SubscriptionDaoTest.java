package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
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
        Subscription subscription = TestHelper.createSubscription(TestHelper.createUser(true), TestHelper.createActivity(true), true);
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

    @Test
    public void testGetByUserWithNullKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByUser(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByUserWithUnknownKey() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        TestHelper.createSubscription(TestHelper.createUser(true), activity, true);
        List<Subscription> result = dao.getByUser(Datastore.allocateId(User.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetByUser() throws Exception {
        User user1 = TestHelper.createUser(true);
        for (int i = 0; i < 2; i++) {
            TestHelper.createSubscription(user1, TestHelper.createActivity(true), true);
        }

        User user2 = TestHelper.createUser(true);
        for (int i = 0; i < 3; i++) {
            TestHelper.createSubscription(user2, TestHelper.createActivity(true), true);
        }

        assertEquals(2, dao.getByUser(user1.getId()).size());
        assertEquals(3, dao.getByUser(user2.getId()).size());
        assertEquals(2, dao.getByUser(user1.getId()).size());
    }

    @Test
    public void testSave() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        User user = TestHelper.createUser(true);
        Subscription subscription = TestHelper.createSubscription(user, activity, false);
        subscription.getUserRef().setKey(null);

        Key subscriptionId = dao.save(subscription, user.getId());
        assertNotNull(subscriptionId);
        Subscription saveSubscription = dao.get(subscriptionId);
        assertNotNull(saveSubscription);
        assertEquals(user.getId(), saveSubscription.getUserRef().getKey());
    }

    @Test
    public void testSaveList() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        User user = TestHelper.createUser(true);

        List<Subscription> subscriptions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            subscriptions.add(TestHelper.createSubscription(user, activity, false));
        }

        List<Key> subscriptionIds = dao.save(subscriptions);
        assertNotNull(subscriptionIds);
        assertEquals(subscriptions.size(), subscriptionIds.size());
    }
}
