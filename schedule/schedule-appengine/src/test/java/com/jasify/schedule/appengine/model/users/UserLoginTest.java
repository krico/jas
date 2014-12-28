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
import static org.junit.Assert.*;

public class UserLoginTest {
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
        HashSet<UserLogin> loginSet = new HashSet<>();
        UserLogin login1 = new UserLogin();
        assertTrue(loginSet.add(login1));
        assertFalse(loginSet.add(new UserLogin()));
        UserLogin login2 = new UserLogin();
        Key id = Datastore.allocateId(UserLogin.class);
        login2.setId(id);
        assertTrue(loginSet.add(login2));
        UserLogin e = new UserLogin();
        e.setId(id);
        assertFalse(loginSet.add(e));
    }

    @Test
    public void testCompare(){
        UserLogin u1 = new UserLogin("a","1");
        UserLogin u2 = new UserLogin("a","2");
        UserLogin u3 = new UserLogin("b","1");

        assertEquals(0, u1.compareTo(u1));
        assertEquals(0, u2.compareTo(u2));
        assertEquals(0, u3.compareTo(u3));
        assertEquals(-1, u1.compareTo(u3));
        assertEquals(1, u3.compareTo(u1));
        assertEquals(-1, u1.compareTo(u2));
        assertEquals(1, u2.compareTo(u1));
        assertEquals(-1, u2.compareTo(u3));
        assertEquals(1, u3.compareTo(u2));
    }
    @Test
    public void testEmptySave(){
        UserLogin ul = new UserLogin();
        Datastore.put(ul);
    }

    @Test(expected = NullPointerException.class)
    public void testOwnerSaveWithNoIdThrows(){
         new UserLogin(new User());
    }

}