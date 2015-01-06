package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.jasify.schedule.appengine.validators.Validator;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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

    static JasifyEndpointUser newCaller(long id, boolean admin) {
        return new JasifyEndpointUser("a@b", id, admin);
    }

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanupDatstore() {
        TestHelper.cleanupDatastore();
    }

    @After
    public void cleanup() {
        UserContext.clearContext();
        EasyMock.verify(userService);
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
        httpSession.setAttribute(EasyMock.anyString(), anyObject());
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

    @Test(expected = ForbiddenException.class)
    public void testGetUsersMustBeAdmin() throws UnauthorizedException, ForbiddenException {
        replay(userService);
        endpoint.getUsers(newCaller(1, false), null, null, null, null, null, null);
    }

    @Test
    public void testGetUsersEmpty() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Constants.DEFAULT_ORDER;
        int offset = 0;
        int limit = Constants.DEFAULT_LIMIT;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;

        expect(userService.getTotalUsers()).andReturn(count);
        expect(userService.list(order, offset, limit)).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), null, null, null, null, null, null);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
    }

    @Test
    public void testGetUsersValues() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Query.SortDirection.DESCENDING;
        int offset = 10;
        int limit = Constants.DEFAULT_LIMIT + 1;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;

        expect(userService.getTotalUsers()).andReturn(count);
        expect(userService.list(order, offset, limit)).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), offset, limit, null, null, null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
    }

    @Test
    public void testGetUsersEmailNull() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Query.SortDirection.DESCENDING;
        int offset = 10;
        int limit = Constants.DEFAULT_LIMIT + 1;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;
        String query = null;

        expect(userService.getTotalUsers()).andReturn(count);
        expect(userService.searchByEmail((Pattern) null, order, offset, limit)).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), offset, limit, query, "email", null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
    }

    @Test
    public void testGetUsersNameNull() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Query.SortDirection.DESCENDING;
        int offset = 10;
        int limit = Constants.DEFAULT_LIMIT + 1;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;
        String query = null;

        expect(userService.getTotalUsers()).andReturn(count);
        expect(userService.searchByName((Pattern) null, order, offset, limit)).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), offset, limit, query, "name", null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
    }

    @Test
    public void testGetUsersName() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Query.SortDirection.DESCENDING;
        int offset = 10;
        int limit = Constants.DEFAULT_LIMIT + 1;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;
        String query = "abc";

        expect(userService.getTotalUsers()).andReturn(count);
        Capture<Pattern> captured = new Capture<>();
        expect(userService.searchByName(capture(captured), anyObject(Query.SortDirection.class), anyInt(), anyInt())).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), offset, limit, query, "name", null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
        assertNotNull(captured.getValue());
        assertEquals(Pattern.compile(query).toString(), captured.getValue().toString());
    }

    @Test
    public void testGetUsersEmail() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Query.SortDirection.DESCENDING;
        int offset = 10;
        int limit = Constants.DEFAULT_LIMIT + 1;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;
        String query = "abc";

        expect(userService.getTotalUsers()).andReturn(count);
        Capture<Pattern> captured = new Capture<>();
        expect(userService.searchByEmail(capture(captured), anyObject(Query.SortDirection.class), anyInt(), anyInt())).andReturn(expectedUsers);
        replay(userService);

        JasUserList users = endpoint.getUsers(newCaller(1, true), offset, limit, query, "email", null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
        assertNotNull(captured.getValue());
        assertEquals(Pattern.compile(query).toString(), captured.getValue().toString());
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserNullId() throws NotFoundException, UnauthorizedException, ForbiddenException {
        replay(userService);
        endpoint.getUser(newCaller(1, false), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetUserNotSameUSer() throws NotFoundException, UnauthorizedException, ForbiddenException {
        replay(userService);
        endpoint.getUser(newCaller(1, false), KeyFactory.createKey("K", 5));
    }

    @Test
    public void testGetUser() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        User expectedUser = new User();
        expect(userService.get(key)).andReturn(expectedUser);
        replay(userService);
        User user = endpoint.getUser(newCaller(expectedId, false), key);
        assertNotNull(user);
        assertTrue(expectedUser == user);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserNotFound() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        expect(userService.get(key)).andReturn(null);
        replay(userService);
        endpoint.getUser(newCaller(expectedId, false), key);
    }

    @Test
    public void testGetUserByAdmin() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        User expectedUser = new User();
        expect(userService.get(key)).andReturn(expectedUser);
        replay(userService);
        User user = endpoint.getUser(newCaller(expectedId + 100, true), key);
        assertNotNull(user);
        assertTrue(expectedUser == user);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateUserNullId() throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException {
        replay(userService);
        endpoint.updateUser(newCaller(1, false), null, new User());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateUserNotSameUser() throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException {
        replay(userService);
        endpoint.updateUser(newCaller(1, false), KeyFactory.createKey("u", 2), new User());
    }

    @Test
    public void testUpdateUser() throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException, EntityNotFoundException {
        User expected = new User();
        expect(userService.save(expected)).andReturn(expected);
        replay(userService);
        User user = endpoint.updateUser(newCaller(1, true), KeyFactory.createKey("u", 2), expected);
        assertTrue(expected == user);
    }

    @Test
    public void testAddUserPassword() throws UserLoginExistsException, UsernameExistsException {
        HttpServletRequest servletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(servletRequest.getSession()).andReturn(null);
        replay(servletRequest);
        User value = new User();
        expect(userService.create(EasyMock.anyObject(User.class), EasyMock.anyString())).andReturn(value);
        replay(userService);
        JasAddUserRequest request = new JasAddUserRequest();
        request.setUser(value);
        request.setPassword(RandomStringUtils.randomAscii(10));
        User user = endpoint.addUser(null, request, servletRequest);
        assertNotNull(user);
        assertTrue(value == user);
    }

    @Test
    public void testAddUserUserLogin() throws UserLoginExistsException, UsernameExistsException {
        HttpServletRequest servletRequest = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        UserLogin userLogin = new UserLogin();
        expect(session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY)).andReturn(userLogin).times(2);
        session.removeAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
        expectLastCall();
        replay(session);
        expect(servletRequest.getSession()).andReturn(session);
        replay(servletRequest);
        User value = new User();
        final Capture<User> captured = new Capture<>();
        expect(userService.create(EasyMock.capture(captured), EasyMock.anyObject(UserLogin.class))).andAnswer(new IAnswer<User>() {
            @Override
            public User answer() throws Throwable {
                assertNotNull(captured.getValue());
                assertFalse(captured.getValue().isAdmin());
                return captured.getValue();
            }
        });
        replay(userService);
        JasAddUserRequest request = new JasAddUserRequest();
        value.setAdmin(true);
        request.setUser(value);
        User user = endpoint.addUser(null, request, servletRequest);
        assertNotNull(user);
        assertTrue(value == user);
    }

    /*
    @Test(expected = NotFoundException.class)
    public void testRemoveUserNullId() throws NotFoundException, UnauthorizedException, ForbiddenException {
        replay(userService);
        endpoint.removeUser(newCaller(1, true), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveUserMustBeAdmin() throws NotFoundException, UnauthorizedException, ForbiddenException {
        replay(userService);
        endpoint.removeUser(newCaller(1, false), Datastore.createKey(User.class, 1));
    }
    */
}