package com.jasify.schedule.appengine.util;

import com.google.common.base.Function;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class LogUtilTest {

    @Test
    public void wellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(LogUtil.class);
    }

    @Test
    public void testToLazyString() {
        Integer data = 1;
        final AtomicInteger calls = new AtomicInteger(0);
        Function<Integer, StringBuilder> fn = new Function<Integer, StringBuilder>() {
            @Nullable
            @Override
            public StringBuilder apply(Integer input) {
                calls.incrementAndGet();
                return new StringBuilder().append(input);
            }
        };
        LogUtil.Lazy<Integer, StringBuilder> lazy = LogUtil.toLazyString(fn, data);
        assertNotNull(lazy);
        assertEquals(0, calls.get());
        for (int i = 0; i < 10; ++i) {
            assertEquals(data.toString(), lazy.toString());
            assertEquals("should cache value", 1, calls.get());
            assertEquals(data.toString(), lazy.toSequence().toString());
            assertEquals("should cache value", 1, calls.get());
        }

    }
}