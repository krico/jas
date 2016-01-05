package com.jasify.schedule.appengine.model.multipass;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class MultipassTest {

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testId() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getId());
        Key id = Datastore.allocateId(Multipass.class);
        multipass.setId(id);
        assertEquals(id, multipass.getId());
    }

    @Test
    public void testCreated() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getCreated());
        Date date = new Date();
        multipass.setCreated(date);
        assertEquals(date, multipass.getCreated());
    }

    @Test
    public void testModified() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getModified());
        Date date = new Date();
        multipass.setModified(date);
        assertEquals(date, multipass.getModified());
    }

    @Test
    public void testName() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getName());
        String name = "TEST";
        multipass.setName("TEST");
        assertEquals("TEST", multipass.getName());
        assertEquals(name, multipass.getLcName());
    }

    @Test
    public void testLcName() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getLcName());
        multipass.setLcName("TEST");
        assertEquals("TEST", multipass.getLcName());
    }

    @Test
    public void testNameAndLcNameRelationship() {
        Multipass multipass = new Multipass();
        multipass.setName("TEST1");
        assertEquals("TEST1", multipass.getName());
        assertEquals("TEST1", multipass.getLcName());

        multipass.setLcName("TEST2");
        assertEquals("TEST1", multipass.getName());
        assertEquals("TEST2", multipass.getLcName());
    }

    @Test
    public void testDescription() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getDescription());
        multipass.setDescription("TEST");
        assertEquals("TEST", multipass.getDescription());
    }


    @Test
    public void testPrice() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getPrice());
        multipass.setPrice(15.0);
        assertEquals(15.0, multipass.getPrice());
    }

    @Test
    public void testCurrency() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getCurrency());
        multipass.setCurrency("TEST");
        assertEquals("TEST", multipass.getCurrency());
    }

    @Test
    public void testExpiresAfter() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getExpiresAfter());
        multipass.setExpiresAfter(12);
        assertEquals(12, multipass.getExpiresAfter().intValue());
    }

    @Test
    public void testUses() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getUses());
        multipass.setUses(12);
        assertEquals(12, multipass.getUses().intValue());
    }

    @Test
    public void testOrganization() {
        Multipass multipass = new Multipass();
        assertNull(multipass.getOrganizationRef().getKey());
        assertNull(multipass.getOrganizationRef().getModel());
        Organization organization = TestHelper.createOrganization(true);
        multipass.getOrganizationRef().setKey(organization.getId());
        assertEquals(organization.getId(), multipass.getOrganizationRef().getKey());
        assertNotNull(multipass.getOrganizationRef().getModel());
    }
}
