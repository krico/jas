package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivity;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasActivityTransformerTest {
    private JasActivityTransformer transformer = new JasActivityTransformer();

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
        Activity internal = new Activity();
        Key id = Datastore.createKey(Activity.class, 1);
        internal.setId(id);
        internal.setDescription("Desc");
        ActivityType at = new ActivityType();
        at.setId(Datastore.createKey(ActivityType.class, 9));
        internal.getActivityTypeRef().setModel(at);

        JasActivity external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals("Desc", external.getDescription());
        assertEquals(id, KeyFactory.stringToKey(external.getId()));
        assertNotNull(external.getActivityType());
        assertNotNull(external.getActivityType().getId());
        assertEquals(at.getId(), KeyFactory.stringToKey(external.getActivityType().getId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasActivity external = new JasActivity();
        Key id = Datastore.createKey(Activity.class, 1);
        external.setId(KeyFactory.keyToString(id));

        external.setActivityType(new JasActivityType());
        Key atId = Datastore.createKey(ActivityType.class, 99);
        external.getActivityType().setId(KeyFactory.keyToString(atId));
        external.setDescription("Desc");
        Activity internal = transformer.transformFrom(external);
        assertNotNull(internal);
        assertEquals("Desc", internal.getDescription());
        assertEquals(id, internal.getId());
        assertNotNull(internal.getActivityTypeRef().getKey());
        assertEquals(atId, internal.getActivityTypeRef().getKey());
    }
}