package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
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

    private OrganizationMember createMember(Key userId, Key organizationId) throws ModelException {
        OrganizationMember om = new OrganizationMember();
        om.getUserRef().setKey(userId);
        om.getOrganizationRef().setKey(organizationId);
        dao.save(om);
        return om;
    }

    @Test
    public void testByOrganizationId() throws Exception {
        Key userId1 = Datastore.allocateId(User.class);
        Key userId2 = Datastore.allocateId(User.class);
        Key userId3 = Datastore.allocateId(User.class);
        Key organizationId1 = Datastore.allocateId(Organization.class);
        Key organizationId2 = Datastore.allocateId(Organization.class);
        Key organizationId3 = Datastore.allocateId(Organization.class);

        createMember(userId1, organizationId1);
        createMember(userId1, organizationId2);
        createMember(userId1, organizationId3);

        createMember(userId2, organizationId1);
        createMember(userId2, organizationId3);

        createMember(userId3, organizationId2);
        createMember(userId3, organizationId3);

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

        OrganizationMember mu1o1 = createMember(userId1, organizationId1);
        OrganizationMember mu1o2 = createMember(userId1, organizationId2);
        OrganizationMember mu1o3 = createMember(userId1, organizationId3);

        OrganizationMember mu2o1 = createMember(userId2, organizationId1);
        OrganizationMember mu2o3 = createMember(userId2, organizationId3);

        OrganizationMember mu3o2 = createMember(userId3, organizationId2);
        OrganizationMember mu3o3 = createMember(userId3, organizationId3);

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

    private HashSet<Key> toUserKeys(List<OrganizationMember> members) {
        HashSet<Key> userIds = new HashSet<>();
        for (OrganizationMember member : members) {
            assertNotNull(member.getUserRef().getKey());
            userIds.add(member.getUserRef().getKey());
        }
        return userIds;
    }
}