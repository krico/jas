package com.jasify.schedule.appengine.http.servlet;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.oauth2.OAuth2Util;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OAuth2CodeCallbackServletTest {

    @BeforeClass
    public static void servletRunner() {
        TestHelper.initializeServletRunner();
    }

    @AfterClass
    public static void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testDoGet() throws Exception {
        ServletRunner servletRunner = TestHelper.servletRunner();
        ServletUnitClient client = servletRunner.newClient();

        String urlString = "http://schedule.jasify.com/oauth2/callback/Google";
        WebRequest request = new GetMethodWebRequest(urlString);
        request.setParameter("state", "WHATEVER");
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse servletResponse = ic.getServletResponse();
        assertEquals(HttpServletResponse.SC_FOUND, servletResponse.getResponseCode());

        String location = servletResponse.getHeaderField("Location");
        assertNotNull(location);
        String expected = "/#/oauth/" + URLEncoder.encode(urlString + "?state=WHATEVER", "utf-8");
        assertEquals(expected, location);
    }

    @Test
    public void testDoGetBookIt() throws Exception {
        ServletRunner servletRunner = TestHelper.servletRunner();
        ServletUnitClient client = servletRunner.newClient();
        String stateKey = OAuth2Util.createStateKey(new GenericUrl("http://l/book-it.html"));
        String urlString = "http://schedule.jasify.com/oauth2/callback/Google";
        WebRequest request = new GetMethodWebRequest(urlString);
        request.setParameter("state", stateKey);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse servletResponse = ic.getServletResponse();
        assertEquals(HttpServletResponse.SC_FOUND, servletResponse.getResponseCode());

        String location = servletResponse.getHeaderField("Location");
        assertNotNull(location);
        String expected = "/book-it.html#/oauth/" + URLEncoder.encode(urlString + "?state=" + stateKey, "utf-8");
        assertEquals(expected, location);
    }

    @Test
    public void testDoGetIndex() throws Exception {
        ServletRunner servletRunner = TestHelper.servletRunner();
        ServletUnitClient client = servletRunner.newClient();
        String stateKey = OAuth2Util.createStateKey(new GenericUrl("http://l/index.html"));
        String urlString = "http://schedule.jasify.com/oauth2/callback/Google";
        WebRequest request = new GetMethodWebRequest(urlString);
        request.setParameter("state", stateKey);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse servletResponse = ic.getServletResponse();
        assertEquals(HttpServletResponse.SC_FOUND, servletResponse.getResponseCode());

        String location = servletResponse.getHeaderField("Location");
        assertNotNull(location);
        String expected = "/index.html#/oauth/" + URLEncoder.encode(urlString + "?state=" + stateKey, "utf-8");
        assertEquals(expected, location);
    }
}