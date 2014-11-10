package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.TestHelper;
import com.meterware.servletunit.ServletRunner;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.*;

public class LogoutServletTest {
    private ServletRunner servletRunner;

    @Before
    public void servletRunner() {
        TestHelper.initializeJasify();
        servletRunner = new ServletRunner();
        servletRunner.registerServlet("logout", LoginServlet.class.getName());
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupDatastore();
    }

}