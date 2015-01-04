package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class EnvironmentUtilTest {
    public static final String EMAIL = "jaz@ify";

    @BeforeClass
    public static void startIdentityService() {
        TestHelper.initializeAppIdentity();
    }

    @AfterClass
    public static void cleanupIdentityService() {
        TestHelper.cleanupAppIdentity();
    }

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(EnvironmentUtil.class);
    }

    @Test
    public void testCurrentEnvironment() throws Exception {
        assertNotNull(EnvironmentUtil.currentEnvironment());
    }

    @Test
    public void testAppId() throws Exception {
        assertNotNull(EnvironmentUtil.appId());
    }

    @Test
    public void testEmail() throws Exception {
        assertNull(EnvironmentUtil.email());
    }

    @Test
    public void testDefaultVersionHostname() throws Exception {
        assertNotNull(EnvironmentUtil.defaultVersionHostname());
    }

    @Test
    public void testIsProduction() throws Exception {
        assertFalse(EnvironmentUtil.isProduction());
    }

    @Test
    public void testIsDevelopment() throws Exception {
        assertTrue(EnvironmentUtil.isDevelopment());
    }

    @Test
    public void testIsContinuousIntegrationEnvironment() {
        boolean expected = Boolean.valueOf(System.getenv(EnvironmentUtil.CI_ENV_KEY));
        assertEquals(expected, EnvironmentUtil.isContinuousIntegrationEnvironment());
    }
}