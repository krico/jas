package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class JasifyEndpointTest {
    @Mock
    private UserService userService;

    @TestSubject
    private JasifyEndpoint endpoint = new JasifyEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
        EasyMock.verify(userService);
    }

    @Test(expected = UnauthorizedException.class)
    public void testMustBeLoggedInThrowsNonAuthorizedOnNull() throws UnauthorizedException {
        replay(userService);
        JasifyEndpoint.mustBeLoggedIn(null);
    }

    @Test
    public void testMustBeLoggedIn() throws UnauthorizedException {
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 1, false);
        assertEquals(user, JasifyEndpoint.mustBeLoggedIn(user));
    }

    @Test(expected = UnauthorizedException.class)
    public void testMustBeSameUserOrAdminThrowsNonAuthorizedOnNull() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpoint.mustBeSameUserOrAdmin(null, 1);
    }

    @Test(expected = ForbiddenException.class)
    public void testMustBeSameUserOrAdminThrowsForbiddenWhenNotSameUser() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 1, false);
        JasifyEndpoint.mustBeSameUserOrAdmin(user, 2);
    }

    @Test
    public void testMustBeSameUserSameUser() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 1, false);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 1));
    }

    @Test
    public void testMustBeSameUserOrAdminWithAdmin() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 1, true);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 2));
    }

    @Test
    public void testApiInfoNoUser() throws Exception {
        replay(userService);
        ApiInfo info = endpoint.getApiInfo(null);
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertFalse(info.isAuthenticated());
    }

    @Test
    public void testApiInfoWithUser() throws Exception {
        replay(userService);
        ApiInfo info = endpoint.getApiInfo(new JasifyEndpointUser("test@foo.bar", 1, false));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
    }

    @Test
    public void testApiInfoWithAdmin() throws Exception {
        replay(userService);
        ApiInfo info = endpoint.getApiInfo(new JasifyEndpointUser("test@foo.bar", 1, true));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertTrue(info.isAdmin());
    }

    @Test(expected = UnauthorizedException.class)
    public void testListLoginsNoUserThrows() throws Exception {
        replay(userService);
        endpoint.listLogins(null, 1);
    }

    @Test(expected = ForbiddenException.class)
    public void testListLoginsOtherUserThrows() throws Exception {
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 5, false);
        endpoint.listLogins(user, 1);
    }

    @Test
    public void testListLoginsSame() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 5, false);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLoginsOtherAdmin() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyEndpointUser user = new JasifyEndpointUser("", 2, true);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLogins() throws Exception {

        List<UserLogin> ret = new ArrayList<>();
        ret.add(new UserLogin("Google", "1234"));

        expect(userService.getUserLogins(23)).andReturn(ret);
        replay(userService);
        User u1 = new User();
        u1.setId(Datastore.createKey(User.class, 23));
        JasifyEndpointUser user = new JasifyEndpointUser("", u1.getId().getId(), false);
        List<UserLogin> logins = endpoint.listLogins(user, u1.getId().getId());
        assertNotNull(logins);
        assertEquals(1, logins.size());
        UserLogin userLogin = logins.get(0);
        assertEquals(ret.get(0).getProvider(), userLogin.getProvider());
        assertEquals(ret.get(0).getUserId(), userLogin.getUserId());
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveLoginNoUser() throws Exception {
        replay(userService);
        endpoint.removeLogin(null, "");
    }

    @Test
    public void testRemoveLoginThatDoesNotExist() throws Exception {
        expect(userService.getLogin(EasyMock.<Key>anyObject())).andReturn(null);
        replay(userService);
        endpoint.removeLogin(new JasifyEndpointUser("a@b", 1, false), KeyFactory.keyToString(Datastore.createKey(UserLogin.class, 23)));
    }

    @Test
    public void testRemoveLogin() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 1));
        expect(userService.getLogin(login.getId())).andReturn(login);
        userService.removeLogin(login.getId());
        expectLastCall();
        replay(userService);
        endpoint.removeLogin(new JasifyEndpointUser("a@b", 1, false), KeyFactory.keyToString(login.getId()));
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveLoginOtherUserThrows() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 2));
        expect(userService.getLogin(login.getId())).andReturn(login);
        replay(userService);
        endpoint.removeLogin(new JasifyEndpointUser("a@b", 1, false), KeyFactory.keyToString(login.getId()));
    }

    @Test
    public void testRemoveLoginOtherUserAdmin() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 1));
        expect(userService.getLogin(login.getId())).andReturn(login);
        userService.removeLogin(login.getId());
        expectLastCall();
        replay(userService);
        endpoint.removeLogin(new JasifyEndpointUser("a@b", 1, true), KeyFactory.keyToString(login.getId()));
    }

}