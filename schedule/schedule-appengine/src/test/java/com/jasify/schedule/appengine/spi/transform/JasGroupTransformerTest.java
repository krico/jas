package com.jasify.schedule.appengine.spi.transform;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.spi.dm.JasGroup;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;

public class JasGroupTransformerTest {
    private JasGroupTransformer transformer = new JasGroupTransformer();

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
        Key id = Datastore.allocateId(Group.class);

        Group internal = new Group("Some Name");
        internal.setId(id);
        internal.setDescription("Some description");

        JasGroup external = transformer.transformTo(internal);

        assertEquals("Some Name", external.getName());
        assertEquals("Some description", external.getDescription());
        assertEquals(id, KeyUtil.stringToKey(external.getId()));
    }

    @Test
    public void testTransformFrom() throws Exception {
        Key id = Datastore.allocateId(Group.class);

        JasGroup external = new JasGroup();
        external.setName("Some Name");
        external.setDescription("Some description");
        external.setId(KeyUtil.keyToString(id));

        Group internal = transformer.transformFrom(external);

        assertEquals("Some Name", internal.getName());
        assertEquals("Some description", internal.getDescription());
        assertEquals(id, internal.getId());

    }
}