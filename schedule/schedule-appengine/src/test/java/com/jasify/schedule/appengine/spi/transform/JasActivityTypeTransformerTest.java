package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasActivityTypeTransformerTest {

    private JasActivityTypeTransformer transformer = new JasActivityTypeTransformer();

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
        ActivityType internal = new ActivityType();
        Key orgId = Datastore.allocateId(Organization.class);
        Key id = Datastore.allocateId(orgId, ActivityType.class);
        internal.setId(id);
        internal.setName("activity");
        internal.setDescription("Desc");
        JasActivityType external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals("activity", external.getName());
        assertEquals("Desc", external.getDescription());
        assertEquals(id, KeyUtil.stringToKey(external.getId()));
        assertNotNull(external.getOrganizationId());
        assertEquals(orgId, new JasKeyTransformer().transformFrom(external.getOrganizationId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasActivityType external = new JasActivityType();
        Key id = Datastore.createKey(ActivityType.class, 1);
        external.setId(KeyUtil.keyToString(id));
        external.setName("activity");
        external.setDescription("Desc");
        ActivityType internal = transformer.transformFrom(external);
        assertNotNull(internal);
        assertEquals("activity", internal.getName());
        assertEquals("Desc", internal.getDescription());
        assertEquals(id, internal.getId());
    }
}