package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.client.http.TestHttpTransportFactory;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OAuth2ServiceTest {

    private OAuth2Service service;
    private TestHttpTransportFactory testHttpTransportFactory = new TestHttpTransportFactory();

    @BeforeClass
    public static void initialize() {
        TestHelper.initializeMemcacheWithDatastore();
        ApplicationData.instance().reload();

        for (OAuth2ProviderEnum provider : OAuth2ProviderEnum.values()) {
            ApplicationData.instance().setProperty(provider.clientIdKey(), provider + "ID");
            ApplicationData.instance().setProperty(provider.clientSecretKey(), provider + "Secret");
        }
    }

    @AfterClass
    public static void cleanup() {
        TestHttpTransportFactory.cleanup();
        TestHelper.cleanupMemcacheWithDatastore();
    }

    @Before
    public void setService() {
        testHttpTransportFactory.setUp();
        service = OAuth2ServiceFactory.getOAuth2Service();
    }

    @After
    public void tearDown() {
        testHttpTransportFactory.tearDown();
    }

    @Test
    public void testCreateCodeRequestUrl() throws Exception {
        Serializable state = new State();

        for (OAuth2ProviderEnum provider : OAuth2ProviderEnum.values()) {
            GenericUrl baseUrl = new GenericUrl("http://" + provider + ".localhost");
            GenericUrl codeRequestUrl = service.createCodeRequestUrl(baseUrl, provider, state);
            assertNotNull("P: " + provider, codeRequestUrl);
            assertEquals("P: " + provider,
                    codeRequestUrl.getScheme() + "://" + codeRequestUrl.getHost() + codeRequestUrl.getRawPath(),
                    provider.authorizationUrl());

            assertEquals(provider + "ID", codeRequestUrl.get("client_id"));
            assertEquals("code", codeRequestUrl.get("response_type"));
            assertNotNull(codeRequestUrl.get("scope"));
            assertNotNull(codeRequestUrl.get("redirect_uri"));
            assertNotNull(codeRequestUrl.get("state"));

            GenericUrl redirectUri = new GenericUrl(codeRequestUrl.get("redirect_uri").toString());
            baseUrl.setRawPath(DefaultOAuth2Service.CALLBACK_PATH_PREFIX + provider.name());
            assertEquals(baseUrl, redirectUri);
        }
    }

    @Test(expected = OAuth2Exception.CodeResponseException.class)
    public void testFetchUserTokenCodeError() throws Exception {
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), OAuth2ProviderEnum.Google, new State());
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri")).set("error", "Something went wrong");
        service.fetchUserToken(responseUrl);
    }

    @Test(expected = OAuth2Exception.MissingStateException.class)
    public void testFetchUserTokenNoState() throws Exception {
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), OAuth2ProviderEnum.Google, new State());
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri")).set("code", RandomStringUtils.randomAscii(32));
        service.fetchUserToken(responseUrl);
    }

    @Test(expected = OAuth2Exception.InconsistentStateException.class)
    public void testFetchUserTokenInconsistentState() throws Exception {
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), OAuth2ProviderEnum.Google, new State());
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri"))
                .set("state", "notFound")
                .set("code", RandomStringUtils.randomAscii(32));
        service.fetchUserToken(responseUrl);
    }

    @Test(expected = OAuth2Exception.BadProviderException.class)
    public void testFetchUserTokenBadProvider() throws Exception {
        service.fetchUserToken(new GenericUrl("http://localhost/"));
    }

    @Test
    public void testFetchUserTokenGoogle() throws Exception {
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Google;
        State state = new State();
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), provider, state);
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri"))
                .set("code", RandomStringUtils.randomAscii(32))
                .set("state", codeRequestUrl.get("state"));

        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
        String accessToken = RandomStringUtils.randomAscii(32);

        Long expires = 3600l;
        TokenResponse tr = new TokenResponse()
                .setAccessToken(accessToken)
                .setExpiresInSeconds(expires)
                .setTokenType("bearer");

        tr.setFactory(JacksonFactory.getDefaultInstance());

        mockResponse.setContent(tr.toString());
        testHttpTransportFactory
                .expect(HttpMethods.POST, provider.tokenUrl())
                .andReturn(mockResponse);

        OAuth2UserToken userToken = service.fetchUserToken(responseUrl);
        assertNotNull(userToken);
        assertNotNull(userToken.getTokenResponse());
        assertEquals(accessToken, userToken.getTokenResponse().getAccessToken());
        assertEquals(expires, userToken.getTokenResponse().getExpiresInSeconds());
        assertNotNull(userToken.getState());
        assertNotNull(userToken.getProvider());
        assertEquals(state, userToken.getState());
        assertEquals(provider, userToken.getProvider());
    }

    @Test(expected = OAuth2Exception.InconsistentStateException.class)
    public void testFetchUserTokenGoogleStateIsDeleted() throws Exception {
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Google;
        State state = new State();
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), provider, state);
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri"))
                .set("code", RandomStringUtils.randomAscii(32))
                .set("state", codeRequestUrl.get("state"));

        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
        TokenResponse tr = new TokenResponse()
                .setAccessToken("AT")
                .setExpiresInSeconds(3600l)
                .setTokenType("bearer");

        tr.setFactory(JacksonFactory.getDefaultInstance());

        mockResponse.setContent(tr.toString());
        testHttpTransportFactory
                .expect(HttpMethods.POST, provider.tokenUrl())
                .andReturn(mockResponse);

        service.fetchUserToken(responseUrl);
        service.fetchUserToken(responseUrl);//second time must throw since state is removed from memcache
    }

    @Test(expected = OAuth2Exception.TokenRequestException.class)
    public void testFetchUserTokenGoogleWithIOException() throws Exception {
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Google;
        State state = new State();
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), provider, state);
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri"))
                .set("code", RandomStringUtils.randomAscii(32))
                .set("state", codeRequestUrl.get("state"));

        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
        TokenResponse tr = new TokenResponse()
                .setAccessToken("AT")
                .setExpiresInSeconds(3600l)
                .setTokenType("bearer");

        tr.setFactory(JacksonFactory.getDefaultInstance());

        mockResponse.setContent(tr.toString());
        testHttpTransportFactory
                .expect(HttpMethods.POST, provider.tokenUrl())
                .andThrow(new IOException("I wanted this to fail"));

        service.fetchUserToken(responseUrl);
    }

    @Test
    public void testFetchUserTokenFacebook() throws Exception {
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Facebook;
        State state = new State();
        GenericUrl codeRequestUrl = service.createCodeRequestUrl(new GenericUrl("http://localhost"), provider, state);
        GenericUrl responseUrl = new GenericUrl((String) codeRequestUrl.get("redirect_uri"))
                .set("code", RandomStringUtils.randomAscii(8))
                .set("state", codeRequestUrl.get("state"));

        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
        String accessToken = RandomStringUtils.randomAlphabetic(8);
        Long expires = 3600l;
        mockResponse.setContent("access_token=" + accessToken + "&expires=" + expires);
        GenericUrl fbTokenRequest = new GenericUrl(provider.tokenUrl());
        fbTokenRequest.set("client_id", provider + "ID");
        fbTokenRequest.set("client_secret", provider + "Secret");
        fbTokenRequest.set("redirect_uri", codeRequestUrl.get("redirect_uri"));
        fbTokenRequest.set("code", responseUrl.get("code"));

        testHttpTransportFactory
                .expect(HttpMethods.GET, fbTokenRequest.build())
                .andReturn(mockResponse);

        OAuth2UserToken userToken = service.fetchUserToken(responseUrl);
        assertNotNull(userToken);
        assertNotNull(userToken.getTokenResponse());
        assertEquals(accessToken, userToken.getTokenResponse().getAccessToken());
        assertEquals(expires, userToken.getTokenResponse().getExpiresInSeconds());
        assertNotNull(userToken.getState());
        assertNotNull(userToken.getProvider());
        assertEquals(state, userToken.getState());
        assertEquals(provider, userToken.getProvider());
    }

    @Test
    public void testFetchInfoFacebook() throws Exception {
        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(RandomStringUtils.randomAlphabetic(8));
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Facebook;
        State state = new State();
        OAuth2UserToken token = new OAuth2UserToken(provider, tokenResponse, state);

        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();

        String id = RandomStringUtils.randomAlphabetic(12);
        String email = RandomStringUtils.randomAlphabetic(8);
        String profile = RandomStringUtils.randomAlphabetic(15);
        String name = RandomStringUtils.randomAlphabetic(15);

        Map<String, String> data = new HashMap<>();
        data.put("id", id);
        data.put("link", profile);
        data.put("email", email);
        data.put("name", name);

        mockResponse.setContent(JSON.toJson(data));
        GenericUrl tokenInfoUrl = new GenericUrl(provider.userInfoUrl())
                .set("access_token", tokenResponse.getAccessToken());
        testHttpTransportFactory
                .expect(HttpMethods.GET, tokenInfoUrl.build())
                .andReturn(mockResponse);

        OAuth2Info oAuth2Info = service.fetchInfo(token);

        assertNotNull(oAuth2Info);
        assertEquals(provider, oAuth2Info.getProvider());
        assertEquals(id, oAuth2Info.getUserId());
        assertEquals(email, oAuth2Info.getEmail());
        assertEquals(name, oAuth2Info.getRealName());
        assertEquals(profile, oAuth2Info.getProfile());
        assertEquals(state, oAuth2Info.getState());
    }

    @Test
    public void testFetchInfoGoogle() throws Exception {
        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(RandomStringUtils.randomAlphabetic(8));
        OAuth2ProviderEnum provider = OAuth2ProviderEnum.Google;
        State state = new State();
        OAuth2UserToken token = new OAuth2UserToken(provider, tokenResponse, state);

        GenericUrl tokenInfoUrl = new GenericUrl("https://www.googleapis.com/oauth2/v2/tokeninfo")
                .set("access_token", tokenResponse.getAccessToken());
        MockLowLevelHttpResponse mockResponse = new MockLowLevelHttpResponse();
        String id = RandomStringUtils.randomAlphabetic(12);
        String email = RandomStringUtils.randomAlphabetic(8);

        Tokeninfo tokenInfo = new Tokeninfo();
        tokenInfo.setUserId(id);
        tokenInfo.setEmail(email);
        tokenInfo.setFactory(JacksonFactory.getDefaultInstance());

        mockResponse.setContent(tokenInfo.toPrettyString());

        testHttpTransportFactory
                .expect(HttpMethods.POST, tokenInfoUrl.build())
                .andReturn(mockResponse);

        mockResponse = new MockLowLevelHttpResponse();
        Userinfoplus userInfo = new Userinfoplus();
        String avatar = RandomStringUtils.randomAlphabetic(15);
        userInfo.setPicture(avatar);
        String profile = RandomStringUtils.randomAlphabetic(15);
        userInfo.setLink(profile);
        String name = RandomStringUtils.randomAlphabetic(15);
        userInfo.setName(name);
        userInfo.setFactory(JacksonFactory.getDefaultInstance());
        userInfo.setId(id);
        userInfo.setEmail(email);

        mockResponse.setContent(userInfo.toPrettyString());
        testHttpTransportFactory
                .expect(HttpMethods.GET, "https://www.googleapis.com/oauth2/v2/userinfo")
                .andReturn(mockResponse);

        OAuth2Info oAuth2Info = service.fetchInfo(token);

        assertNotNull(oAuth2Info);
        assertEquals(provider, oAuth2Info.getProvider());
        assertEquals(id, oAuth2Info.getUserId());
        assertEquals(email, oAuth2Info.getEmail());
        assertEquals(name, oAuth2Info.getRealName());
        assertEquals(avatar, oAuth2Info.getAvatar());
        assertEquals(profile, oAuth2Info.getProfile());
        assertEquals(state, oAuth2Info.getState());
    }

    public static class State implements Serializable {
        private final String name = RandomStringUtils.random(10);
        private final Date created = new Date();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof State)) return false;

            State state = (State) o;

            if (!created.equals(state.created)) return false;
            if (!name.equals(state.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + created.hashCode();
            return result;
        }
    }
}