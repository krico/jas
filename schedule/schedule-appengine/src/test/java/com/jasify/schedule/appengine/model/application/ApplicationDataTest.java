package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

public class ApplicationDataTest {
    private static final Logger log = LoggerFactory.getLogger(ApplicationDataTest.class);

    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testInstance() {
        ApplicationData instance = ApplicationData.instance();
        assertNotNull(instance);
        assertEquals(instance, ApplicationData.instance());
        assertTrue(instance == ApplicationData.instance());
    }

    @Test
    public void testGetDefaultPropertiesForString() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertNull(instance.getProperty("Test1"));
        assertEquals("TestResult", instance.getPropertyWithDefaultValue("Test1", "TestResult"));
        assertEquals("TestResult", instance.getProperty("Test1"));
    }

    @Test
    public void testGetDefaultPropertiesForInteger() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertNull(instance.getProperty("Test1"));
        assertEquals(15, (int) instance.getPropertyWithDefaultValue("Test1", 15));
        assertEquals(15, instance.getProperty("Test1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultPropertiesForDouble() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertNull(instance.getProperty("Test1"));
        instance.getPropertyWithDefaultValue("Test1", 15.8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDefaultPropertiesForFloat() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertNull(instance.getProperty("Test1"));
        instance.getPropertyWithDefaultValue("Test1", 15f);
    }

    @Test
    public void testGetDefaultPropertiesForLong() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertEquals(15l, (long) instance.getPropertyWithDefaultValue("Test1", 15l));
        assertEquals(15l, instance.getProperty("Test1"));
    }

    @Test
    public void testGetDefaultPropertiesForBoolean() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertEquals(Boolean.TRUE, instance.getPropertyWithDefaultValue("Test1", Boolean.TRUE));
        assertEquals(Boolean.TRUE, instance.getProperty("Test1"));
    }

    @Test
    public void testGetDefaultPropertiesForNull() {
        ApplicationData instance = ApplicationData.instance().reload();
        assertNull(instance.getPropertyWithDefaultValue("Test1", null));
        assertNull(instance.getProperty("Test1"));
    }

    @Test
    public void testGetSetProperties() {
        ApplicationData instance = ApplicationData.instance().reload();
        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");
        assertNull(instance.getProperty("String"));
        assertNull(instance.getProperty("Boolean"));
        assertNull(instance.getProperty("Integer"));
        assertNull(instance.getProperty("Long"));
        assertNull(instance.getProperty("Text"));
        assertNull(instance.getProperty("Blob"));
        assertNull(instance.getProperty("List"));

        instance.setProperty("String", "A String");
        instance.setProperty("Boolean", true);
        instance.setProperty("Integer", 1);
        instance.setProperty("Long", 1L);
        instance.setProperty("Text", new Text("Some text..."));
        instance.setProperty("Blob", new Blob("Some text...".getBytes()));
        instance.setProperty("List", list);

        assertEquals("A String", instance.getProperty("String"));
        assertEquals(Boolean.TRUE, instance.getProperty("Boolean"));
        assertEquals(new Integer(1), instance.getProperty("Integer"));
        assertEquals(new Long(1), instance.getProperty("Long"));
        assertEquals(new Text("Some text..."), instance.getProperty("Text"));
        assertEquals(new Blob("Some text...".getBytes()), instance.getProperty("Blob"));
        assertEquals(list, instance.getProperty("List"));
    }

    @Test
    public void testLoad() {
        ApplicationData instance = ApplicationData.instance().reload();

        assertNull(instance.getProperty("String"));
        assertNull(instance.getProperty("Boolean"));
        assertNull(instance.getProperty("Integer"));
        assertNull(instance.getProperty("Long"));
        assertNull(instance.getProperty("Text"));
        assertNull(instance.getProperty("Blob"));
        assertNull(instance.getProperty("List"));

        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");

        instance.setProperty("String", "A String");
        instance.setProperty("Boolean", true);
        instance.setProperty("Integer", 1);
        instance.setProperty("Long", 1L);
        instance.setProperty("Text", new Text("Some text..."));
        instance.setProperty("Blob", new Blob("Some text...".getBytes()));
        instance.setProperty("List", list);

        instance.reload();
        Application application = instance.loadApplication();
        assertNotNull(application);

        Transaction tx = Datastore.beginTransaction();
        try {
            for (ApplicationProperty property : application.listProperties(tx)) {
                log.info("P: "/* '+' is to get toString coverage */ + property);
                assertNotNull(property.toString(), property.getValue());
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
        }

        assertEquals("A String", instance.getProperty("String"));
        assertEquals(Boolean.TRUE, instance.getProperty("Boolean"));
        assertEquals(new Integer(1), instance.getProperty("Integer"));
        assertEquals(new Long(1), instance.getProperty("Long"));
        assertEquals(new Text("Some text..."), instance.getProperty("Text"));
        assertEquals(new Blob("Some text...".getBytes()), instance.getProperty("Blob"));
        assertEquals(list, instance.getProperty("List"));

        instance.setProperty("String", null);
        instance.setProperty("Boolean", null);
        instance.setProperty("Integer", null);
        instance.setProperty("Long", null);
        instance.setProperty("Text", null);
        instance.setProperty("Blob", null);
        instance.setProperty("List", null);

        tx = Datastore.beginTransaction();
        try {
            for (ApplicationProperty property : application.listProperties(tx)) {
                switch (property.getKey().getName()) {
                    case "String":
                    case "Boolean":
                    case "Integer":
                    case "Long":
                    case "Text":
                    case "Blob":
                    case "List":
                        break;
                    default:
                        continue;
                }
                log.info("P: "/* '+' is to get toString coverage */ + property);
                assertNull(property.getValue());
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
        }

        assertNull(instance.getProperty("String"));
        assertNull(instance.getProperty("Boolean"));
        assertNull(instance.getProperty("Integer"));
        assertNull(instance.getProperty("Long"));
        assertNull(instance.getProperty("Text"));
        assertNull(instance.getProperty("Blob"));
        assertNull(instance.getProperty("List"));

        instance.reload();

        assertNull(instance.getProperty("String"));
        assertNull(instance.getProperty("Boolean"));
        assertNull(instance.getProperty("Integer"));
        assertNull(instance.getProperty("Long"));
        assertNull(instance.getProperty("Text"));
        assertNull(instance.getProperty("Blob"));
        assertNull(instance.getProperty("List"));
    }
}