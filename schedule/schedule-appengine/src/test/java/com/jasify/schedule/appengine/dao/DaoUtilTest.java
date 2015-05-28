package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.TestHelper;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class DaoUtilTest {

    public static final ExampleMeta META = ExampleMeta.get();

    static Example createExample() {
        Populator populator = new PopulatorBuilder().build();
        return populator.populateBean(Example.class, "id");
    }

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void isItWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(DaoUtil.class);
    }

    @Test
    public void testMarshallAndUnMarshallMemcacheEntries() {
        Example example = createExample();
        example.setId(Datastore.allocateId(Example.class));
        Example ret = DaoUtil.cachePut(example.getId(), META, example);
        assertSame(example, ret);
        Example fetched = DaoUtil.cacheGet(example.getId(), META);
        assertEquals(example, fetched);

    }

    @Ignore("This test takes too long, but proves the point")
    @Test
    public void testPutWithExpiration() throws Exception {
        final int milliDelay = 3000;
        Example example = createExample();
        example.setId(Datastore.allocateId(Example.class));
        Example ret = DaoUtil.cachePut(example.getId(), META, example, Expiration.byDeltaMillis(milliDelay));
        assertSame(example, ret);
        Example fetched = DaoUtil.cacheGet(example.getId(), META);
        assertEquals(example, fetched);
        Thread.sleep(milliDelay + 100);
        assertNull(DaoUtil.cacheGet(example.getId(), META));
    }

    @Test
    public void testCacheGetOrNullReturnsNull() {
        assertNull(DaoUtil.cacheGetOrNull(Datastore.allocateId(Example.class), META));
    }

    @Test
    public void testCacheGetOrNullReturnsAbsent() {
        Key id = Datastore.allocateId(Example.class);
        DaoUtil.cachePut(id, META, null);
        Optional<Example> optional = DaoUtil.cacheGetOrNull(id, META);
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @Test
    public void testCacheGetOrNullReturnsObject() {
        Key id = Datastore.allocateId(Example.class);
        Example example = createExample();
        example.setId(id);
        DaoUtil.cachePut(id, META, example);
        Optional<Example> optional = DaoUtil.cacheGetOrNull(id, META);
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(example, optional.get());
    }
}