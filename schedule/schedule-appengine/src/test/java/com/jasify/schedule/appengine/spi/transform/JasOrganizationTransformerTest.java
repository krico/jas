package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.dm.JasOrganization;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;

public class JasOrganizationTransformerTest {
    private JasOrganizationTransformer transformer = new JasOrganizationTransformer();

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
        Key id = Datastore.allocateId(Organization.class);

        Organization internal = new Organization("Some Name");
        internal.setId(id);
        internal.setDescription("Some description");

        JasOrganization external = transformer.transformTo(internal);

        assertEquals("Some Name", external.getName());
        assertEquals("Some description", external.getDescription());
        assertEquals(id, KeyFactory.stringToKey(external.getId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        Key id = Datastore.allocateId(Organization.class);

        JasOrganization external = new JasOrganization();
        external.setName("Some Name");
        external.setDescription("Some description");
        external.setId(KeyFactory.keyToString(id));

        Organization internal = transformer.transformFrom(external);

        assertEquals("Some Name", internal.getName());
        assertEquals("Some description", internal.getDescription());
        assertEquals(id, internal.getId());

    }
}