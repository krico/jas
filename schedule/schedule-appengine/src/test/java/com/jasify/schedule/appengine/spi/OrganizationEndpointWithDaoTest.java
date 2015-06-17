package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.AssertionHelper;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.common.GroupDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.jasify.schedule.appengine.AssertionHelper.assertIdsEqual;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newOrgMemberCaller;
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

    @Test
    public void testGetOrganizationsForUser() throws Exception {
        Organization o1 = new Organization("Org1");
        Organization o2 = new Organization("Org2");
        Organization o3 = new Organization("Org3");
        User user = new User("user1");

        Datastore.put(o1, o2, o3, user);

        OrganizationMember om1 = new OrganizationMember(o1, user);
        OrganizationMember om3 = new OrganizationMember(o3, user);

        Datastore.put(om1, om3);

        JasifyEndpointUser caller = newOrgMemberCaller(user.getId().getId());
        List<Organization> organizations = endpoint.getOrganizations(caller);
        assertIdsEqual(Arrays.asList(o1, o3), organizations);
    }

    @Test
    public void testGetOrganization() throws Exception {
        Organization organization = new Organization();
        organization.setName("Org");

        organization = endpoint.addOrganization(newAdminCaller(55), organization);

        Organization result = endpoint.getOrganization(newAdminCaller(55), organization.getId());
        assertEquals(organization.getId(), result.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testGetOrganizationNotFound() throws Exception {
        Key key = Datastore.allocateId(Organization.class);
        endpoint.getOrganization(newAdminCaller(55), key);
    }

    @Test
    public void testAddOrganization() throws Exception {
        Organization organization = new Organization();
        organization.setName("Org");

        Organization result = endpoint.addOrganization(newAdminCaller(55), organization);
        assertEquals(organization, result);
    }

    @Test(expected = BadRequestException.class)
    public void testAddOrganizationNotFoundViaUniqueConstraintException() throws Exception {
        Organization organization = new Organization();
        organization.setName("Org");
        Organization dupOrganization = new Organization();
        dupOrganization.setName("Org");
        endpoint.addOrganization(newAdminCaller(55), organization);
        endpoint.addOrganization(newAdminCaller(55), dupOrganization);
    }

    @Test(expected = BadRequestException.class)
    public void testAddOrganizationNotFoundViaFieldValueException() throws Exception {
        Organization organization = new Organization();
        endpoint.addOrganization(newAdminCaller(55), organization);
    }

    @Test
    public void testGetOrganizationUsers() throws Exception {
        Key organizationId = Datastore.allocateId(Organization.class);
        List<User> result = endpoint.getOrganizationUsers(newAdminCaller(55), organizationId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetOrganizationGroups() throws Exception {
        Key organizationId = Datastore.allocateId(Organization.class);
        List<Group> result = endpoint.getOrganizationGroups(newAdminCaller(55), organizationId);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        Organization organization = TestHelper.createOrganization(false);
        OrganizationDao dao = new OrganizationDao();
        HashSet<PaymentTypeEnum> pt = new HashSet<>();
        pt.add(PaymentTypeEnum.PayPal);
        organization.setPaymentTypes(pt);
        dao.save(organization);

        Organization update = new Organization(organization.getName());
        update.setLcName("THIS SHOULD BE IGNORED");
        update.setName("New Org Name");
        update.getPaymentTypes().add(PaymentTypeEnum.Cash);
        update.setDescription("Update description");

        Organization updatedResponse = endpoint.updateOrganization(newAdminCaller(11), organization.getId(), update);

        //Make sure datastore and response are the same
        AssertionHelper.assertAttributesEquals(Datastore.get(Organization.class, organization.getId()), updatedResponse);

        //make sure stuff was really updated
        assertEquals(update.getName(), updatedResponse.getName());
        assertEquals(StringUtils.lowerCase(update.getName()), updatedResponse.getLcName());
        assertEquals(update.getDescription(), updatedResponse.getDescription());
        assertEquals(update.getPaymentTypes(), updatedResponse.getPaymentTypes());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOrganizationNameAlreadyExists() throws Exception {
        Organization organization1 = TestHelper.createOrganization(false);
        Organization organization2 = TestHelper.createOrganization(false);
        OrganizationDao dao = new OrganizationDao();
        dao.save(organization1);
        dao.save(organization2);

        organization2.setName(organization1.getName());

        endpoint.updateOrganization(newAdminCaller(55), organization2.getId(), organization2);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateNonExisting() throws Exception {
        Organization organization = new Organization();
        organization.setId(Datastore.allocateId(Organization.class));
        endpoint.updateOrganization(newAdminCaller(55), organization.getId(), organization);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOrganizationNullName() throws Exception {
        Organization organization1 = TestHelper.createOrganization(false);
        OrganizationDao dao = new OrganizationDao();
        dao.save(organization1);

        organization1.setName(null);

        endpoint.updateOrganization(newAdminCaller(55), organization1.getId(), organization1);
    }

    @Test
    public void testAddUserToOrganization() throws Exception {
        Organization organization1 = TestHelper.createOrganization(false);
        OrganizationDao organizationDao = new OrganizationDao();
        Key organizationId = organizationDao.save(organization1);
        UserDao userDao = new UserDao();
        User user1 = new User("myUser");
        Key userId = userDao.save(user1);
        assertTrue(endpoint.getOrganizationUsers(newAdminCaller(55), organizationId).isEmpty());
        endpoint.addUserToOrganization(newAdminCaller(55), organizationId, userId);
        assertFalse(endpoint.getOrganizationUsers(newAdminCaller(55), organizationId).isEmpty());
    }

    @Test
    public void testRemoveUserFromOrganization() throws Exception {
        testAddUserToOrganization();
        Organization organization = endpoint.getOrganizations(newAdminCaller(55)).get(0);
        User user = endpoint.getOrganizationUsers(newAdminCaller(55), organization.getId()).get(0);

        endpoint.removeUserFromOrganization(newAdminCaller(55), organization.getId(), user.getId());

        assertTrue(endpoint.getOrganizationUsers(newAdminCaller(55), organization.getId()).isEmpty());
    }

    @Test
    public void testAddGroupToOrganization() throws Exception {
        Organization organization1 = OrganizationDaoTest.createExample();
        OrganizationDao organizationDao = new OrganizationDao();
        Key organizationId = organizationDao.save(organization1);
        GroupDao group = new GroupDao();
        Group group1 = new Group("myUser");
        Key groupId = group.save(group1);
        assertTrue(endpoint.getOrganizationGroups(newAdminCaller(55), organizationId).isEmpty());
        endpoint.addGroupToOrganization(newAdminCaller(55), organizationId, groupId);
        assertFalse(endpoint.getOrganizationGroups(newAdminCaller(55), organizationId).isEmpty());
    }

    @Test
    public void testRemoveGroupFromOrganization() throws Exception {
        testAddGroupToOrganization();
        Organization organization = endpoint.getOrganizations(newAdminCaller(55)).get(0);
        Group group = endpoint.getOrganizationGroups(newAdminCaller(55), organization.getId()).get(0);

        endpoint.removeGroupFromOrganization(newAdminCaller(55), organization.getId(), group.getId());

        assertTrue(endpoint.getOrganizationGroups(newAdminCaller(55), organization.getId()).isEmpty());
    }
}
