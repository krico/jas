package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonPasswordChangeRequest;
import com.jasify.schedule.appengine.http.json.JsonResponse;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.*;

public class ChangePasswordServletTest {
    private User admin;
    private User user;

    private static String createUrl(User user) {
        return "http://schedule.jasify.com/change-password/" + user.getId().getId();
    }

    @Before
    public void servletRunner() throws Exception {
        TestHelper.initializeServletRunner();
        admin = UserServiceFactory.getUserService().newUser();
        admin.setName("test-admin");
        admin.setEmail("boss@boss.ta");
        admin.setAdmin(true);
        admin = UserServiceFactory.getUserService().create(admin, "password");
        assertNotNull(admin);
        user = UserServiceFactory.getUserService().newUser();
        user.setName("user");
        user = UserServiceFactory.getUserService().create(user, "password");
        assertNotNull(user);
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testNoInputFails() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest(createUrl(user));
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, ic.getServletResponse().getResponseCode());
    }
    @Test
    public void testUnauthorizedAccessNoUser() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest("password", "changedPassword");

        WebRequest request = new PostMethodWebRequest(createUrl(user), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testUnauthorizedAccessUserNotAdminChangingAnotherPassword() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");

        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest("password", "changedPassword");

        WebRequest request = new PostMethodWebRequest(createUrl(admin), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testFailIfNewPasswordIsEmpty() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest("password", "");
        WebRequest request = new PostMethodWebRequest(createUrl(user), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        JsonResponse jr = JsonResponse.parse(ic.getServletResponse().getText());
        assertTrue("New password cannot be empty", jr.isNok());
    }

    @Test
    public void testFailIfOldPasswordDoesNotMatch() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest("password1", "password2");
        WebRequest request = new PostMethodWebRequest(createUrl(user), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        JsonResponse jr = JsonResponse.parse(ic.getServletResponse().getText());
        assertTrue("Old password must match", jr.isNok());
    }

    @Test
    public void testUserChangesOwn() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest("password", "changedPassword");
        WebRequest request = new PostMethodWebRequest(createUrl(user), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        JsonResponse jr = JsonResponse.parse(ic.getServletResponse().getText());
        assertTrue("Password changed", jr.isOk());
        TestHelper.login("user", "changedPassword");
    }

    @Test
    public void testAdminChangesUserNoNeedForOld() throws Exception {
        ServletUnitClient client = TestHelper.login("test-admin", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(null, "byAdminPassword");
        WebRequest request = new PostMethodWebRequest(createUrl(user), IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        JsonResponse jr = JsonResponse.parse(ic.getServletResponse().getText());
        assertTrue("Password changed", jr.isOk());
        TestHelper.login("user", "byAdminPassword");
        TestHelper.login("test-admin", "password");
    }
}