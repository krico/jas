package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonSignUpUser;
import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static com.jasify.schedule.appengine.TestHelper.assertEqualsNoMillis;
import static junit.framework.TestCase.*;
import static org.apache.commons.io.IOUtils.toInputStream;

public class UserServletTest {
    private ServletRunner servletRunner;
    private User user;

    @Before
    public void servletRunner() throws UsernameExistsException {
        TestHelper.initializeServletRunner();
        user = UserServiceFactory.getUserService().newUser();
        user.setName("Jas");
        user = UserServiceFactory.getUserService().create(user, "password");
        assertNotNull(user);
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testUnauthenticatedGetIsUnauthorized() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testUnauthenticatedPostIsUnauthorized() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testGet() throws Exception {
        ServletUnitClient client = TestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getResponseCode());
        assertEquals(JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonUser jUser = JsonUser.parse(text);
        assertNotNull(jUser);
        assertEquals(user.getId().getId(), jUser.getId());
        assertEqualsNoMillis(user.getCreated(), jUser.getCreated());
        assertEqualsNoMillis(user.getModified(), jUser.getModified());
        assertEquals(user.getName(), jUser.getName().toLowerCase());
        assertEquals(user.getNameWithCase(), jUser.getName());
        assertEquals(user.getEmail(), jUser.getEmail());
        assertEquals(user.getAbout(), jUser.getAbout());
    }

    @Test
    public void testPost() throws Exception {
        ServletUnitClient client = TestHelper.login("jas", "password");
        JsonUser updatedUser = new JsonUser(user);
        updatedUser.setAbout("Now I have an about...");
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId(), toInputStream(updatedUser.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getResponseCode());
        assertEquals(JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonUser jUser = JsonUser.parse(text);
        assertNotNull(jUser);
        assertEquals(user.getId().getId(), jUser.getId());
        assertEqualsNoMillis(user.getCreated(), jUser.getCreated());
        assertEqualsNoMillis(user.getModified(), jUser.getModified());
        assertEquals(user.getName(), jUser.getName().toLowerCase());
        assertEquals(user.getNameWithCase(), jUser.getName());
        assertEquals(user.getEmail(), jUser.getEmail());

        assertEquals(updatedUser.getAbout(), jUser.getAbout());
        User db = UserServiceFactory.getUserService().get(jUser.getId());
        assertEquals(db.getAbout(), jUser.getAbout());
    }

    @Test
    public void testGetAnotherUserIsUnauthorized() throws Exception {
        ServletUnitClient client = TestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + (user.getId().getId() + 1));
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testGetBadPath() throws Exception {
        ServletUnitClient client = TestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/wrong");
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testGetCurrent() throws Exception {
        ServletUnitClient client = TestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/current");
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        WebResponse response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getResponseCode());
        assertEquals(JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonUser jUser = JsonUser.parse(text);
        assertNotNull(jUser);
        assertEquals(user.getId().getId(), jUser.getId());
        assertEqualsNoMillis(user.getCreated(), jUser.getCreated());
        assertEqualsNoMillis(user.getModified(), jUser.getModified());
        assertEquals(user.getName(), jUser.getName().toLowerCase());
        assertEquals(user.getNameWithCase(), jUser.getName());
        assertEquals(user.getEmail(), jUser.getEmail());
        assertEquals(user.getAbout(), jUser.getAbout());
    }

    @Test
    public void testPostNewUserFailsWhenMissing() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonSignUpUser signUp = new JsonSignUpUser();
        signUp.setName("new");

        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);

        ic.service();

        WebResponse response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getResponseCode());

        signUp.setEmail("nEw@jasify.com");

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        ic = client.newInvocation(request);

        ic.service();

        response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getResponseCode());

        signUp.setPassword("abcde");

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        ic = client.newInvocation(request);

        ic.service();

        response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getResponseCode());

        signUp.setConfirmPassword(signUp.getPassword() + "x");

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        ic = client.newInvocation(request);

        ic.service();

        response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getResponseCode());

        signUp.setConfirmPassword(signUp.getPassword());

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        ic = client.newInvocation(request);

        ic.service();

        response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getResponseCode());
        assertEquals(JSON.CONTENT_TYPE, response.getContentType());
        String text = response.getText();
        assertNotNull(text);
        JsonUser newUser = JsonUser.parse(text);
        assertTrue("reg should work", newUser.getId() > 0);
        assertEquals(signUp.getName(), newUser.getName());
        assertEquals(signUp.getEmail(), newUser.getEmail());
        assertNotNull(newUser.getCreated());

        //try again, user should exist
        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        ic = client.newInvocation(request);

        ic.service();

        response = ic.getServletResponse();
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getResponseCode());
    }

}