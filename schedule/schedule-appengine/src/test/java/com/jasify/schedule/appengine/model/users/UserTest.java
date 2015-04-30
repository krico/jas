package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashSet;

import static junit.framework.TestCase.assertEquals;
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

    @Test
    public void testReadFromUserLogin() {
        UserLogin userLogin = new UserLogin();
        String email = "a@com";
        userLogin.setEmail(email);
        String realName = "Someone Nice";
        userLogin.setRealName(realName);
        User user = new User(userLogin);
        assertEquals(email, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(realName, user.getRealName());
    }

    @Test
    public void testDisplayNameWithRealNameSet() {
        User user = new User("Name");
        user.setRealName("RealName");
        assertEquals(user.getRealName(), user.getDisplayName());
    }

    @Test
    public void testDisplayNameWithRealNameUnset() {
        User user = new User("Name");
        assertEquals(user.getName(), user.getDisplayName());
    }
}