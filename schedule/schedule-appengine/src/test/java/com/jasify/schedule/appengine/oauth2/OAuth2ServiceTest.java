package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.client.http.TestHttpTransportFactory;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

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