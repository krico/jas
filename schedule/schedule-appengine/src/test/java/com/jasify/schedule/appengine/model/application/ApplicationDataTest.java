package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.model.ModelTestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class ApplicationDataTest {
    @Before
    public void initializeDatastore() {
        ModelTestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        ModelTestHelper.cleanupDatastore();
    }

    @Test
    public void testInstance() {
        ApplicationData instance = ApplicationData.instance();
        assertNotNull(instance);
        assertEquals(instance, ApplicationData.instance());
        assertTrue(instance == ApplicationData.instance());
    }

    @Test
    public void testGetSet() {
        ApplicationData instance = ApplicationData.instance();
        assertNull(instance.getProperty("String"));
        assertNull(instance.getProperty("Boolean"));
        assertNull(instance.getProperty("Long"));
        assertNull(instance.getProperty("Text"));
        assertNull(instance.getProperty("Blob"));

        instance.setProperty("String", "A String");
        instance.setProperty("Boolean", true);
        instance.setProperty("Long", 1L);
        instance.setProperty("Text", new Text("Some text..."));
        instance.setProperty("Blob", new Blob("Some text...".getBytes()));

        assertEquals("A String", instance.getProperty("String"));
        assertEquals(Boolean.TRUE, instance.getProperty("Boolean"));
        assertEquals(new Long(1), instance.getProperty("Long"));
        assertEquals(new Text("Some text..."), instance.getProperty("Text"));
        assertEquals(new Blob("Some text...".getBytes()), instance.getProperty("Blob"));

        instance.reload();

        assertEquals("A String", instance.getProperty("String"));
        assertEquals(Boolean.TRUE, instance.getProperty("Boolean"));
        assertEquals(new Long(1), instance.getProperty("Long"));
        assertEquals(new Text("Some text..."), instance.getProperty("Text"));
        assertEquals(new Blob("Some text...".getBytes()), instance.getProperty("Blob"));
    }

}