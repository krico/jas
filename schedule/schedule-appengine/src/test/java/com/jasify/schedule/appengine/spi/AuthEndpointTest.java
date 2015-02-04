package com.jasify.schedule.appengine.spi;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.*;
import com.jasify.schedule.appengine.oauth2.*;
import com.jasify.schedule.appengine.spi.dm.*;
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
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

public class AuthEndpointTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();
    private TestOAuth2ServiceFactory testOAuth2ServiceFactory = new TestOAuth2ServiceFactory();

    private AuthEndpoint endpoint = new AuthEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        testUserServiceFactory.setUp();

    }

    @After
    public void cleanupDatastore() {
        testOAuth2ServiceFactory.tearDown();
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

    @Test
    public void testProviderAuthorize() {
        testUserServiceFactory.replay();
        testOAuth2ServiceFactory.setUp();

        String baseUrl = "http://my.host";
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Google;
        String data = RandomStringUtils.randomAscii(128);

        String authorizeUrl = "http://provider.com/authorize";

        EasyMock.expect(OAuth2ServiceFactory
                .getOAuth2Service()
                .createCodeRequestUrl(new GenericUrl(baseUrl), provider, data))
                .andReturn(new GenericUrl(authorizeUrl));

        testOAuth2ServiceFactory.replay();

        JasProviderAuthorizeRequest request = new JasProviderAuthorizeRequest();
        request.setBaseUrl(baseUrl);
        request.setProvider(provider);
        request.setData(data);

        JasProviderAuthorizeResponse response = endpoint.providerAuthorize(request);
        assertNotNull(response);
        assertEquals(authorizeUrl, response.getAuthorizeUrl());
    }

    @Test
    public void testProviderAuthenticate() throws Exception {
        testOAuth2ServiceFactory.setUp();
        UserService userService = UserServiceFactory.getUserService();
        OAuth2Service oAuth2Service = OAuth2ServiceFactory.getOAuth2Service();

        User user = new User();
        user.setId(KeyFactory.createKey("User", 1));
        HttpServletRequest httpServletRequest = EasyMock.createMock(HttpServletRequest.class);
        HttpSession session = EasyMock.createMock(HttpSession.class);
        EasyMock.expect(httpServletRequest.getSession(true)).andReturn(session);
        session.setAttribute(EasyMock.anyString(), EasyMock.anyObject());
        EasyMock.expectLastCall();

        EasyMock.replay(session);
        EasyMock.replay(httpServletRequest);

        JasProviderAuthenticateRequest request = new JasProviderAuthenticateRequest();
        request.setCallbackUrl("http://l.com/");

        OAuth2UserToken token = new OAuth2UserToken(OAuth2ProviderEnum.Google, new TokenResponse(), "ST");
        EasyMock.expect(oAuth2Service.fetchUserToken(new GenericUrl(request.getCallbackUrl())))
                .andReturn(token);

        OAuth2Info userInfo = new OAuth2Info(token.getProvider(), token.getState());
        userInfo.setUserId("ABC");
        EasyMock.expect(oAuth2Service.fetchInfo(token)).andReturn(userInfo);

        EasyMock.expect(userService.findByLogin(token.getProvider().name(), userInfo.getUserId()))
                .andReturn(user);

        testOAuth2ServiceFactory.replay();
        testUserServiceFactory.replay();

        JasProviderAuthenticateResponse response = endpoint.providerAuthenticate(null, httpServletRequest, request);
        assertNotNull(response);
    }


    @Test
    public void testForgotPassword() throws Exception {
        JasForgotPasswordRequest request = new JasForgotPasswordRequest();
        request.setUrl("https://foo.com");
        request.setEmail("a@com");
        User model = new User();
        model.setEmail(request.getEmail());
        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setCode(Datastore.createKey(PasswordRecovery.class, "XYZ12"));
        recovery.getUserRef().setModel(model);
        EasyMock.expect(UserServiceFactory.getUserService().registerPasswordRecovery(request.getEmail()))
                .andReturn(recovery);
        testUserServiceFactory.replay();
        endpoint.forgotPassword(request);
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertEquals(1, sentMessages.size());
        MailServicePb.MailMessage mailMessage = sentMessages.get(0);
        assertEquals(request.getEmail(), mailMessage.getTo(0));
        assertTrue(mailMessage.getHtmlBody().contains(recovery.getCode().getName()));
        assertTrue(mailMessage.getTextBody().contains(recovery.getCode().getName()));
    }

    @Test
    public void testRecoverPassword() throws Exception {
        JasRecoverPasswordRequest request = new JasRecoverPasswordRequest();
        request.setCode("YAZ1");
        request.setNewPassword("secret");
        UserServiceFactory.getUserService().recoverPassword(request.getCode(), request.getNewPassword());
        EasyMock.expectLastCall();
        testUserServiceFactory.replay();
        endpoint.recoverPassword(request);
    }

}