package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

public class OrganizationServiceTest {
    private static final String TEST_USER_NAME = "pablo";
    private static final String TEST_ORGANIZATION_NAME = "Organization Named Test";
    private static final String TEST_GROUP_NAME = "Group Named Test";
    private OrganizationService organizationService;

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        organizationService = OrganizationServiceFactory.getOrganizationService();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = FieldValueException.class)
    public void testAddOrganizationWithNoName() throws Exception {
        organizationService.addOrganization(new Organization());
    }

    @Test(expected = FieldValueException.class)
    public void testAddOrganizationWithEmptyName() throws Exception {
        organizationService.addOrganization(new Organization(""));
    }

    @Test(expected = UniqueConstraintException.class)
    public void testAddOrganizationViolatingNameConstraint() throws Exception {
        organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
    }

    @Test(expected = UniqueConstraintException.class)
    public void testAddOrganizationViolatingNameConstraintIsCaseInsensitive() throws Exception {
        organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME.toLowerCase()));
    }

    @Test
    public void testAddOrganization() throws Exception {
        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        organization.setDescription("Description");
        Key badId = Datastore.allocateId(OrganizationMeta.get());
        organization.setId(badId);

        Key id = organizationService.addOrganization(organization);
        assertNotNull(id);
        assertNotSame(badId, id);
        assertEquals(id, organization.getId());

        Organization fetched = Datastore.get(OrganizationMeta.get(), id);
        assertNotNull(fetched);
        assertEquals(TEST_ORGANIZATION_NAME, fetched.getName());
        assertEquals("Description", fetched.getDescription());
    }

    @Test
    public void testGetOrganizationById() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        Organization organization = organizationService.getOrganization(id);
        assertNotNull(organization);
        assertEquals(TEST_ORGANIZATION_NAME, organization.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetOrganizationByIdNotOrganization() throws Exception {
        Key id = organizationService.addGroup(new Group(TEST_GROUP_NAME));
        organizationService.getOrganization(id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetOrganizationByIdNotFound() throws Exception {
        organizationService.getOrganization(Datastore.allocateId(Organization.class));
    }

    @Test
    public void testGetOrganizationByName() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        Organization organization = organizationService.getOrganization(TEST_ORGANIZATION_NAME);
        assertNotNull(organization);
        assertEquals(TEST_ORGANIZATION_NAME, organization.getName());
        assertEquals(id, organization.getId());
    }

    @Test
    public void testGetOrganizationByNameCaseInsensitive() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        Organization organization = organizationService.getOrganization(TEST_ORGANIZATION_NAME.toLowerCase());
        assertNotNull(organization);
        assertEquals(TEST_ORGANIZATION_NAME, organization.getName());
        assertEquals(id, organization.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetOrganizationByNameNotFound() throws Exception {
        organizationService.getOrganization("not found");
    }

    @Test
    public void testGetGroups() throws Exception {
        List<Group> groups = organizationService.getGroups();
        assertNotNull(groups);
        assertTrue(groups.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            added.add(organizationService.addGroup(new Group(TEST_GROUP_NAME + i)));
        }

        groups = organizationService.getGroups();
        assertNotNull(groups);
        assertEquals(20, groups.size());
        assertEquals(20, added.size());
        for (Group group : groups) {
            assertTrue(added.remove(group.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test
    public void testGetOrganizations() throws Exception {
        List<Organization> organizations = organizationService.getOrganizations();
        assertNotNull(organizations);
        assertTrue(organizations.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            added.add(organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME + i)));
        }

        organizations = organizationService.getOrganizations();
        assertNotNull(organizations);
        assertEquals(20, organizations.size());
        assertEquals(20, added.size());
        for (Organization organization : organizations) {
            assertTrue(added.remove(organization.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        Key id = organizationService.addOrganization(organization);
        organization.setDescription("New Description");
        Organization returnedOrg = organizationService.updateOrganization(organization);
        assertNotNull(returnedOrg);
        assertEquals(id, returnedOrg.getId());
        Organization fetched = organizationService.getOrganization(id);
        assertEquals("New Description", fetched.getDescription());
    }

    @Test
    public void testAddUserToOrganization() throws Exception {
        User user = new User(TEST_USER_NAME);
        Datastore.put(user);
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));

        Organization organization = organizationService.getOrganization(id);

        assertNotNull(organization);
        List<User> users = organization.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        organizationService.addUserToOrganization(id, user.getId());
        organization = organizationService.getOrganization(id);

        users = organization.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void testRemoveUserFromOrganization() throws Exception {
        User user = new User(TEST_USER_NAME);
        Datastore.put(user);
        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        organizationService.addOrganization(organization);
        organizationService.addUserToOrganization(organization.getId(), user.getId());
        organizationService.addUserToOrganization(organization, user);
        organizationService.removeUserFromOrganization(organization, user);

        List<User> users = organization.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testAddGroupToOrganization() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        organizationService.addGroup(group);
        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        Key organizationId = organizationService.addOrganization(organization);

        List<Group> groups = organization.getGroups();
        assertNotNull(groups);
        assertTrue(groups.isEmpty());

        organizationService.addGroupToOrganization(organization, group);
        organization = organizationService.getOrganization(organizationId);
        assertNotNull(organization);
        groups = organization.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals(group.getId(), groups.get(0).getId());
    }

    @Test
    public void testRemoveGroupFromOrganization() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        organizationService.addGroup(group);

        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        Key organizationId = organizationService.addOrganization(organization);

        organizationService.addGroupToOrganization(organization.getId(), group.getId());
        organizationService.addGroupToOrganization(organization, group);
        organizationService.removeGroupFromOrganization(organization.getId(), group.getId());

        organization = organizationService.getOrganization(organizationId);
        assertNotNull(organization);
        List<Group> groups = organization.getGroups();
        assertNotNull(groups);
        assertTrue(groups.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testRemoveOrganizationNotFound() throws Exception {
        organizationService.removeOrganization(Datastore.allocateId(Organization.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveOrganizationNotOrganization() throws Exception {
        Key id = organizationService.addGroup(new Group(TEST_GROUP_NAME));
        organizationService.removeOrganization(id);
    }

    @Test
    public void testRemoveOrganizationReleasesName() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.removeOrganization(id);
        organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
    }

    @Test
    public void testRemoveOrganization() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        assertNotNull(id);

        User user = new User(TEST_USER_NAME);
        Datastore.put(user);

        Organization organization = new Organization(TEST_ORGANIZATION_NAME);
        Key organizationId = organizationService.addOrganization(organization);

        organizationService.addUserToOrganization(organization, user);
        organizationService.addGroupToOrganization(organization, group);

        organizationService.removeOrganization(organizationId);

        assertTrue(Datastore.query(Organization.class).asKeyList().isEmpty());
        assertTrue(Datastore.query(OrganizationMember.class).asKeyList().isEmpty());

        //make sure they exists (will throw if they don't)
        Datastore.get(User.class, user.getId());
        Datastore.get(Group.class, id);

    }

    @Test(expected = FieldValueException.class)
    public void testAddGroupNoName() throws Exception {
        organizationService.addGroup(new Group());
    }

    @Test
    public void testAddGroup() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key badId = Datastore.allocateId(Group.class);
        group.setId(badId);
        group.setDescription("Description");
        Key id = organizationService.addGroup(group);
        assertNotNull(id);
        assertNotSame(badId, id);
        Group fetched = organizationService.getGroup(id);
        assertNotNull(fetched);
        assertEquals(TEST_GROUP_NAME, fetched.getName());
        assertEquals("Description", fetched.getDescription());
        assertEquals(id, fetched.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetGroupByIdNotFound() throws Exception {
        organizationService.getGroup(Datastore.allocateId(Group.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGroupByIdNotGroup() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.getGroup(id);
    }

    @Test
    public void testGetGroupById() throws Exception {
        Key id = organizationService.addGroup(new Group(TEST_GROUP_NAME));
        Group group = organizationService.getGroup(id);
        assertNotNull(group);
        assertEquals(TEST_GROUP_NAME, group.getName());
        assertEquals(id, group.getId());
    }

    @Test
    public void testUpdateGroup() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        group.setDescription("New Description");
        Group returnedGroup = organizationService.updateGroup(group);
        assertNotNull(returnedGroup);
        assertEquals(id, returnedGroup.getId());
        Group fetched = organizationService.getGroup(id);
        assertEquals("New Description", fetched.getDescription());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddUserToGroupUserNotFound() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        User user = new User(TEST_USER_NAME);
        user.setId(Datastore.allocateId(User.class));

        organizationService.addUserToGroup(group, user);
    }

    @Test
    public void testAddUserToGroup() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        User user = new User(TEST_USER_NAME);
        Datastore.put(user);

        group = organizationService.getGroup(id);
        assertNotNull(group);
        List<User> users = group.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());

        organizationService.addUserToGroup(group, user);

        group = organizationService.getGroup(id);
        users = group.getUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(user.getId(), users.get(0).getId());
    }

    @Test
    public void testRemoveUserFromGroup() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        User user = new User(TEST_USER_NAME);
        Datastore.put(user);

        organizationService.addUserToGroup(group.getId(), user.getId());
        organizationService.addUserToGroup(group, user);
        organizationService.removeUserFromGroup(group.getId(), user.getId());

        group = organizationService.getGroup(id);
        assertNotNull(group);
        List<User> users = group.getUsers();
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testRemoveGroupNotFound() throws Exception {
        organizationService.removeGroup(Datastore.allocateId(Group.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGroupNotGroup() throws Exception {
        Key id = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.removeGroup(id);
    }

    @Test
    public void testRemoveGroup() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        organizationService.removeGroup(id);
        assertTrue(Datastore.query(Group.class).asKeyList().isEmpty());
    }

    @Test
    public void testRemoveGroupRemovesGroupUser() throws Exception {
        Group group = new Group(TEST_GROUP_NAME);
        Key id = organizationService.addGroup(group);
        User user = new User(TEST_USER_NAME);
        Datastore.put(user);
        organizationService.addUserToGroup(group, user);

        organizationService.removeGroup(id);
        assertTrue(Datastore.query(Group.class).asKeyList().isEmpty());
        assertTrue(Datastore.query(GroupUser.class).asKeyList().isEmpty());

        //make sure they exists (will throw if they don't)
        Datastore.get(User.class, user.getId());
    }

    @Test
    public void testRemoveGroupRemovesOrganizationMember() throws Exception {
        Key id = organizationService.addGroup(new Group(TEST_GROUP_NAME));
        Key organizationId = organizationService.addOrganization(new Organization(TEST_ORGANIZATION_NAME));
        organizationService.addGroupToOrganization(organizationId, id);

        organizationService.removeGroup(id);
        assertTrue(Datastore.query(Group.class).asKeyList().isEmpty());
        assertTrue(Datastore.query(OrganizationMember.class).asKeyList().isEmpty());

        //make sure they exists (will throw if they don't)
        Datastore.get(Organization.class, organizationId);
    }
}