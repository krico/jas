package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class JasLoginRequestTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasLoginRequest.class);
    }

    @Test
    public void testPassword() {
        JasLoginRequest jasLoginRequest = new JasLoginRequest();
        String password = "Zebra";
        jasLoginRequest.setPassword(password);
        assertEquals(password, jasLoginRequest.getPassword());
    }

    @Test
    public void testUsername() {
        JasLoginRequest jasLoginRequest = new JasLoginRequest();
        String username = "Horse";
        jasLoginRequest.setUsername(username);
        assertEquals(username, jasLoginRequest.getUsername());
    }

    @Test
    public void testConstructor() {
        String username = "Fly";
        String password = "Spider";
        JasLoginRequest jasLoginRequest = new JasLoginRequest(username, password);
        assertEquals(username, jasLoginRequest.getUsername());
        assertEquals(password, jasLoginRequest.getPassword());
    }
}