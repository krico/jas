package com.jasify.schedule.appengine.spi;

import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static junit.framework.TestCase.*;

/**
 * @author krico
 * @since 03/06/15.
 */
public class OrganizationEndpointWithDaoTest {
    private OrganizationEndpoint endpoint;
    private List<Organization> organizations = new ArrayList<>();

    static Organization createOrganization() {
        Populator populator = new PopulatorBuilder().build();
        return populator.populateBean(Organization.class, "id", "organizationMemberListRef");
    }

    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
        endpoint = new OrganizationEndpoint();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetPublicOrganizationsEmpty() throws Exception {
        OrganizationDao dao = new OrganizationDao();
        List<Organization> organizations = endpoint.getPublicOrganizations(null);
        assertNotNull(organizations);
        assertTrue(organizations.isEmpty());
    }

    @Test
    public void testGetPublicOrganizations() throws Exception {
        OrganizationDao dao = new OrganizationDao();
        for (int i = 0; i < 20; ++i) {
            Transaction tx = Datastore.beginTransaction();
            try {
                Organization organization = createOrganization();
                dao.save(organization);
                tx.commit();
                organizations.add(organization);
            } finally {
                if (tx.isActive()) tx.rollback();
            }
        }
        List<Organization> publicOrganizations = endpoint.getPublicOrganizations(null);
        assertNotNull(publicOrganizations);
        assertEquals(organizations.size(), publicOrganizations.size());
        for (Organization organization : organizations) {
            boolean found = false;
            for (Organization publicOrganization : publicOrganizations) {
                if (organization.getId().equals(publicOrganization.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    public void testGetOrganizationsForAdmin() throws Exception {
        testGetPublicOrganizations();
        JasifyEndpointUser caller = newAdminCaller(55);
        List<Organization> adminOrganizations = endpoint.getOrganizations(caller);
        assertNotNull(adminOrganizations);
        assertEquals(organizations.size(), adminOrganizations.size());
        for (Organization organization : organizations) {
            boolean found = false;
            for (Organization adminOrganization : adminOrganizations) {
                if (organization.getId().equals(adminOrganization.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }
}
