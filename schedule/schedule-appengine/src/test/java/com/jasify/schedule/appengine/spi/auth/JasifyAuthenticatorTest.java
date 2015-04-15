package com.jasify.schedule.appengine.spi.auth;

import com.google.api.server.spi.auth.common.User;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class JasifyAuthenticatorTest {

    private JasifyAuthenticator authenticator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserSession session;

    @Before
    public void create() {
        authenticator = new JasifyAuthenticator();
    }

    @After
    public void verifyMocks() {
        UserContext.clearContext();
        verify(request);
        verify(session);
    }

    @Test
    public void testAuthenticateAdmin() throws Exception {
        long userId = 1l;
        expect(session.getUserId()).andReturn(userId).anyTimes();
        expect(session.isAdmin()).andReturn(true).anyTimes();
        expect(session.isOrgMember()).andReturn(true).anyTimes();
        replay(session);
        replay(request);
        assertNull(authenticator.authenticate(request));
        UserContext.setContext(session, null, null);
        User user = authenticator.authenticate(request);
        assertTrue(user instanceof JasifyEndpointUser);
        JasifyEndpointUser jas = (JasifyEndpointUser) user;
        assertEquals(userId, jas.getUserId());
        assertTrue(jas.isAdmin());
        assertTrue(jas.isOrgMember());
    }
}