package com.jasify.schedule.appengine.endpoints;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;

public class JasifyEndpointTest {
    private JasifyEndpoint endpoint;

    @Before
    public void createEndpoint() {
        TestHelper.initializeDatastore();
        endpoint = new JasifyEndpoint();
    }

    @After
    public void cleanup() {
    }

    @Test
    public void testSettingsNoUser() throws Exception {
        Settings settings = endpoint.settings(null);
        assertNotNull(settings);
        assertNotNull(settings.getVersion());
        assertFalse(settings.isAuthenticated());
    }

    @Test
    public void testSettingsWithUser() throws Exception {
        Settings settings = endpoint.settings(new JasifyUser("test@foo.bar", 1, false));
        assertNotNull(settings);
        assertNotNull(settings.getVersion());
        assertTrue(settings.isAuthenticated());
    }

    @Test(expected = UnauthorizedException.class)
    public void testListLoginsNoUserThrows() throws Exception {
        endpoint.listLogins(null, 1);
    }

    @Test(expected = ForbiddenException.class)
    public void testListLoginsOtherUserThrows() throws Exception {
        JasifyUser user = new JasifyUser("", 5, false);
        endpoint.listLogins(user, 1);
    }

    @Test
    public void testListLoginsSame() throws Exception {
        JasifyUser user = new JasifyUser("", 5, false);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLoginsOtherAdmin() throws Exception {
        JasifyUser user = new JasifyUser("", 2, true);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLogins() throws Exception {
        User u1 = new User();
        u1.setName("test");
        UserLogin origLogin = new UserLogin("Google", "1234");
        UserServiceFactory.getUserService().create(u1, origLogin);
        JasifyUser user = new JasifyUser("", u1.getId().getId(), false);
        List<UserLogin> logins = endpoint.listLogins(user, u1.getId().getId());
        assertNotNull(logins);
        assertEquals(1, logins.size());
        UserLogin userLogin = logins.get(0);
        assertEquals(origLogin.getProvider(), userLogin.getProvider());
        assertEquals(origLogin.getUserId(), userLogin.getUserId());
    }

}