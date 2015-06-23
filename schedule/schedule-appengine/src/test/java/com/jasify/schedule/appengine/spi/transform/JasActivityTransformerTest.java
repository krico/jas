package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
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
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Activity internal = TestHelper.createActivity(activityType, true);

        JasActivity external = transformer.transformTo(internal);
        assertNotNull(external);
        assertNotNull(external.getActivityType());
        assertEquals(activityType.getId(), KeyUtil.stringToKey(external.getActivityType().getId()));
        assertEquals(internal.getCurrency(), external.getCurrency());
        assertEquals(internal.getDescription(), external.getDescription());
        assertEquals(internal.getFinish(), external.getFinish());
        assertEquals(internal.getId(), KeyUtil.stringToKey(external.getId()));
        assertEquals(internal.getLocation(), external.getLocation());
        assertEquals(internal.getMaxSubscriptions(), external.getMaxSubscriptions());
        assertEquals(internal.getPrice(), external.getPrice());
        assertEquals(internal.getStart(), external.getStart());
        assertEquals(internal.getSubscriptionCount(), external.getSubscriptionCount());
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