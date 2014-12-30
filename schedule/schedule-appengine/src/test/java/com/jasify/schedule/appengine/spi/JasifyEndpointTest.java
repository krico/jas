package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
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
        JasifyUser user = new JasifyUser("", 1, false);
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
        JasifyUser user = new JasifyUser("", 1, false);
        JasifyEndpoint.mustBeSameUserOrAdmin(user, 2);
    }

    @Test
    public void testMustBeSameUserSameUser() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyUser user = new JasifyUser("", 1, false);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 1));
    }

    @Test
    public void testMustBeSameUserOrAdminWithAdmin() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyUser user = new JasifyUser("", 1, true);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 2));
    }

    @Test
    public void testApiInfoNoUser() throws Exception {
        replay(userService);
        JasifyInfo info = endpoint.apiInfo(null);
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertFalse(info.isAuthenticated());
    }

    @Test
    public void testApiInfoWithUser() throws Exception {
        replay(userService);
        JasifyInfo info = endpoint.apiInfo(new JasifyUser("test@foo.bar", 1, false));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
    }

    @Test
    public void testApiInfoWithAdmin() throws Exception {
        replay(userService);
        JasifyInfo info = endpoint.apiInfo(new JasifyUser("test@foo.bar", 1, true));
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
        JasifyUser user = new JasifyUser("", 5, false);
        endpoint.listLogins(user, 1);
    }

    @Test
    public void testListLoginsSame() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyUser user = new JasifyUser("", 5, false);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLoginsOtherAdmin() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyUser user = new JasifyUser("", 2, true);
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
        JasifyUser user = new JasifyUser("", u1.getId().getId(), false);
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
        endpoint.removeLogin(null, 1, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveLoginNonExistentUser() throws Exception {
        expect(userService.get(EasyMock.anyLong())).andReturn(null);
        replay(userService);
        endpoint.removeLogin(new JasifyUser("a@b", 1, false), 1, 1);
    }

    @Test
    public void testRemoveLoginThatDoesNotExist() throws Exception {
        expect(userService.get(EasyMock.anyLong())).andReturn(new User());
        expect(userService.getLogin(EasyMock.anyLong(), EasyMock.anyLong())).andReturn(null);
        replay(userService);
        endpoint.removeLogin(new JasifyUser("a@b", 1, false), 1, 1);
    }

    @Test
    public void testRemoveLogin() throws Exception {
        User user = new User();
        expect(userService.get(EasyMock.anyLong())).andReturn(user);
        UserLogin login = new UserLogin();
        expect(userService.getLogin(EasyMock.anyLong(), EasyMock.anyLong())).andReturn(login);
        userService.removeLogin(user, login);
        expectLastCall();
        replay(userService);
        endpoint.removeLogin(new JasifyUser("a@b", 1, false), 1, 1);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveLoginOtherUserThrows() throws Exception {
        replay(userService);
        endpoint.removeLogin(new JasifyUser("a@b", 1, false), 2, 1);
    }

    @Test
    public void testRemoveLoginOtherUserAdmin() throws Exception {
        User user = new User();
        expect(userService.get(EasyMock.anyLong())).andReturn(user);
        UserLogin login = new UserLogin();
        expect(userService.getLogin(EasyMock.anyLong(), EasyMock.anyLong())).andReturn(login);
        userService.removeLogin(user, login);
        expectLastCall();
        replay(userService);
        endpoint.removeLogin(new JasifyUser("a@b", 1, true), 2, 1);
    }

}