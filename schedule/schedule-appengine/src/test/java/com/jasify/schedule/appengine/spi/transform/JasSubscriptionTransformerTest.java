package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.spi.dm.JasUser;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class JasSubscriptionTransformerTest {
    private JasSubscriptionTransformer transformer = new JasSubscriptionTransformer();

    @BeforeClass
    public static void initialise() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testTransformTo() throws Exception {
        User user = TestHelper.createUser(true);
        Activity activity = TestHelper.createActivity(true);
        Subscription internal =  TestHelper.createSubscription(user, activity, true);
        JasSubscription external = transformer.transformTo(internal);

        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getUserRef().getModel().getName(), external.getUser().getName());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasSubscription external = new JasSubscription();

        Key id = Datastore.allocateId(Subscription.class);
        external.setId(KeyUtil.keyToString(id));
        external.setCreated(new Date());
        JasUser user = new JasUser();
        user.setName("Bob");
        external.setUser(user);

        Subscription internal = transformer.transformFrom(external);

        assertEquals(id, internal.getId());
        assertNull(internal.getCreated());
        assertEquals(external.getUser().getName(), internal.getUserRef().getModel().getName());
    }
}