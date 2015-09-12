package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.jasify.schedule.appengine.AssertionHelper.assertIdsEqual;
import static junit.framework.TestCase.*;

public class OrganizationMemberDaoTest {
    private OrganizationMemberDao dao;

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
        dao = new OrganizationMemberDao();
    }

    private HashSet<Key> toUserKeys(List<OrganizationMember> members) {
        HashSet<Key> userIds = new HashSet<>();
        for (OrganizationMember member : members) {
            assertNotNull(member.getUserRef().getKey());
            userIds.add(member.getUserRef().getKey());
        }
        return userIds;
    }

    @Test
    public void testByUserId() throws Exception {
        Key key = Datastore.allocateId(User.class);

        OrganizationMember member1 = new OrganizationMember();
        member1.getUserRef().setKey(key);
        OrganizationMember member2 = new OrganizationMember();
        member2.getUserRef().setKey(Datastore.allocateId(User.class));
        OrganizationMember member3 = new OrganizationMember();
        member3.getUserRef().setKey(key);

        dao.save(Arrays.asList(member1, member2, member3));


        for (int M = 0; M < 3; ++M) {
            List<OrganizationMember> results = dao.byUserId(key);
            assertNotNull(results);
            assertEquals(2, results.size());
            assertIdsEqual(Arrays.asList(member1, member3), results);
        }
    }

    private OrganizationMember createUserMember(Key userId, Key organizationId) throws ModelException {
        OrganizationMember om = new OrganizationMember();
        om.getUserRef().setKey(userId);
        om.getOrganizationRef().setKey(organizationId);
        dao.save(om);
        return om;
    }

    private OrganizationMember createGroupMember(Key userId, Key organizationId) throws ModelException {
        OrganizationMember om = new OrganizationMember();
        om.getGroupRef().setKey(userId);
        om.getOrganizationRef().setKey(organizationId);
        dao.save(om);
        return om;
    }

    @Test
    public void byOrganizationIdAsKeys() throws Exception {
        Key userId1 = Datastore.allocateId(User.class);
        Key userId2 = Datastore.allocateId(User.class);
        Key organizationId = Datastore.allocateId(Organization.class);
        createUserMember(userId1, organizationId);
        createUserMember(userId2, organizationId);
        List<Key> userIds = dao.byOrganizationIdAsKeys(organizationId);
        assertEquals(2, userIds.size());
    }

    @Test
    public void testByOrganizationId() throws Exception {
        Key userId1 = Datastore.allocateId(User.class);
        Key userId2 = Datastore.allocateId(User.class);
        Key userId3 = Datastore.allocateId(User.class);
        Key organizationId1 = Datastore.allocateId(Organization.class);
        Key organizationId2 = Datastore.allocateId(Organization.class);
        Key organizationId3 = Datastore.allocateId(Organization.class);

        createUserMember(userId1, organizationId1);
        createUserMember(userId1, organizationId2);
        createUserMember(userId1, organizationId3);

        createUserMember(userId2, organizationId1);
        createUserMember(userId2, organizationId3);

        createUserMember(userId3, organizationId2);
        createUserMember(userId3, organizationId3);

        for (int M = 0; M < 3; ++M) {
            HashSet<Key> userIds = toUserKeys(dao.byOrganizationId(organizationId1));
            assertEquals(2, userIds.size());
            assertTrue(userIds.contains(userId1));
            assertTrue(userIds.contains(userId2));

            userIds = toUserKeys(dao.byOrganizationId(organizationId2));
            assertEquals(2, userIds.size());
            assertTrue(userIds.contains(userId1));
            assertTrue(userIds.contains(userId3));

            userIds = toUserKeys(dao.byOrganizationId(organizationId3));
            assertEquals(3, userIds.size());
            assertTrue(userIds.contains(userId1));
            assertTrue(userIds.contains(userId2));
            assertTrue(userIds.contains(userId3));
        }
    }

    @Test
    public void testByOrganizationIdAndUserId() throws Exception {
        Key userId1 = Datastore.allocateId(User.class);
        Key userId2 = Datastore.allocateId(User.class);
        Key userId3 = Datastore.allocateId(User.class);
        Key organizationId1 = Datastore.allocateId(Organization.class);
        Key organizationId2 = Datastore.allocateId(Organization.class);
        Key organizationId3 = Datastore.allocateId(Organization.class);

        OrganizationMember mu1o1 = createUserMember(userId1, organizationId1);
        OrganizationMember mu1o2 = createUserMember(userId1, organizationId2);
        OrganizationMember mu1o3 = createUserMember(userId1, organizationId3);

        OrganizationMember mu2o1 = createUserMember(userId2, organizationId1);
        OrganizationMember mu2o3 = createUserMember(userId2, organizationId3);

        OrganizationMember mu3o2 = createUserMember(userId3, organizationId2);
        OrganizationMember mu3o3 = createUserMember(userId3, organizationId3);

        assertIdsEqual(mu1o1, dao.byOrganizationIdAndUserId(organizationId1, userId1));
        assertIdsEqual(mu1o2, dao.byOrganizationIdAndUserId(organizationId2, userId1));
        assertIdsEqual(mu1o3, dao.byOrganizationIdAndUserId(organizationId3, userId1));

        assertIdsEqual(mu2o1, dao.byOrganizationIdAndUserId(organizationId1, userId2));
        assertNull(dao.byOrganizationIdAndUserId(organizationId2, userId2));
        assertIdsEqual(mu2o3, dao.byOrganizationIdAndUserId(organizationId3, userId2));

        assertNull(dao.byOrganizationIdAndUserId(organizationId1, userId3));
        assertIdsEqual(mu3o2, dao.byOrganizationIdAndUserId(organizationId2, userId3));
        assertIdsEqual(mu3o3, dao.byOrganizationIdAndUserId(organizationId3, userId3));
    }

    @Test
    public void testByOrganizationIdAndGroupId() throws Exception {
        Key groupId1 = Datastore.allocateId(Group.class);
        Key groupId2 = Datastore.allocateId(Group.class);
        Key groupId3 = Datastore.allocateId(Group.class);
        Key organizationId1 = Datastore.allocateId(Organization.class);
        Key organizationId2 = Datastore.allocateId(Organization.class);
        Key organizationId3 = Datastore.allocateId(Organization.class);

        OrganizationMember mg1o1 = createGroupMember(groupId1, organizationId1);
        OrganizationMember mg1o2 = createGroupMember(groupId1, organizationId2);
        OrganizationMember mg1o3 = createGroupMember(groupId1, organizationId3);

        OrganizationMember mg2o1 = createGroupMember(groupId2, organizationId1);
        OrganizationMember mg2o3 = createGroupMember(groupId2, organizationId3);

        OrganizationMember mg3o2 = createGroupMember(groupId3, organizationId2);
        OrganizationMember mg3o3 = createGroupMember(groupId3, organizationId3);

        assertIdsEqual(mg1o1, dao.byOrganizationIdAndGroupId(organizationId1, groupId1));
        assertIdsEqual(mg1o2, dao.byOrganizationIdAndGroupId(organizationId2, groupId1));
        assertIdsEqual(mg1o3, dao.byOrganizationIdAndGroupId(organizationId3, groupId1));

        assertIdsEqual(mg2o1, dao.byOrganizationIdAndGroupId(organizationId1, groupId2));
        assertNull(dao.byOrganizationIdAndGroupId(organizationId2, groupId2));
        assertIdsEqual(mg2o3, dao.byOrganizationIdAndGroupId(organizationId3, groupId2));

        assertNull(dao.byOrganizationIdAndGroupId(organizationId1, groupId3));
        assertIdsEqual(mg3o2, dao.byOrganizationIdAndGroupId(organizationId2, groupId3));
        assertIdsEqual(mg3o3, dao.byOrganizationIdAndGroupId(organizationId3, groupId3));
    }
}