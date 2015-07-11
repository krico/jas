package com.jasify.schedule.appengine.dao;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.UniqueConstraintsTest;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class UniqueIndexCacheTest {

    public static final String INDEX_NAME = "TEST_INDEX";

    @BeforeClass
    public static void initialize() {
        TestHelper.setSystemProperties();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void resetCache() {
        UniqueIndexCache.clear();
        TestHelper.initializeDatastore();
        ApplicationData.instance().reload();
        UniqueConstraintsTest.createExampleConstraints();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(UniqueIndexCache.class);
    }

    @Test
    public void testUniqueIndexCachesValues() {
        UniqueIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, false);
        assertNotNull(uniqueIndex);
        UniqueIndex uniqueIndex2 = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, false);
        assertSame(uniqueIndex, uniqueIndex2);
    }

    @Test
    public void testUniqueIndexReserve() throws UniqueConstraintException {
        UniqueIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, false);
        uniqueIndex.reserve("a");
        boolean threw = false;
        try {
            uniqueIndex.reserve("a");
        } catch (UniqueConstraintException e) {
            threw = true;
        }
        assertTrue(threw);
    }

    @Test
    public void testUniqueIndexRelease() throws UniqueConstraintException {
        UniqueIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, false);
        uniqueIndex.reserve("a");
        uniqueIndex.release("a");
        uniqueIndex.release("a");
        uniqueIndex.reserve("a");
    }

    @Test
    public void testUniquePairIndexCachesValues() {
        UniquePairIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, ExampleMeta.get().dataType, false);
        assertNotNull(uniqueIndex);
        UniquePairIndex uniqueIndex2 = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, ExampleMeta.get().dataType, false);
        assertSame(uniqueIndex, uniqueIndex2);
    }

    @Test
    public void testUniquePairIndexReserve() throws UniqueConstraintException {
        UniquePairIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, ExampleMeta.get().dataType, false);
        uniqueIndex.reserve("a", "b");
        boolean threw = false;
        try {
            uniqueIndex.reserve("a", "b");
        } catch (UniqueConstraintException e) {
            threw = true;
        }
        assertTrue(threw);
    }

    @Test
    public void testUniquePairIndexRelease() throws UniqueConstraintException {
        UniquePairIndex uniqueIndex = UniqueIndexCache.get(INDEX_NAME, ExampleMeta.get(), ExampleMeta.get().data, ExampleMeta.get().dataType, false);
        uniqueIndex.reserve("a", "b");
        uniqueIndex.release("a", "b");
        uniqueIndex.reserve("a", "b");
    }
}