package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Entity;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ModelMetadataUtilTest {

    @Before
    public void setupDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(ModelMetadataUtil.class);
    }

    @Test
    public void testQueryAllKinds() throws IOException {
        Set<String> allKinds = ModelMetadataUtil.queryAllKinds();
        assertTrue(allKinds.isEmpty());
        List<Entity> toPut = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            toPut.add(new Entity("K1", "i" + i));
        }
        Datastore.put(toPut);

        allKinds = ModelMetadataUtil.queryAllKinds();
        assertEquals(1, allKinds.size());
        assertTrue(allKinds.contains("K1"));

        List<Entity> toPut2 = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            toPut2.add(new Entity("K2", "i" + i));
        }
        Datastore.put(toPut2);

        allKinds = ModelMetadataUtil.queryAllKinds();
        assertEquals(2, allKinds.size());
        assertTrue(allKinds.contains("K1"));
        assertTrue(allKinds.contains("K2"));

        List<Entity> toPut3 = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            toPut3.add(new Entity("M1", "i" + i));
        }
        Datastore.put(toPut3);

        allKinds = ModelMetadataUtil.queryAllKinds();
        assertEquals(3, allKinds.size());
        assertTrue(allKinds.contains("K1"));
        assertTrue(allKinds.contains("K2"));
        assertTrue(allKinds.contains("M1"));

        ModelMetadataUtil.dumpDb(System.out);
    }

    @Test
    public void testQueryKindsByPrefix() throws IOException {
        testQueryAllKinds();
        Set<String> keys = ModelMetadataUtil.queryKindsThatStartWith("K");
        assertEquals(2, keys.size());
        assertTrue(keys.contains("K1"));
        assertTrue(keys.contains("K2"));

        keys = ModelMetadataUtil.queryKindsThatStartWith("M");
        assertEquals(1, keys.size());
        assertTrue(keys.contains("M1"));

        keys = ModelMetadataUtil.queryKindsThatStartWith("K1");
        assertEquals(1, keys.size());
        assertTrue(keys.contains("K1"));

    }
}