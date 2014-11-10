package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.TestHelper;
import com.meterware.servletunit.ServletRunner;
import org.junit.After;
import org.junit.Before;

public class UserServletTest {
    private ServletRunner servletRunner;

    @Before
    public void servletRunner() {
        TestHelper.initializeJasify();
        servletRunner = new ServletRunner();
        servletRunner.registerServlet("user", UserServlet.class.getName());
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupDatastore();
    }


}