package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.spi.dm.JasAddUserRequest;
import com.jasify.schedule.appengine.spi.dm.JasUserList;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

public class UserEndpointTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    private UserEndpoint endpoint;

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        endpoint = new UserEndpoint();
        testUserServiceFactory.setUp();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testUserServiceFactory.tearDown();
    }

    @Test(expected = ForbiddenException.class)
    public void testGetUsersMustBeAdmin() throws UnauthorizedException, ForbiddenException {
        testUserServiceFactory.replay();
        endpoint.getUsers(newCaller(1), null, null, null, null, null, null);
    }

    @Test
    public void testGetUsersEmpty() throws UnauthorizedException, ForbiddenException {
        Query.SortDirection order = Constants.DEFAULT_ORDER;
        int offset = 0;
        int limit = Constants.DEFAULT_LIMIT;
        ArrayList<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(new User());
        int count = 50;

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        expect(UserServiceFactory.getUserService().list(order, offset, limit)).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), null, null, null, null, null, null);
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

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        expect(UserServiceFactory.getUserService().list(order, offset, limit)).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), offset, limit, null, null, null, order);
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

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        expect(UserServiceFactory.getUserService().searchByEmail((Pattern) null, order, offset, limit)).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), offset, limit, null, "email", null, order);
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

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        expect(UserServiceFactory.getUserService().searchByName((Pattern) null, order, offset, limit)).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), offset, limit, null, "name", null, order);
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

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        Capture<Pattern> captured = EasyMock.newCapture();
        expect(UserServiceFactory.getUserService().searchByName(capture(captured), anyObject(Query.SortDirection.class), anyInt(), anyInt())).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), offset, limit, query, "name", null, order);
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

        expect(UserServiceFactory.getUserService().getTotalUsers()).andReturn(count);
        Capture<Pattern> captured = EasyMock.newCapture();
        expect(UserServiceFactory.getUserService().searchByEmail(capture(captured), anyObject(Query.SortDirection.class), anyInt(), anyInt())).andReturn(expectedUsers);
        testUserServiceFactory.replay();

        JasUserList users = endpoint.getUsers(newAdminCaller(1), offset, limit, query, "email", null, order);
        assertNotNull(users);
        assertEquals(expectedUsers.size(), users.size());
        assertEquals(expectedUsers.get(0), users.get(0));
        assertEquals(count, users.getTotal());
        assertNotNull(captured.getValue());
        assertEquals(Pattern.compile(query).toString(), captured.getValue().toString());
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserNullId() throws NotFoundException, UnauthorizedException, ForbiddenException {
        testUserServiceFactory.replay();
        endpoint.getUser(newCaller(1), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetUserNotSameUSer() throws NotFoundException, UnauthorizedException, ForbiddenException {
        testUserServiceFactory.replay();
        endpoint.getUser(newCaller(1), KeyFactory.createKey("K", 5));
    }

    @Test
    public void testGetUser() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        User expectedUser = new User();
        expect(UserServiceFactory.getUserService().get(key)).andReturn(expectedUser);
        testUserServiceFactory.replay();
        User user = endpoint.getUser(newCaller(expectedId), key);
        assertNotNull(user);
        assertTrue(expectedUser == user);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserNotFound() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        expect(UserServiceFactory.getUserService().get(key)).andReturn(null);
        testUserServiceFactory.replay();
        endpoint.getUser(newCaller(expectedId), key);
    }

    @Test
    public void testGetUserByAdmin() throws NotFoundException, UnauthorizedException, ForbiddenException {
        long expectedId = 1;
        Key key = Datastore.createKey(User.class, expectedId);
        User expectedUser = new User();
        expect(UserServiceFactory.getUserService().get(key)).andReturn(expectedUser);
        testUserServiceFactory.replay();
        User user = endpoint.getUser(newAdminCaller(expectedId + 100), key);
        assertNotNull(user);
        assertTrue(expectedUser == user);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateUserNullId() throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException, InternalServerErrorException {
        testUserServiceFactory.replay();
        endpoint.updateUser(newCaller(1), null, new User());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateUserNotSameUser() throws NotFoundException, UnauthorizedException, ForbiddenException, FieldValueException, InternalServerErrorException {
        testUserServiceFactory.replay();
        endpoint.updateUser(newCaller(1), KeyFactory.createKey("u", 2), new User());
    }

    @Test
    public void testUpdateUser() throws NotFoundException, UnauthorizedException, ForbiddenException, ModelException, InternalServerErrorException {
        User expected = new User();
        expect(UserServiceFactory.getUserService().save(expected)).andReturn(expected);
        testUserServiceFactory.replay();
        User user = endpoint.updateUser(newAdminCaller(1), KeyFactory.createKey("u", 2), expected);
        assertTrue(expected == user);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateUserNotFoundException() throws NotFoundException, UnauthorizedException, ForbiddenException, ModelException, InternalServerErrorException {
        User user = new User();
        UserServiceFactory.getUserService().save(user);
        expectLastCall().andThrow(new EntityNotFoundException());
        testUserServiceFactory.replay();
        endpoint.updateUser(newAdminCaller(1), KeyFactory.createKey("u", 2), user);
    }

    @Test
    public void testAddUserPassword() throws Exception {
        HttpServletRequest servletRequest = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        session.setAttribute(EasyMock.anyString(), EasyMock.anyObject(HttpUserSession.class));
        expectLastCall();
        replay(session);
        expect(servletRequest.getSession(true)).andReturn(session);
        expect(servletRequest.getSession()).andReturn(null);
        expect(servletRequest.getHeader("referer")).andReturn("Me");
        expect(servletRequest.getRemoteAddr()).andReturn("Mars");
        expect(servletRequest.getRemoteAddr()).andReturn("Maybe Pluto");

        replay(servletRequest);
        User value = new User();
        expect(UserServiceFactory.getUserService().create(EasyMock.anyObject(User.class), EasyMock.anyString())).andReturn(value);
        testUserServiceFactory.replay();
        JasAddUserRequest request = new JasAddUserRequest();
        request.setUser(value);
        request.setPassword(RandomStringUtils.randomAscii(10));
        User user = endpoint.addUser(null, request, servletRequest);
        assertNotNull(user);
        assertTrue(value == user);
    }

    @Test
    public void testAddUserUserLogin() throws Exception {
        HttpServletRequest servletRequest = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        UserLogin userLogin = new UserLogin();
        expect(session.getAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY)).andReturn(userLogin).times(2);
        session.removeAttribute(HttpUserSession.OAUTH_USER_LOGIN_KEY);
        expectLastCall();
        session.setAttribute(EasyMock.anyString(), EasyMock.anyObject(HttpUserSession.class));
        expectLastCall();
        replay(session);
        expect(servletRequest.getSession(true)).andReturn(session);
        expect(servletRequest.getSession()).andReturn(session);
        expect(servletRequest.getHeader("referer")).andReturn("Me");
        expect(servletRequest.getRemoteAddr()).andReturn("14 Whaka Terrace");
        expect(servletRequest.getRemoteAddr()).andReturn("Not here");
        replay(servletRequest);
        User value = new User();
        final Capture<User> captured = EasyMock.newCapture();
        expect(UserServiceFactory.getUserService().create(EasyMock.capture(captured), EasyMock.anyObject(UserLogin.class))).andAnswer(new IAnswer<User>() {
            @Override
            public User answer() throws Throwable {
                assertNotNull(captured.getValue());
                assertFalse(captured.getValue().isAdmin());
                return captured.getValue();
            }
        });
        testUserServiceFactory.replay();
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