package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jasify.schedule.appengine.AssertionHelper.assertIdsEqual;
import static junit.framework.TestCase.*;


public class OrganizationDaoTest {
    private OrganizationDao dao;

    @BeforeClass
    public static void initialize() {
        TestHelper.setSystemProperties();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void resetCache() {
        TestHelper.initializeDatastore();
        dao = new OrganizationDao();
    }

    private Key save(final Organization example) throws ModelException {
        return TransactionOperator.execute(new ModelOperation<Key>() {
            @Override
            public Key execute(Transaction tx) throws ModelException {
                Key key = dao.save(example);
                tx.commit();
                return key;
            }
        });
    }

    @Test
    public void testSave() throws Exception {
        Key id = save(TestHelper.createOrganization(false));
        assertNotNull(id);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testSaveChecksUniquenessOfName() throws Exception {
        Organization example = TestHelper.createOrganization(false);
        Organization example2 = TestHelper.createOrganization(false);
        example2.setName(example.getName());
        save(example);
        save(example2);
    }

    @Test
    public void testUpdateWithNoNameChange() throws Exception {
        final Key id = save(TestHelper.createOrganization(false));
        Organization organization = dao.get(id);
        assertNotNull(organization);
        organization.setDescription("New desc");
        save(organization);
    }

    @Test
    public void testSaveUpdatesUniqueIndex() throws Exception {
        Organization example = TestHelper.createOrganization(false);
        Organization example2 = TestHelper.createOrganization(false);
        example2.setName(example.getName());
        save(example);
        example.setName("Another Name");
        save(example);//this should update the index
        save(example2);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testBatchSaveUniqueIndex() throws Exception {
        Organization example1 = TestHelper.createOrganization(false);
        Organization example2 = TestHelper.createOrganization(false);
        Organization example3 = TestHelper.createOrganization(false);
        example3.setName(example1.getName());
        dao.save(Arrays.asList(example1, example2));
        save(example3);
    }

    @Test
    public void testDelete() throws Exception {
        Key id = save(TestHelper.createOrganization(false));
        dao.delete(id);
    }

    @Test
    public void testDeleteFreesIndex() throws Exception {
        final Organization example = TestHelper.createOrganization(false);
        Organization example2 = TestHelper.createOrganization(false);
        example2.setName(example.getName());
        save(example);
        TransactionOperator.execute(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                dao.delete(example.getId());
                tx.commit();
                return null;
            }
        });
        save(example2);
    }

    @Test
    public void testBatchDeleteFreesIndex() throws Exception {
        Organization example1 = TestHelper.createOrganization(false);
        Organization example2 = TestHelper.createOrganization(false);
        Organization example3 = TestHelper.createOrganization(false);

        save(example1);
        save(example2);


        example3.setName(example1.getName());

        final List<Key> batchDelete = new ArrayList<>();
        batchDelete.add(example1.getId());
        batchDelete.add(example2.getId());

        TransactionOperator.execute(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                dao.delete(batchDelete);
                tx.commit();
                return null;
            }
        });
        save(example3);
    }

    @Test
    public void testByMemberUserId() throws Exception {
        Organization org1 = TestHelper.createOrganization(false);
        Organization org2 = TestHelper.createOrganization(false);
        Organization org3 = TestHelper.createOrganization(false);
        User user = new User("a@b.com");
        assertNotNull(org1);
        assertNotNull(org2);
        assertNotNull(org3);
        assertNotNull(user);
        Datastore.put(org1, org2, org3, user);
        OrganizationMember om1 = new OrganizationMember(org1, user);
        OrganizationMember om3 = new OrganizationMember(org3, user);
        Datastore.put(om1, om3);

        for (int M = 0; M < 3; ++M) {
            List<Organization> organizations = dao.byMemberUserId(user.getId());
            assertEquals(2, organizations.size());
            assertTrue(organizations.get(0).getId().equals(org1.getId()) || organizations.get(0).getId().equals(org3.getId()));
            assertTrue(organizations.get(1).getId().equals(org1.getId()) || organizations.get(1).getId().equals(org3.getId()));

            //TODO: This is only temporary as a proof of concept
            List<Organization> organizationsForUser = OrganizationServiceFactory.getOrganizationService().getOrganizationsForUser(user.getId());
            assertEquals(organizations.size(), organizationsForUser.size());
            assertTrue(organizations.get(0).getId().equals(organizationsForUser.get(0).getId()) || organizations.get(0).getId().equals(organizationsForUser.get(1).getId()));
            assertTrue(organizations.get(1).getId().equals(organizationsForUser.get(0).getId()) || organizations.get(1).getId().equals(organizationsForUser.get(1).getId()));
        }
    }

    @Test
    public void testGetUsersOfOrganization() throws Exception {
        Organization org1 = TestHelper.createOrganization(false);
        Organization org2 = TestHelper.createOrganization(false);
        User user1 = new User("a@b.com");
        User user2 = new User("b@b.com");
        User user3 = new User("c@b.com");
        assertNotNull(org1);
        assertNotNull(org2);

        Datastore.put(org1, org2, user1, user2, user3);

        Datastore.put(
                new OrganizationMember(org1, user1),
                new OrganizationMember(org1, user2),
                new OrganizationMember(org1, user3),
                new OrganizationMember(org2, user2)
        );

        for (int M = 0; M < 3; ++M) {
            assertIdsEqual(Arrays.asList(user1, user2, user3), dao.getUsersOfOrganization(org1.getId()));
            assertIdsEqual(Arrays.asList(user2), dao.getUsersOfOrganization(org2.getId()));
        }
    }

    @Test
    public void testGetGroupsOfOrganization() throws Exception {
        Organization org1 = TestHelper.createOrganization(false);
        Organization org2 = TestHelper.createOrganization(false);
        Group group1 = new Group("a@b.com");
        Group group2 = new Group("b@b.com");
        Group group3 = new Group("c@b.com");
        assertNotNull(org1);
        assertNotNull(org2);

        Datastore.put(org1, org2, group1, group2, group3);

        Datastore.put(
                new OrganizationMember(org1, group1),
                new OrganizationMember(org1, group2),
                new OrganizationMember(org1, group3),
                new OrganizationMember(org2, group2)
        );

        for (int M = 0; M < 3; ++M) {
            assertIdsEqual(Arrays.asList(group1, group2, group3), dao.getGroupsOfOrganization(org1.getId()));
            assertIdsEqual(Arrays.asList(group2), dao.getGroupsOfOrganization(org2.getId()));
        }
    }

    @Test
    public void testAddUserToOrganization() throws Exception {
        Organization org1 = createExample();
        Organization org2 = createExample();
        User user1 = new User("whatever");
        User user2 = new User("whoever");
        Datastore.put(org1, org2, user1, user2);

        assertTrue(dao.addUserToOrganization(org1.getId(), user1.getId()));
        assertTrue(dao.addUserToOrganization(org1.getId(), user2.getId()));
        assertFalse("double add1", dao.addUserToOrganization(org1.getId(), user1.getId()));
        assertFalse("double add1", dao.addUserToOrganization(org1.getId(), user2.getId()));
        assertTrue(dao.addUserToOrganization(org2.getId(), user1.getId()));
        assertFalse("double add1.2", dao.addUserToOrganization(org1.getId(), user1.getId()));
        assertFalse("double add2", dao.addUserToOrganization(org2.getId(), user1.getId()));

        assertIdsEqual(Arrays.asList(user1, user2), dao.getUsersOfOrganization(org1.getId()));
        assertIdsEqual(Arrays.asList(user1), dao.getUsersOfOrganization(org2.getId()));
    }

    @Test
    public void testRemoveUserFromOrganization() throws Exception {
        Organization org1 = createExample();
        Organization org2 = createExample();
        User user1 = new User("whatever");
        User user2 = new User("whoever");
        Datastore.put(org1, org2, user1, user2);

        assertTrue(dao.addUserToOrganization(org1.getId(), user1.getId()));
        assertTrue(dao.addUserToOrganization(org1.getId(), user2.getId()));
        assertIdsEqual(Arrays.asList(user1, user2), dao.getUsersOfOrganization(org1.getId()));
        dao.removeUserFromOrganization(org1.getId(), user1.getId());
        assertIdsEqual(Arrays.asList(user2), dao.getUsersOfOrganization(org1.getId()));
        dao.removeUserFromOrganization(org1.getId(), user2.getId());
        assertTrue(dao.getUsersOfOrganization(org1.getId()).isEmpty());
    }

    @Test
    public void testAddGroupToOrganization() throws Exception {
        Organization org1 = createExample();
        Organization org2 = createExample();
        Group group1 = new Group("whatever");
        Group group2 = new Group("whoever");
        Datastore.put(org1, org2, group1, group2);

        assertTrue(dao.addGroupToOrganization(org1.getId(), group1.getId()));
        assertTrue(dao.addGroupToOrganization(org1.getId(), group2.getId()));
        assertFalse("double add1", dao.addGroupToOrganization(org1.getId(), group1.getId()));
        assertFalse("double add1", dao.addGroupToOrganization(org1.getId(), group2.getId()));
        assertTrue(dao.addGroupToOrganization(org2.getId(), group1.getId()));
        assertFalse("double add1.2", dao.addGroupToOrganization(org1.getId(), group1.getId()));
        assertFalse("double add2", dao.addGroupToOrganization(org2.getId(), group1.getId()));

        assertIdsEqual(Arrays.asList(group1, group2), dao.getGroupsOfOrganization(org1.getId()));
        assertIdsEqual(Arrays.asList(group1), dao.getGroupsOfOrganization(org2.getId()));
    }

    @Test
    public void testRemoveGroupFromOrganization() throws Exception {
        Organization org1 = createExample();
        Organization org2 = createExample();
        Group group1 = new Group("whatever");
        Group group2 = new Group("whoever");
        Datastore.put(org1, org2, group1, group2);

        assertTrue(dao.addGroupToOrganization(org1.getId(), group1.getId()));
        assertTrue(dao.addGroupToOrganization(org1.getId(), group2.getId()));
        assertIdsEqual(Arrays.asList(group1, group2), dao.getGroupsOfOrganization(org1.getId()));
        dao.removeGroupFromOrganization(org1.getId(), group1.getId());
        assertIdsEqual(Arrays.asList(group2), dao.getGroupsOfOrganization(org1.getId()));
        dao.removeGroupFromOrganization(org1.getId(), group2.getId());
        assertTrue(dao.getGroupsOfOrganization(org1.getId()).isEmpty());
    }
}