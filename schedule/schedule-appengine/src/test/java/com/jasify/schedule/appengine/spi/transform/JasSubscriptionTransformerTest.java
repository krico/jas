package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.spi.dm.JasSubscription;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;

public class JasSubscriptionTransformerTest {
    private JasSubscriptionTransformer transformer = new JasSubscriptionTransformer();

    @BeforeClass
    public static void datastore() {
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

        JasSubscription external = transformer.transformTo(internal);

        assertEquals(KeyFactory.keyToString(id), external.getId());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasSubscription external = new JasSubscription();

        Key id = Datastore.allocateId(Subscription.class);
        external.setId(KeyFactory.keyToString(id));

        Subscription internal = transformer.transformFrom(external);

        assertEquals(id, internal.getId());


    }
}