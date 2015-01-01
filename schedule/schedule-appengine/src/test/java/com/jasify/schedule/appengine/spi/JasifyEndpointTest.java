package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.LoginFailedException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasChangePasswordRequest;
import com.jasify.schedule.appengine.spi.dm.JasLoginRequest;
import com.jasify.schedule.appengine.spi.dm.JasLoginResponse;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

@RunWith(EasyMockRunner.class)
public class JasifyEndpointTest {
    @Mock
    private UserService userService;
    @Mock
    private Validator<String> usernameValidator;

    @TestSubject
    private JasifyEndpoint endpoint = new JasifyEndpoint();

    private static JasifyEndpointUser newCaller(long id, boolean admin) {
        return new JasifyEndpointUser("a@b", id, admin);
    }

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        UserContext.clearContext();
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
        JasifyEndpointUser user = newCaller(1, false);
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
        JasifyEndpointUser user = newCaller(1, false);
        JasifyEndpoint.mustBeSameUserOrAdmin(user, 2);
    }

    @Test
    public void testMustBeSameUserSameUser() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpointUser user = newCaller(1, false);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 1));
    }

    @Test
    public void testMustBeSameUserOrAdminWithAdmin() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        JasifyEndpointUser user = newCaller(1, true);
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
        ApiInfo info = endpoint.getApiInfo(newCaller(1, false));
        assertNotNull(info);
        assertNotNull(info.getVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
    }

    @Test
    public void testApiInfoWithAdmin() throws Exception {
        replay(userService);
        ApiInfo info = endpoint.getApiInfo(newCaller(1, true));
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
        JasifyEndpointUser user = newCaller(5, false);
        endpoint.listLogins(user, 1);
    }

    @Test
    public void testListLoginsSame() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyEndpointUser user = newCaller(5, false);
        assertNotNull(endpoint.listLogins(user, 5));
    }

    @Test
    public void testListLoginsOtherAdmin() throws Exception {
        expect(userService.getUserLogins(5)).andReturn(Collections.<UserLogin>emptyList());
        replay(userService);
        JasifyEndpointUser user = newCaller(2, true);
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
        JasifyEndpointUser user = newCaller(u1.getId().getId(), false);
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
        endpoint.removeLogin(newCaller(1, false), KeyFactory.keyToString(Datastore.createKey(UserLogin.class, 23)));
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
        endpoint.removeLogin(newCaller(1, false), KeyFactory.keyToString(login.getId()));
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveLoginOtherUserThrows() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 2));
        expect(userService.getLogin(login.getId())).andReturn(login);
        replay(userService);
        endpoint.removeLogin(newCaller(1, false), KeyFactory.keyToString(login.getId()));
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
        endpoint.removeLogin(newCaller(1, true), KeyFactory.keyToString(login.getId()));
    }

    @Test(expected = ConflictException.class)
    public void testCheckUsernameThrows() throws ConflictException {
        expect(usernameValidator.validate(EasyMock.anyString())).andReturn(Arrays.asList("Bad")).once();
        replay(usernameValidator);
        replay(userService);
        endpoint.checkUsername(RandomStringUtils.randomAlphabetic(5));
    }

    @Test
    public void testCheckUsername() throws ConflictException {
        expect(usernameValidator.validate(EasyMock.anyString())).andReturn(Collections.<String>emptyList()).once();
        replay(usernameValidator);
        replay(userService);
        endpoint.checkUsername(RandomStringUtils.randomAlphabetic(5));
        verify(usernameValidator);
    }

    @Test(expected = ForbiddenException.class)
    public void testChangePasswordCheckAuthentication() throws Exception {
        replay(userService);
        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(2, "abc", "def"));
    }

    @Test
    public void testChangePassword() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 1));
        String oldPw = "abc";
        user.setPassword(TypeUtil.toShortBlob(DigestUtil.encrypt(oldPw)));
        expect(userService.get(1)).andReturn(user).times(2);
        expect(userService.setPassword(user, "def")).andReturn(user).times(2);
        replay(userService);

        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(1, oldPw, "def"));
        //admin
        endpoint.changePassword(newCaller(2, true), new JasChangePasswordRequest(1, "", "def"));
    }

    @Test(expected = ForbiddenException.class)
    public void testChangePasswordWrongOld() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 1));
        String oldPw = "abc";
        user.setPassword(TypeUtil.toShortBlob(DigestUtil.encrypt(oldPw)));
        expect(userService.get(1)).andReturn(user);
        replay(userService);

        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(1, oldPw + "x", "def"));
    }

    @Test
    public void testLogin() throws Exception {
        String loginName = RandomStringUtils.randomAlphabetic(5);
        String password = RandomStringUtils.randomAscii(8);

        User user = new User();
        user.setId(Datastore.createKey(User.class, 99));
        user.setName(loginName.toLowerCase());
        expect(userService.login(loginName, password)).andReturn(user).once();
        replay(userService);

        HttpSession httpSession = EasyMock.createMock(HttpSession.class);
        httpSession.setAttribute(EasyMock.anyString(), EasyMock.anyObject());
        expectLastCall();
        replay(httpSession);

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);

        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        expect(httpServletRequest.getSession(true)).andReturn(httpSession);

        replay(httpServletRequest);

        JasLoginResponse response = endpoint.login(httpServletRequest, new JasLoginRequest(loginName, password));
        assertNotNull(response);
        assertEquals(loginName.toLowerCase(), response.getName());
        assertEquals(user.getId().getId(), response.getUserId());


        verify(httpServletRequest);
        verify(httpSession);
    }

    @Test(expected = BadRequestException.class)
    public void testLoginBadParametersNullName() throws Exception {
        replay(userService);

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        replay(httpServletRequest);

        endpoint.login(httpServletRequest, new JasLoginRequest(null, "aaa"));
    }

    @Test(expected = BadRequestException.class)
    public void testLoginBadParametersNullPassword() throws Exception {
        replay(userService);

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        replay(httpServletRequest);

        endpoint.login(httpServletRequest, new JasLoginRequest("aaa", null));
    }

    @Test(expected = UnauthorizedException.class)
    public void testLoginFailed() throws Exception {
        String loginName = RandomStringUtils.randomAlphabetic(5);
        String password = RandomStringUtils.randomAscii(8);

        User user = new User();
        user.setId(Datastore.createKey(User.class, 99));
        user.setName(loginName.toLowerCase());
        expect(userService.login(loginName, password)).andThrow(new LoginFailedException()).once();
        replay(userService);

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        replay(httpServletRequest);

        endpoint.login(httpServletRequest, new JasLoginRequest(loginName, password));
    }

    @Test
    public void testLogout() throws Exception {
        replay(userService);
        UserSession session = EasyMock.createMock(UserSession.class);
        session.invalidate();
        expectLastCall();
        replay(session);
        UserContext.setContext(session, null, null);
        endpoint.logout(newCaller(1, false));
        verify(session);
    }

}