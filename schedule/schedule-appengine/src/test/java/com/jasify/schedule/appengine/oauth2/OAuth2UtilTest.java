package com.jasify.schedule.appengine.oauth2;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class OAuth2UtilTest {
    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(OAuth2Util.class);
    }

    @Test
    public void testCreateStateKey() {
        GenericUrl baseUrl = new GenericUrl("http://some.com");
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        assertNotNull(stateKey);
        assertTrue(stateKey.length() > 10);
        assertNotSame(stateKey, OAuth2Util.createStateKey(baseUrl));
    }

    @Test
    public void testAppPathRoot() {
        GenericUrl baseUrl = new GenericUrl("http://some.com");
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        String appPath = OAuth2Util.appPath(stateKey);
        assertNotNull(appPath);
        assertEquals("/", appPath);
    }

    @Test
    public void testAppPathIndex() {
        GenericUrl baseUrl = new GenericUrl("http://some.com/index.html");
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        String appPath = OAuth2Util.appPath(stateKey);
        assertNotNull(appPath);
        assertEquals("/index.html", appPath);
    }

    @Test
    public void testAppPathBookIt() {
        GenericUrl baseUrl = new GenericUrl("http://some.com/book-it.html");
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        String appPath = OAuth2Util.appPath(stateKey);
        assertNotNull(appPath);
        assertEquals("/book-it.html", appPath);
    }

    @Test
    public void testAppPathBookItWithFragment() {
        GenericUrl baseUrl = new GenericUrl("http://some.com/book-it.html");
        baseUrl.setFragment("/path/to/route");
        System.err.println(baseUrl);
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        String appPath = OAuth2Util.appPath(stateKey);
        assertNotNull(appPath);
        assertEquals("/book-it.html", appPath);
    }

    @Test
    public void testAppPathFallBackToRoot() {
        GenericUrl baseUrl = new GenericUrl("http://some.com/whatever.html");
        String stateKey = OAuth2Util.createStateKey(baseUrl);
        String appPath = OAuth2Util.appPath(stateKey);
        assertNotNull(appPath);
        assertEquals("/", appPath);
    }

}