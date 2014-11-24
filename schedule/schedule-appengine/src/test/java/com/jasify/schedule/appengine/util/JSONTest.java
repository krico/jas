package com.jasify.schedule.appengine.util;

import com.google.gson.reflect.TypeToken;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JSONTest {
    private static final TypeToken<List<E>> TYPE_TOKEN = new TypeToken<List<E>>() {
    };

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
        StringWriter writer = new StringWriter();
        JSON.toJson(writer, e);
        E e1 = JSON.fromJson(new StringReader(writer.toString()), E.class);
        assertEquals(e.k, e1.k);
        assertEquals(e.v, e1.v);
    }

    @Test
    public void testFromToJsonTypedString() {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            E e = new E();
            e.k = "k";
            e.v = "v" + i;
            list.add(e);
        }
        StringWriter writer = new StringWriter();
        JSON.toJson(writer, list);
        List<E> eList = JSON.fromJson(writer.toString(), TYPE_TOKEN.getType());
        assertNotNull(eList);
        assertEquals(list.size(), eList.size());

        for (int i = 0; i < eList.size(); ++i) {
            assertEquals(list.get(i), eList.get(i));
        }
    }

    @Test
    public void testFromToJsonTypedReader() {
        List<E> list = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            E e = new E();
            e.k = "k";
            e.v = "v" + i;
            list.add(e);
        }
        StringWriter writer = new StringWriter();
        JSON.toJson(writer, list);
        List<E> eList = JSON.fromJson(new StringReader(writer.toString()), TYPE_TOKEN.getType());
        assertNotNull(eList);
        assertEquals(list.size(), eList.size());

        for (int i = 0; i < eList.size(); ++i) {
            assertEquals(list.get(i), eList.get(i));
        }
    }

    public static class E {
        private String k;
        private String v;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            E e = (E) o;

            if (k != null ? !k.equals(e.k) : e.k != null) return false;
            if (v != null ? !v.equals(e.v) : e.v != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = k != null ? k.hashCode() : 0;
            result = 31 * result + (v != null ? v.hashCode() : 0);
            return result;
        }
    }

}