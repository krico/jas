package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OAuth2ServiceTest {

    private OAuth2Service service;

    @BeforeClass
    public static void initialize() {
        TestHelper.initializeMemcacheWithDatastore();
        ApplicationData.instance().reload();

        for (OAuth2ProviderConfig.ProviderEnum provider : OAuth2ProviderConfig.ProviderEnum.values()) {
            ApplicationData.instance().setProperty(provider.clientIdKey(), provider + "ID");
            ApplicationData.instance().setProperty(provider.clientSecretKey(), provider + "Secret");
        }
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupMemcacheWithDatastore();
    }

    @Before
    public void setService() {
        service = OAuth2ServiceFactory.getOAuth2Service();
    }

    @Test
    public void testCreateCodeRequestUrl() throws Exception {
        Serializable state = new State();

        for (OAuth2ProviderConfig.ProviderEnum provider : OAuth2ProviderConfig.ProviderEnum.values()) {
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

            GenericUrl redirectUri = new GenericUrl(codeRequestUrl.get("redirect_uri").toString());
            baseUrl.setRawPath(DefaultOAuth2Service.CALLBACK_PATH_PREFIX + provider.name());
            assertEquals(baseUrl, redirectUri);
        }
    }

    public static class State implements Serializable {
        private String name = RandomStringUtils.random(10);
        private Date created = new Date();
    }
}