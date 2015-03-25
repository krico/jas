package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import com.jasify.schedule.appengine.spi.dm.JasUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;

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
        Subscription internal = new Subscription();
        Key id = Datastore.allocateId(Subscription.class);
        internal.setId(id);
        internal.setCreated(new Date());
        internal.getUserRef().setModel(new User("Fred"));
        JasSubscription external = transformer.transformTo(internal);

        assertEquals(KeyFactory.keyToString(id), external.getId());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getUserRef().getModel().getName(), external.getUser().getName());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasSubscription external = new JasSubscription();

        Key id = Datastore.allocateId(Subscription.class);
        external.setId(KeyFactory.keyToString(id));
        external.setCreated(new Date());
        JasUser user = new JasUser();
        user.setName("Bob");
        external.setUser(user);

        Subscription internal = transformer.transformFrom(external);

        assertEquals(id, internal.getId());
        assertEquals(external.getCreated(), internal.getCreated());
        assertEquals(external.getUser().getName(), internal.getUserRef().getModel().getName());
    }
}