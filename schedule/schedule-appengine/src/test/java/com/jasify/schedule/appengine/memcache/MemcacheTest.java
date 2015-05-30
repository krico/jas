package com.jasify.schedule.appengine.memcache;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

public class MemcacheTest {

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(Memcache.class);
    }
}