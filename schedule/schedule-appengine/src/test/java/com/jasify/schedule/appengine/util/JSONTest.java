package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import java.io.StringReader;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JSONTest {
    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(JSON.class);
    }

    @Test
    public void testFromToJsonString() {
        E e = new E();
        e.k = "k";
        e.v = "v";
        String str = JSON.toJson(e);
        assertNotNull(str);
        E e1 = JSON.fromJson(str, E.class);
        assertEquals(e.k, e1.k);
        assertEquals(e.v, e1.v);
    }

    @Test
    public void testFromToJsonReader() {
        E e = new E();
        e.k = "k";
        e.v = "v";
        String str = JSON.toJson(e);
        assertNotNull(str);
        E e1 = JSON.fromJson(new StringReader(str), E.class);
        assertEquals(e.k, e1.k);
        assertEquals(e.v, e1.v);
    }

    public static class E {
        private String k;
        private String v;
    }

}