package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.multipass.MultipassDao;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import com.jasify.schedule.appengine.spi.dm.JasAddMultipassRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;

/**
 * @author wszarmach
 * @since 12/11/15.
 */
public class MultipassEndpointTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private MultipassEndpoint endpoint;

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        endpoint = new MultipassEndpoint();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    private void equals(Multipass multipass1, Multipass multipass2) {
        assertEquals(multipass1.getCurrency(), multipass2.getCurrency());
        assertEquals(multipass1.getDescription(), multipass2.getDescription());
        assertEquals(multipass1.getId(), multipass2.getId());
        assertEquals(multipass1.getLcName().toLowerCase(), multipass2.getLcName());
        assertEquals(multipass1.getName(), multipass2.getName());
        assertEquals(multipass1.getOrganizationRef().getKey(), multipass2.getOrganizationRef().getKey());
        assertEquals(multipass1.getPrice(), multipass2.getPrice());
    }

    @Test
    public void testAddMultipassNullJasAddMultipassRequest() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
        endpoint.addMultipass(newAdminCaller(1), null);
    }

    @Test
    public void testAddMultipassNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.addMultipass(newCaller(1), new JasAddMultipassRequest());
    }

    @Test
    public void testAddMultipassNullRequestMultipass() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.multipass == null");
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setOrganizationId(Datastore.allocateId(Organization.class));
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testAddMultipassNullRequestOrganization() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.organizationId == null");
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setMultipass(new Multipass());
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testAddMultipassUnkownRequestOrganization() throws Exception {
        thrown.expect(BadRequestException.class);
        Key organizationId = Datastore.allocateId(Organization.class);
        thrown.expectMessage("No entity was found matching the key: " + organizationId);
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setMultipass(new Multipass());
        jasAddMultipassRequest.setOrganizationId(organizationId);
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testAddMultipassNullName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Multipass.name");
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setMultipass(new Multipass());
        jasAddMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testAddMultipassDuplicate() throws Exception {
        thrown.expect(BadRequestException.class);
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasAddMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        thrown.expectMessage("Multipass.name=" + jasAddMultipassRequest.getMultipass().getName() + ", Organization.id=" + jasAddMultipassRequest.getOrganizationId());
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
        jasAddMultipassRequest.getMultipass().setId(null);
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testAddMultipass() throws Exception {
        JasAddMultipassRequest jasAddMultipassRequest = new JasAddMultipassRequest();
        jasAddMultipassRequest.setMultipass(com.jasify.schedule.appengine.TestHelper.populateBean(Multipass.class, "id", "organizationRef", "lcName"));
        jasAddMultipassRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.addMultipass(newAdminCaller(1), jasAddMultipassRequest);
    }

    @Test
    public void testGetMultipassNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.getMultipass(newAdminCaller(1), null);
    }

    @Test
    public void testGetMultipassNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getMultipass(newCaller(1), Datastore.allocateId(Multipass.class));
    }

    @Test
    public void testGetMultipassUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.getMultipass(newAdminCaller(1), id);
    }

    @Test
    public void testGetMultipass() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        Multipass result = endpoint.getMultipass(newAdminCaller(1), multipass.getId());
        equals(multipass, result);
    }

    @Test
    public void testGetMultipassNullOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("organizationId == null");
        endpoint.getMultipasses(newCaller(1), null);
    }

    @Test
    public void testGetMultipassesUnknownOrganizationId() throws Exception {
        List<Multipass> result = endpoint.getMultipasses(newCaller(1), Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetMultipasses() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        TestHelper.createMultipass(organization, true);
        TestHelper.createMultipass(organization, true);
        TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        List<Multipass> result = endpoint.getMultipasses(newCaller(1), organization.getId());
        assertEquals(2, result.size());
    }

    @Test
    public void testRemoveMultipassNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.removeMultipass(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveMultipassNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeMultipass(newCaller(1), Datastore.allocateId(Multipass.class));
    }

    @Test
    public void testRemoveMultipassUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.removeMultipass(newAdminCaller(1), id);
    }

    @Test
    public void testRemoveMultipass() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Multipass multipass = TestHelper.createMultipass(organization, true);
        MultipassDao multipassDao = new MultipassDao();
        assertFalse(multipassDao.getByOrganization(organization.getId()).isEmpty());
        endpoint.removeMultipass(newAdminCaller(1), multipass.getId());
        assertTrue(multipassDao.getByOrganization(organization.getId()).isEmpty());
    }

    @Test
    public void testUpdateMultipassNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.updateMultipass(newAdminCaller(1), null, TestHelper.createMultipass(TestHelper.createOrganization(true), true));
    }

    @Test
    public void testUpdateMultipassNullMultipass() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("multipass == null");
        endpoint.updateMultipass(newAdminCaller(1), Datastore.allocateId(Multipass.class), null);
    }

    @Test
    public void testUpdateMultipassNotAdminOrOrgAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        endpoint.updateMultipass(newCaller(1), multipass.getId(), multipass);
    }

    @Test
    public void testUpdateMultipassUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Multipass.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.updateMultipass(newAdminCaller(1), id, new Multipass());
    }

    @Test
    public void testUpdateMultipass() throws Exception {
        Multipass multipass = TestHelper.createMultipass(TestHelper.createOrganization(true), true);
        Multipass result = endpoint.updateMultipass(newAdminCaller(1), multipass.getId(), multipass);
        equals(multipass, result);
    }
}
