package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.expectResponse;

public class OAuth2CodeCallbackServletTest {

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testDoGet() throws Exception {
        ServletRunner servletRunner = TestHelper.servletRunner();
        ServletUnitClient client = servletRunner.newClient();

        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/oauth2/callback/Google?state=WHATEVER");
        InvocationContext ic = expectResponse(client, request, HttpServletResponse.SC_FOUND);
        WebResponse servletResponse = ic.getServletResponse();
    }
}