package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonSessionResponse;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.Urls.*;
import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.expectResponse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class LoginServletTest {

    @Before
    public void startServletRunner() {
        TestHelper.initializeServletRunner();
    }

    @After
    public void stopServletRunner() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testEmptyLoginFails() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest(LOGIN_URL);
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testBadCredentialsLoginFails() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonLoginRequest req = new JsonLoginRequest("jas", "password");

        WebRequest request = new PostMethodWebRequest(LOGIN_URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);

        WebRequest isLoggedInRequest = new GetMethodWebRequest(RESTORE_URL);
        expectResponse(client, isLoggedInRequest, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testLoginSucceeds() throws Exception {

        User user = UserServiceFactory.getUserService().newUser();
        user.setName("jas");
        UserServiceFactory.getUserService().create(user, "password");

        ServletUnitClient client = ServletTestHelper.login("jas", "password");

        WebRequest isLoggedInRequest = new GetMethodWebRequest(RESTORE_URL);
        InvocationContext ic = expectResponse(client, isLoggedInRequest, HttpServletResponse.SC_OK);
        JsonSessionResponse loginResponse = JsonSessionResponse.parse(ic.getServletResponse().getText());

        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getId());
        assertEquals(user.getId().getId(), loginResponse.getUserId());
        assertEquals(user.getId().getId(), loginResponse.getUser().getId());
        assertEquals(user.getName(), loginResponse.getUser().getName());

        WebRequest logoutRequest = new GetMethodWebRequest(LOGOUT_URL);
        expectResponse(client, logoutRequest, HttpServletResponse.SC_OK);
    }

}