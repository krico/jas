package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivity;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import com.jasify.schedule.appengine.util.KeyUtil;
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
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        Activity internal = TestHelper.createActivity(activityType, true);

        JasActivity external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals(internal.getDescription(), external.getDescription());
        assertEquals(internal.getCurrency(), external.getCurrency());
        assertEquals(internal.getLocation(), external.getLocation());
        assertEquals(internal.getFinish(), external.getFinish());
        assertEquals(internal.getStart(), external.getStart());
        assertEquals(internal.getMaxSubscriptions(), external.getMaxSubscriptions());
        assertEquals(internal.getPrice(), external.getPrice());
        assertEquals(internal.getSubscriptionCount(), external.getSubscriptionCount());
        assertEquals(internal.getId(), KeyUtil.stringToKey(external.getId()));
        assertNotNull(external.getActivityType());
        assertNotNull(external.getActivityType().getId());
        assertEquals(activityType.getId(), KeyUtil.stringToKey(external.getActivityType().getId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasActivity external = new JasActivity();
        Key id = Datastore.createKey(Activity.class, 1);
        external.setId(KeyUtil.keyToString(id));

        external.setActivityType(new JasActivityType());
        Key atId = Datastore.createKey(ActivityType.class, 99);
        external.getActivityType().setId(KeyUtil.keyToString(atId));
        external.setDescription("Desc");
        Activity internal = transformer.transformFrom(external);
        assertNotNull(internal);
        assertEquals("Desc", internal.getDescription());
        assertEquals(id, internal.getId());
        assertNotNull(internal.getActivityTypeRef().getKey());
        assertEquals(atId, internal.getActivityTypeRef().getKey());
    }
}