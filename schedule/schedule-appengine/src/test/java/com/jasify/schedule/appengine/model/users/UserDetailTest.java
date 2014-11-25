package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserDetailTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testHashCode() throws Exception {
        HashSet<UserDetail> userSet = new HashSet<>();
        UserDetail u = new UserDetail();
        assertTrue(userSet.add(u));
        assertFalse(userSet.add(new UserDetail()));
        UserDetail u1 = new UserDetail();
        Key id = Datastore.allocateId(UserDetail.class);
        u1.setId(id);
        assertTrue(userSet.add(u1));
        UserDetail e = new UserDetail();
        e.setId(id);
        assertFalse(userSet.add(e));
    }

}