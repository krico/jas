package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackage;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasActivityPackageTransformerTest {

    private JasActivityPackageTransformer transformer = new JasActivityPackageTransformer();

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
        ActivityPackage internal = new ActivityPackage();

        internal.setId(Datastore.allocateId(ActivityPackage.class));
        internal.getOrganizationRef().setKey(Datastore.allocateId(Organization.class));
        internal.setValidFrom(new Date());
        internal.setDescription("desc");
        internal.setMaxExecutions(273);
        internal.setCurrency("CHF");
        //TODO: i'm too lazy to test all properties

        JasActivityPackage external = transformer.transformTo(internal);

        assertNotNull(external);
        assertEquals(internal.getId(), KeyUtil.stringToKey(external.getId()));
        assertEquals(internal.getOrganizationRef().getKey(), KeyUtil.stringToKey(external.getOrganizationId()));
        assertEquals(internal.getDescription(), external.getDescription());
        assertEquals(internal.getMaxExecutions(), external.getMaxExecutions());
        assertEquals(internal.getCurrency(), external.getCurrency());
        assertEquals(0, external.getActivityCount());
    }

    @Test
    public void testTransformFrom() throws Exception {
        JasActivityPackage external = new JasActivityPackage();
        external.setId(KeyUtil.keyToString(Datastore.allocateId(ActivityPackage.class)));
        external.setOrganizationId(KeyUtil.keyToString(Datastore.allocateId(Organization.class)));
        external.setPrice(99d);
        //TODO: i'm too lazy to test all properties

        ActivityPackage internal = transformer.transformFrom(external);
        assertEquals(external.getId(), KeyUtil.keyToString(internal.getId()));
        assertEquals(external.getOrganizationId(), KeyUtil.keyToString(internal.getOrganizationRef().getKey()));
        assertEquals(external.getPrice(), internal.getPrice());
    }
}