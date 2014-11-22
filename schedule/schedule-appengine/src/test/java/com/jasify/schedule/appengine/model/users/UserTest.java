package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashSet;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserTest {
    @Before
    public void initializeDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testHasPermission() throws Exception {
        User u = new User();
        for (Category permission : Permissions.ALL) {
            assertFalse(u.hasPermission(permission));
        }
        for (Category permission : Permissions.ALL) {
            assertTrue(u.addPermission(permission));
            assertTrue(u.hasPermission(permission));
            assertFalse(u.addPermission(permission));
            assertTrue(u.hasPermission(permission));
        }
    }

    @Test
    public void testHashCode() throws Exception {
        HashSet<User> userSet = new HashSet<>();
        User u = new User();
        assertTrue(userSet.add(u));
        assertFalse(userSet.add(new User()));
        User u1 = new User();
        Key id = Datastore.allocateId(User.class);
        u1.setId(id);
        assertTrue(userSet.add(u1));
        User e = new User();
        e.setId(id);
        assertFalse(userSet.add(e));
    }
}