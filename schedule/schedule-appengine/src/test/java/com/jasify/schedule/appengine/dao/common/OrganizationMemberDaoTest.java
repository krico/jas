package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.List;

import static com.jasify.schedule.appengine.AssertionHelper.assertIdsEqual;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
}