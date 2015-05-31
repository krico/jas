package com.jasify.schedule.appengine.memcache;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.*;

public class MemcacheTest {

    @Before
    public void setupMemcache() {
        TestHelper.initializeMemcacheWithDatastore();
    }

    @After
    public void cleanupMemcache() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(Memcache.class);
    }

    @Test
    public void testGetAll() {
        Memcache.put("1", "value 1");
        Memcache.put("2", null);
        Memcache.put("3", "value 3");
        List<String> keys = Arrays.asList("1", "2", "3", "4");
        Map<String, String> fetched = Memcache.getAll(keys);
        assertNotNull(fetched);
        assertEquals(3, fetched.size());
        assertEquals("value 1", fetched.get("1"));
        assertNull(fetched.get("2"));
        assertEquals("value 3", fetched.get("3"));
        assertFalse(fetched.containsKey("4"));
    }
}