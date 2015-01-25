package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class JasAddUserRequestTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasAddUserRequest.class);
    }

    @Test
    public void testPassword() {
        JasAddUserRequest jasAddUserRequest = new JasAddUserRequest();
        String password = "Zebra";
        jasAddUserRequest.setPassword(password);
        assertEquals(password, jasAddUserRequest.getPassword());
    }

    @Test
    public void testUser() {
        JasAddUserRequest jasAddUserRequest = new JasAddUserRequest();
        User user = new User();
        jasAddUserRequest.setUser(user);
        assertEquals(user, jasAddUserRequest.getUser());
    }

    @Test
    public void testLogin() {
        JasAddUserRequest jasAddUserRequest = new JasAddUserRequest();
        UserLogin userLogin = new UserLogin();
        jasAddUserRequest.setLogin(userLogin);
        assertEquals(userLogin, jasAddUserRequest.getLogin());
    }
}