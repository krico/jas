package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wszarmach
 * @since 03/09/15.
 */
public class JasUserSubscriptionTest {

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasUserSubscription.class);
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        JasUserSubscription userSubscription = new JasUserSubscription();
        assertNull(userSubscription.getSubscription());
        assertNull(userSubscription.getActivity());
        assertFalse(userSubscription.isPaid());
    }

    @Test
    public void testConvenienceConstructor() throws Exception {
        Subscription subscription = TestHelper.createSubscription(true);
        JasUserSubscription userSubscription = new JasUserSubscription(subscription);
        assertNotNull(userSubscription.getSubscription());
        assertNotNull(userSubscription.getActivity());
        assertEquals(KeyUtil.keyToString(subscription.getId()), userSubscription.getSubscription().getId());
        assertEquals(KeyUtil.keyToString(subscription.getActivityRef().getKey()), userSubscription.getActivity().getId());
        assertFalse(userSubscription.isPaid());
    }

    @Test
    public void testIsPaid() throws Exception {
        Subscription subscription = TestHelper.createSubscription(true);
        JasUserSubscription userSubscription = new JasUserSubscription(subscription);
        userSubscription.setPaid(true);
        assertTrue(userSubscription.isPaid());
    }

    @Test
    public void testSubscription() throws Exception {
        Subscription subscription = TestHelper.createSubscription(true);
        JasUserSubscription userSubscription = new JasUserSubscription(subscription);
        userSubscription.setSubscription(null);
        assertNull(userSubscription.getSubscription());
    }

    @Test
    public void testActivity() throws Exception {
        Subscription subscription = TestHelper.createSubscription(true);
        JasUserSubscription userSubscription = new JasUserSubscription(subscription);
        userSubscription.setActivity(null);
        assertNull(userSubscription.getActivity());
    }
}
