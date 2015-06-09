package com.jasify.schedule.appengine.dao;

import com.google.api.client.util.Maps;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.common.base.Optional;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.TestCase.*;

public class DaoUtilTest {

    public static final ExampleMeta META = ExampleMeta.get();

    static Example createExample() {
        return TestHelper.populateBean(Example.class, "id");
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

    @Test
    public void testMarshallAndUnMarshallALLMemcacheEntries() {
        Example example1 = createExample();
        example1.setId(Datastore.allocateId(Example.class));
        Example example2 = createExample();
        example2.setId(Datastore.allocateId(Example.class));

        Map<Key, Example> fetchedEmpty = DaoUtil.cacheGet(Arrays.asList(example1.getId(), example2.getId()), META);
        assertTrue(fetchedEmpty.isEmpty());

        Map<Key, Example> cache = Maps.newHashMap();
        cache.put(example1.getId(), example1);
        cache.put(example2.getId(), example2);
        DaoUtil.cachePutAll(cache, META);
        assertEquals(example1, DaoUtil.cacheGet(example1.getId(), META));
        assertEquals(example2, DaoUtil.cacheGet(example2.getId(), META));

        Map<Key, Example> fetched = DaoUtil.cacheGet(Arrays.asList(example1.getId(), example2.getId()), META);
        assertNotNull(fetched);
        assertEquals(2, fetched.size());
        assertEquals(example1, fetched.get(example1.getId()));
        assertEquals(example2, fetched.get(example2.getId()));
    }

    //    @Ignore("This test takes too long, but proves the point")
//    @Test
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