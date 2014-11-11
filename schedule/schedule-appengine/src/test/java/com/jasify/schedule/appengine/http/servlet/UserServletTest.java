package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.TypeUtil;
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

import static com.jasify.schedule.appengine.TestHelper.assertEqualsNoMillis;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
        assertEquals(TypeUtil.toString(user.getEmail()), jUser.getEmail());
        assertEquals(TypeUtil.toString(user.getAbout()), jUser.getAbout());
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
        assertEquals(TypeUtil.toString(user.getEmail()), jUser.getEmail());
        assertEquals(TypeUtil.toString(user.getAbout()), jUser.getAbout());
    }

}