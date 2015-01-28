package com.jasify.schedule.appengine.oauth2;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

public class OAuth2ServiceFactoryTest {

    @BeforeClass
    public static void initializeDatastore() {
        TestHelper.initializeJasify();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetOAuth2Service() throws Exception {
        assertNotNull(OAuth2ServiceFactory.getOAuth2Service());
    }
}