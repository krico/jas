package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JasLoginResponseTest {
    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

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

    @Test
    public void testUserConstructor() {
        String sessionId = RandomStringUtils.randomAscii(10);
        Key userId = KeyFactory.createKey("User", 98);
        String name = RandomStringUtils.randomAscii(18);
        boolean admin = new Random().nextBoolean();
        User user = new User();
        user.setName(name);
        user.setAdmin(admin);
        user.setId(userId);

        UserSession userSession = EasyMock.createMock(UserSession.class);
        EasyMock.expect(userSession.getSessionId()).andReturn(sessionId).anyTimes();
        EasyMock.replay(userSession);

        JasLoginResponse response = new JasLoginResponse(user, userSession);

        assertEquals(KeyUtil.keyToString(userId), response.getUserId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(name, response.getName());
        assertEquals(admin, response.isAdmin());
    }
}