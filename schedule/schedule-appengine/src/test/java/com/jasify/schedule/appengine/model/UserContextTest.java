package com.jasify.schedule.appengine.model;

import com.google.api.server.spi.auth.common.User;
import com.jasify.schedule.appengine.TestHelper;
import org.easymock.EasyMock;
import org.junit.Test;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;

public class UserContextTest {
    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(UserContext.class);
    }


    @Test
    public void testSetContext() throws Exception {
        assertNull(UserContext.getCurrentUser());
        assertNull(UserContext.getCurrentRequest());
        assertNull(UserContext.getCurrentResponse());
        UserSession session = EasyMock.createMock(UserSession.class);
        ServletRequest request = EasyMock.createMock(ServletRequest.class);
        ServletResponse response = EasyMock.createMock(ServletResponse.class);
        EasyMock.expect(session.isAdmin()).andReturn(true);
        EasyMock.replay(session);

        UserContext.setContext(session, request, response);
        assertEquals(session, UserContext.getCurrentUser());
        assertEquals(request, UserContext.getCurrentRequest());
        assertEquals(response, UserContext.getCurrentResponse());
        assertTrue(UserContext.isCurrentUserAdmin());
        EasyMock.verify(session);

        UserContext.setCurrentUser(null);
        assertNull(UserContext.getCurrentUser());
        assertEquals(request, UserContext.getCurrentRequest());
        assertEquals(response, UserContext.getCurrentResponse());
        assertFalse(UserContext.isCurrentUserAdmin());

        UserContext.clearContext();
        assertNull(UserContext.getCurrentUser());
        assertNull(UserContext.getCurrentRequest());
        assertNull(UserContext.getCurrentResponse());
        assertFalse(UserContext.isCurrentUserAdmin());
    }
}