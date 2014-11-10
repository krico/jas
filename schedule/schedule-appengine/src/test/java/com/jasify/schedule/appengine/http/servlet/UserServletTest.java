package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.servlet.UserServlet;
import com.meterware.servletunit.ServletRunner;
import org.junit.After;
import org.junit.Before;

public class UserServletTest {
    private ServletRunner servletRunner;

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }


}