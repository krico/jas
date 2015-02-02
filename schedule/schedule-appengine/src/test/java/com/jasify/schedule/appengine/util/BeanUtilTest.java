package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class BeanUtilTest {
    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testCopyProperties() {
        E1 from = new E1();
        String expected = RandomStringUtils.random(20);
        from.setProperty(expected);
        E2 to = new E2();

        BeanUtil.copyProperties(to, from);

        assertEquals(expected, from.getProperty());
        assertEquals(expected, to.getProperty());
    }

    @Test
    public void testCopyPropertiesKeyToString() {
        EKeyId from = new EKeyId();
        Key expectedKey = KeyFactory.createKey("E", 1);

        from.setId(expectedKey);

        EStringId to = new EStringId();

        BeanUtil.copyProperties(to, from);

        assertEquals(expectedKey, from.getId());
        assertNotNull(to.getId());
        assertEquals(expectedKey, KeyFactory.stringToKey(to.getId()));
    }

    @Test
    public void testCopyPropertiesStringToKey() {
        EStringId from = new EStringId();
        Key expectedKey = KeyFactory.createKey("E", 1);
        String expectedString = KeyFactory.keyToString(expectedKey);

        from.setId(expectedString);

        EKeyId to = new EKeyId();

        BeanUtil.copyProperties(to, from);

        assertEquals(expectedString, from.getId());
        assertEquals(expectedKey, to.getId());
    }

    @Test
    public void testCopyPropertiesKeyToKey() {
        EKeyId from = new EKeyId();
        Key expectedKey = KeyFactory.createKey("E", 1);

        from.setId(expectedKey);

        EKeyId to = new EKeyId();

        BeanUtil.copyProperties(to, from);

        assertEquals(expectedKey, to.getId());
    }

    @Test(expected = RuntimeException.class)
    public void testCopyPropertiesPrivateSetter() {
        E1 from = new E1();
        String expected = RandomStringUtils.random(20);
        from.setProperty(expected);
        E3Throws to = new E3Throws();

        BeanUtil.copyProperties(to, from);
    }

    @Test
    public void testCopyPropertiesExcluding() {
        E from = new E();
        from.setSomeString("string");
        from.setSomeDate(new Date());
        from.setSomeInteger(1234);
        from.setSomeboolean(false);
        from.setSomelong(9L);

        E to = new E();
        to.setSomeboolean(true);
        to.setSomeDate(new Date(5));

        BeanUtil.copyPropertiesExcluding(to, from, "someboolean", "someDate");
        assertEquals("string", to.getSomeString());
        assertEquals(new Date(5), to.getSomeDate());
        assertEquals((Integer) 1234, to.getSomeInteger());
        assertEquals(true, to.isSomeboolean());
        assertEquals(9L, to.getSomelong());
    }

    public static class E1 {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    public static class E2 {
        private String property;

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }

    public static class E3Throws {

        public void setProperty(String property) {
            throw new IllegalStateException();
        }
    }

    public static class EKeyId {
        private Key id;

        public Key getId() {
            return id;
        }

        public void setId(Key id) {
            this.id = id;
        }
    }

    public static class EStringId {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class E {
        private String someString;
        private Date someDate;
        private Integer someInteger;
        private boolean someboolean;
        private long somelong;

        public String getSomeString() {
            return someString;
        }

        public void setSomeString(String someString) {
            this.someString = someString;
        }

        public Date getSomeDate() {
            return someDate;
        }

        public void setSomeDate(Date someDate) {
            this.someDate = someDate;
        }

        public Integer getSomeInteger() {
            return someInteger;
        }

        public void setSomeInteger(Integer someInteger) {
            this.someInteger = someInteger;
        }

        public boolean isSomeboolean() {
            return someboolean;
        }

        public void setSomeboolean(boolean someboolean) {
            this.someboolean = someboolean;
        }

        public long getSomelong() {
            return somelong;
        }

        public void setSomelong(long somelong) {
            this.somelong = somelong;
        }
    }
}