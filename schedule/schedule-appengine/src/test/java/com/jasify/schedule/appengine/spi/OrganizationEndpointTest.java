package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.AssertionHelper;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.common.GroupDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDaoTest;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
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
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.*;
import static junit.framework.TestCase.*;

public class OrganizationEndpointTest {
    private List<Organization> organizations = new ArrayList<>();
    private OrganizationEndpoint endpoint;

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        endpoint = new OrganizationEndpoint();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationsNoUser() throws Exception {
        endpoint.getOrganizations(null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationsNotAdmin() throws Exception {
        endpoint.getOrganizations(newCaller(1));
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddOrganizationsNoUser() throws Exception {
        endpoint.addOrganization(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddOrganizationNotAdmin() throws Exception {
        endpoint.addOrganization(newCaller(1), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveOrganizationNotAdmin() throws Exception {
        endpoint.removeOrganization(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveOrganizationNoUser() throws Exception {
        endpoint.removeOrganization(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationNotAdmin() throws Exception {
        endpoint.getOrganization(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddUserToOrganizationNoUser() throws Exception {
        endpoint.addUserToOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddUserToOrganizationNotAdmin() throws Exception {
        endpoint.addUserToOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveUserFromOrganizationNoUser() throws Exception {
        endpoint.removeUserFromOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveUserFromOrganizationNotAdmin() throws Exception {
        endpoint.removeUserFromOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddGroupToOrganizationNoUser() throws Exception {
        endpoint.addGroupToOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddGroupToOrganizationNotAdmin() throws Exception {
        endpoint.addGroupToOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveGroupFromOrganizationNoUser() throws Exception {
        endpoint.removeGroupFromOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveGroupFromOrganizationNotAdmin() throws Exception {
        endpoint.removeGroupFromOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationUsersNoUser() throws Exception {
        endpoint.getOrganizationUsers(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationUsersNotAdmin() throws Exception {
        endpoint.getOrganizationUsers(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationGroupsNoUser() throws Exception {
        endpoint.getOrganizationGroups(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationGroupsNotAdmin() throws Exception {
        endpoint.getOrganizationGroups(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateOrganizationNoUser() throws Exception {
        endpoint.updateOrganization(null, null, new Organization());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateOrganizationNotAdmin() throws Exception {
        endpoint.updateOrganization(newCaller(1), null, new Organization());
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateOrganizationCheckNotFound() throws Exception {
        endpoint.updateOrganization(newAdminCaller(1), null, new Organization());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveOrganizationCheckNotFound() throws Exception {
        endpoint.removeOrganization(newAdminCaller(1), null);
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
                Organization organization = OrganizationDaoTest.createExample();
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
        Organization organization = OrganizationDaoTest.createExample();
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
        Organization organization1 = OrganizationDaoTest.createExample();
        Organization organization2 = OrganizationDaoTest.createExample();
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
        Organization organization1 = OrganizationDaoTest.createExample();
        OrganizationDao dao = new OrganizationDao();
        dao.save(organization1);

        organization1.setName(null);

        endpoint.updateOrganization(newAdminCaller(55), organization1.getId(), organization1);
    }

    @Test
    public void testAddUserToOrganization() throws Exception {
        Organization organization1 = OrganizationDaoTest.createExample();
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

    @Test(expected = NotFoundException.class)
    public void testRemoveOrganizationNotFound() throws Exception {
        Key key = Datastore.allocateId(Organization.class);
        endpoint.removeOrganization(newAdminCaller(55), key);
    }

    @Test
    public void testRemoveOrganization() throws Exception {
        Organization organization1 = OrganizationDaoTest.createExample();
        OrganizationDao organizationDao = new OrganizationDao();
        Key organizationId = organizationDao.save(organization1);
        endpoint.removeOrganization(newAdminCaller(55), organizationId);
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveOrganizationWithMembers() throws Exception {
        OrganizationDao organizationDao = new OrganizationDao();
        UserDao userDao = new UserDao();
        GroupDao groupDao = new GroupDao();
        Key organizationId = organizationDao.save(OrganizationDaoTest.createExample());
        Key userId = userDao.save(new User("user1"));
        Key groupId = groupDao.save(new Group("group1"));

        organizationDao.addUserToOrganization(organizationId, userId);
        organizationDao.addGroupToOrganization(organizationId, groupId);

        endpoint.removeOrganization(newAdminCaller(55), organizationId);
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveOrganizationWithManyMembers() throws Exception {
        OrganizationDao organizationDao = new OrganizationDao();
        UserDao userDao = new UserDao();
        GroupDao groupDao = new GroupDao();
        Key organizationId = organizationDao.save(OrganizationDaoTest.createExample());
        final int memberNumber = 50;

        for (int i = 0; i < memberNumber; ++i) {
            Key userId = userDao.save(new User("user" + i));
            Key groupId = groupDao.save(new Group("group" + i));

            organizationDao.addUserToOrganization(organizationId, userId);
            organizationDao.addGroupToOrganization(organizationId, groupId);
        }

        endpoint.removeOrganization(newAdminCaller(55), organizationId);
    }
}