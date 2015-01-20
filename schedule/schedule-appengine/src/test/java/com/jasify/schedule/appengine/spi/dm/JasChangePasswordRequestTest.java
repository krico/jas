package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JasChangePasswordRequestTest {

    @Before
    public void before() {
        TestHelper.initializeDatastore();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasChangePasswordRequest.class);
    }

    @Test
    public void testDefaultConstructor() {
        JasChangePasswordRequest jasChangePasswordRequest = new JasChangePasswordRequest();
        assertNull(jasChangePasswordRequest.getUserId());
        assertNull(jasChangePasswordRequest.getOldPassword());
        assertNull(jasChangePasswordRequest.getNewPassword());
    }

    @Test
    public void testParameterConstructor() {
        Key userId = Datastore.allocateId(User.class);
        String oldPassword = "TestOldPassword";
        String newPassword = "TestNewPassword";
        JasChangePasswordRequest jasChangePasswordRequest = new JasChangePasswordRequest(userId, oldPassword, newPassword);
        assertEquals(userId, jasChangePasswordRequest.getUserId());
        assertEquals(oldPassword, jasChangePasswordRequest.getOldPassword());
        assertEquals(newPassword, jasChangePasswordRequest.getNewPassword());
    }

    @Test
    public void testUserId() {
        Key userId = Datastore.allocateId(User.class);
        JasChangePasswordRequest jasChangePasswordRequest = new JasChangePasswordRequest();
        jasChangePasswordRequest.setUserId(userId);
        assertEquals(userId, jasChangePasswordRequest.getUserId());
    }

    @Test
    public void testOldPassword() {
        String oldPassword = "TestOldPassword";
        JasChangePasswordRequest jasChangePasswordRequest = new JasChangePasswordRequest();
        jasChangePasswordRequest.setOldPassword(oldPassword);
        assertEquals(oldPassword, jasChangePasswordRequest.getOldPassword());
    }

    @Test
    public void testNewPassword() {
        String newPassword = "TestNewPassword";
        JasChangePasswordRequest jasChangePasswordRequest = new JasChangePasswordRequest();
        jasChangePasswordRequest.setNewPassword(newPassword);
        assertEquals(newPassword, jasChangePasswordRequest.getNewPassword());
    }
}