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
        internal.getOrganizationRef().setKey(orgId);
        internal.setName("activity");
        internal.setDescription("Desc");
        internal.setPrice(12.0);
        internal.setCurrency("NZD");
        internal.setLocation("Location");
        internal.setMaxSubscriptions(5);
        JasActivityType external = transformer.transformTo(internal);
        assertNotNull(external);
        assertEquals("activity", external.getName());
        assertEquals("Desc", external.getDescription());
        assertEquals(12.0, external.getPrice());
        assertEquals("NZD", external.getCurrency());
        assertEquals("Location", external.getLocation());
        assertEquals("TimeZone", external.getTimeZone());
        assertEquals(5, external.getMaxSubscriptions());
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
        external.setPrice(12.0);
        external.setCurrency("NZD");
        external.setLocation("Location");
        external.setTimeZone("TimeZone");
        external.setMaxSubscriptions(5);
        ActivityType internal = transformer.transformFrom(external);
        assertNotNull(internal);
        assertEquals("activity", internal.getName());
        assertEquals("Desc", internal.getDescription());
        assertEquals(12.0, internal.getPrice());
        assertEquals("NZD", internal.getCurrency());
        assertEquals("Location", internal.getLocation());
        assertEquals(5, internal.getMaxSubscriptions());
        assertEquals(id, internal.getId());
    }
}