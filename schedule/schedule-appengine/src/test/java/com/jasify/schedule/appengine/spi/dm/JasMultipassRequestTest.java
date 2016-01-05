package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author wszarmach
 * @since 12/11/15.
 */
public class JasMultipassRequestTest {

    @Before
    public void before() {
        TestHelper.initializeDatastore();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testMultipass() {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        Multipass multipass = new Multipass();
        jasMultipassRequest.setMultipass(multipass);
        assertEquals(multipass, jasMultipassRequest.getMultipass());
    }

    @Test
    public void testOrganizationId() {
        JasMultipassRequest jasMultipassRequest = new JasMultipassRequest();
        assertNull(jasMultipassRequest.getOrganizationId());
        Key organizationId = Datastore.allocateId(Organization.class);
        jasMultipassRequest.setOrganizationId(organizationId);
        assertEquals(organizationId, jasMultipassRequest.getOrganizationId());
    }
}