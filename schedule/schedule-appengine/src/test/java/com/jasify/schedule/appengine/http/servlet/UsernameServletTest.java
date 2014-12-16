package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.expectResponse;

public class UsernameServletTest {

    @Before
    public void servletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testPostEmpty() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/username");
        expectResponse(client, request, HttpServletResponse.SC_NOT_ACCEPTABLE);
    }

    @Test
    public void testPostExistingUsername() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        PostMethodWebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/username", IOUtils.toInputStream("admin"), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_NOT_ACCEPTABLE);
    }

    @Test
    public void testPostWithValidUsername() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();

        PostMethodWebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/username", IOUtils.toInputStream("krico"), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_OK);
    }


}