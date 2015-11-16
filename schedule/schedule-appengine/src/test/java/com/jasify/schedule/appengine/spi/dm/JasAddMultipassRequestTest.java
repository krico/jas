package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

/**
 * @author wszarmach
 * @since 12/11/15.
 */
public class JasAddMultipassRequestTest {

    @Before
    public void before() {
        TestHelper.initializeDatastore();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testOrganization() {
        Key organization = Datastore.allocateId(Organization.class);
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setOrganizationId(organization);
        Assert.assertEquals(organization, jasAddMultipassRequest.getOrganizationId());
    }

    @Test
    public void testMultipass() {
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        Multipass multipass = new Multipass();
        jasAddMultipassRequest.setMultipass(multipass);
        Assert.assertEquals(multipass, jasAddMultipassRequest.getMultipass());
    }
}