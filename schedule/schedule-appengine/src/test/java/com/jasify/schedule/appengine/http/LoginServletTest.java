package com.jasify.schedule.appengine.http;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonLoginRequest;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jasify.schedule.appengine.TestHelper.si;
import static junit.framework.TestCase.*;

public class LoginServletTest {

    @Before
    public void startServletRunner() {
        TestHelper.initializeServletRunner(
                si("login", LoginServlet.class),
                si("logout", LogoutServlet.class),
                si("isLoggedIn", IsLoggedInServlet.class)
        );
    }

    @After
    public void stopServletRunner() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testEmptyLoginFails() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/login");
        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertTrue(jr.isNok());
        assertFalse(jr.isOk());
        assertTrue(StringUtils.isNotBlank(jr.getNokText()));
    }

    @Test
    public void testBadCredentialsLoginFails() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonLoginRequest req = new JsonLoginRequest("jas", "password");
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/login", IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertTrue(jr.isNok());
        assertFalse(jr.isOk());
        assertTrue(StringUtils.isNotBlank(jr.getNokText()));
        WebRequest isLoggedInRequest = new GetMethodWebRequest("http://schedule.jasify.com/isLoggedIn");
        WebResponse isLoggedInResponse = client.getResponse(isLoggedInRequest);
        assertNotNull("No response received", isLoggedInResponse);
        assertEquals("content type", JSON.CONTENT_TYPE, isLoggedInResponse.getContentType());
        String isLoggedInText = response.getText();
        assertNotNull(isLoggedInText);
        JsonResponse jr2 = JsonResponse.parse(isLoggedInText);
        assertNotNull(jr2);
        assertTrue(jr2.isNok());

        WebRequest logoutRequest = new GetMethodWebRequest("http://schedule.jasify.com/logout");
        WebResponse logoutResponse = client.getResponse(logoutRequest);
        assertNotNull("No response received", logoutResponse);
        assertEquals("content type", JSON.CONTENT_TYPE, logoutResponse.getContentType());
        String logoutText = response.getText();
        assertNotNull(logoutText);
        JsonResponse jr3 = JsonResponse.parse(logoutText);
        assertNotNull(jr3);
        assertTrue(jr3.isNok());


    }

    @Test
    public void testLoginSucceeds() throws Exception {

        User user = UserServiceFactory.getUserService().newUser();
        user.setName("jas");
        UserServiceFactory.getUserService().create(user, "password");

        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonLoginRequest req = new JsonLoginRequest("jas", "password");
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/login", IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        WebResponse response = client.getResponse(request);
        assertNotNull("No response received", response);
        assertEquals("content type", JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonResponse jr = JsonResponse.parse(text);
        assertNotNull(jr);
        assertFalse(jr.isNok());
        assertTrue(jr.isOk());
        assertTrue(StringUtils.isBlank(jr.getNokText()));

        WebRequest isLoggedInRequest = new GetMethodWebRequest("http://schedule.jasify.com/isLoggedIn");
        WebResponse isLoggedInResponse = client.getResponse(isLoggedInRequest);
        assertNotNull("No response received", isLoggedInResponse);
        assertEquals("content type", JSON.CONTENT_TYPE, isLoggedInResponse.getContentType());
        String isLoggedInText = response.getText();
        assertNotNull(isLoggedInText);
        JsonResponse jr2 = JsonResponse.parse(isLoggedInText);
        assertNotNull(jr2);
        assertTrue(jr2.isOk());

        WebRequest logoutRequest = new GetMethodWebRequest("http://schedule.jasify.com/logout");
        WebResponse logoutResponse = client.getResponse(logoutRequest);
        assertNotNull("No response received", logoutResponse);
        assertEquals("content type", JSON.CONTENT_TYPE, logoutResponse.getContentType());
        String logoutText = response.getText();
        assertNotNull(logoutText);
        JsonResponse jr3 = JsonResponse.parse(logoutText);
        assertNotNull(jr3);
        assertTrue(jr3.isOk());
    }

}