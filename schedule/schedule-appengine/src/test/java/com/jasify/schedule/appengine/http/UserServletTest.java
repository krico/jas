package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.TestHelper;
import com.meterware.servletunit.ServletRunner;
import org.junit.After;
import org.junit.Before;

public class UserServletTest {
    private ServletRunner servletRunner;

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner("user", UserServlet.class);
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }


}