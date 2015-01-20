package com.jasify.schedule.appengine.spi.dm;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JasLoginResponseTest {
    @Test
    public void testEntityWellDefined() {
        DmTestHelper.assertWellFormedEntity(JasLoginResponse.class);
    }

    @Test
    public void testSessionId() {
        JasLoginResponse jasLoginResponse = new JasLoginResponse();
        String sessionId = "TestId";
        jasLoginResponse.setSessionId(sessionId);
        assertEquals(sessionId, jasLoginResponse.getSessionId());
    }

    @Test
    public void testUserId() {
        JasLoginResponse jasLoginResponse = new JasLoginResponse();
        String userId = "UserId";
        jasLoginResponse.setUserId(userId);
        assertEquals(userId, jasLoginResponse.getUserId());
    }

    @Test
    public void testName() {
        JasLoginResponse jasLoginResponse = new JasLoginResponse();
        String name = "nameId";
        jasLoginResponse.setName(name);
        assertEquals(name, jasLoginResponse.getName());
    }

    @Test
    public void testAdmin() {
        JasLoginResponse jasLoginResponse = new JasLoginResponse();
        jasLoginResponse.setAdmin(true);
        assertTrue(jasLoginResponse.isAdmin());
    }
}