package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.LoginFailedException;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.dm.JasChangePasswordRequest;
import com.jasify.schedule.appengine.spi.dm.JasLoginRequest;
import com.jasify.schedule.appengine.spi.dm.JasLoginResponse;
import com.jasify.schedule.appengine.util.DigestUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.*;

public class AuthEndpointTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    private AuthEndpoint endpoint = new AuthEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        testUserServiceFactory.setUp();

    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testUserServiceFactory.tearDown();
    }

    @Test(expected = ForbiddenException.class)
    public void testChangePasswordCheckAuthentication() throws Exception {
        testUserServiceFactory.replay();
        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(Datastore.createKey(User.class, 2), "abc", "def"));
    }

    @Test
    public void testChangePassword() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 1));
        String oldPw = "abc";
        user.setPassword(TypeUtil.toShortBlob(DigestUtil.encrypt(oldPw)));
        expect(UserServiceFactory.getUserService().get(user.getId())).andReturn(user).times(2);
        expect(UserServiceFactory.getUserService().setPassword(user, "def")).andReturn(user).times(2);
        testUserServiceFactory.replay();

        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(Datastore.createKey(User.class, 1), oldPw, "def"));
        //admin
        endpoint.changePassword(newCaller(2, true), new JasChangePasswordRequest(Datastore.createKey(User.class, 1), "", "def"));
    }

    @Test(expected = ForbiddenException.class)
    public void testChangePasswordWrongOld() throws Exception {
        User user = new User();
        user.setId(Datastore.createKey(User.class, 1));
        String oldPw = "abc";
        user.setPassword(TypeUtil.toShortBlob(DigestUtil.encrypt(oldPw)));
        expect(UserServiceFactory.getUserService().get(user.getId())).andReturn(user);
        testUserServiceFactory.replay();

        endpoint.changePassword(newCaller(1, false), new JasChangePasswordRequest(Datastore.createKey(User.class, 1), oldPw + "x", "def"));
    }

    @Test
    public void testLogin() throws Exception {
        String loginName = RandomStringUtils.randomAlphabetic(5);
        String password = RandomStringUtils.randomAscii(8);

        User user = new User();
        user.setId(Datastore.createKey(User.class, 99));
        user.setName(loginName.toLowerCase());
        expect(UserServiceFactory.getUserService().login(loginName, password)).andReturn(user).once();
        testUserServiceFactory.replay();

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
        assertEquals(KeyFactory.keyToString(user.getId()), response.getUserId());


        verify(httpServletRequest);
        verify(httpSession);
    }

    @Test(expected = BadRequestException.class)
    public void testLoginBadParametersNullName() throws Exception {
        testUserServiceFactory.replay();

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        replay(httpServletRequest);

        endpoint.login(httpServletRequest, new JasLoginRequest(null, "aaa"));
    }

    @Test(expected = BadRequestException.class)
    public void testLoginBadParametersNullPassword() throws Exception {
        testUserServiceFactory.replay();

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
        expect(UserServiceFactory.getUserService().login(loginName, password)).andThrow(new LoginFailedException()).once();
        testUserServiceFactory.replay();

        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        expect(httpServletRequest.getRemoteAddr()).andReturn("127.0.0.1:-)").anyTimes();
        replay(httpServletRequest);

        endpoint.login(httpServletRequest, new JasLoginRequest(loginName, password));
    }

    @Test
    public void testLogout() throws Exception {
        testUserServiceFactory.replay();
        UserSession session = EasyMock.createMock(UserSession.class);
        session.invalidate();
        expectLastCall();
        replay(session);
        UserContext.setContext(session, null, null);
        endpoint.logout(newCaller(1, false));
        verify(session);
    }

}