package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonPasswordChangeRequest;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
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
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class ChangePasswordServletTest {
    public static final String URL = "http://schedule.jasify.com/auth/change-password";
    private User admin;
    private User user;

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
        ServletUnitClient client = TestHelper.login("user", "password");
        WebRequest request = new PostMethodWebRequest(URL);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testUnauthorizedAccessNoUser() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(user, "password", "changedPassword");

        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testUnauthorizedAccessUserNotAdminChangingAnotherPassword() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");

        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(admin, "password", "changedPassword");

        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testFailIfNewPasswordIsEmpty() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(user, "password", "");
        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testFailIfOldPasswordDoesNotMatch() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(user, "password1", "password2");
        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testUserChangesOwn() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(user, "password", "changedPassword");
        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);

        expectResponse(client, request, HttpServletResponse.SC_OK);

        TestHelper.login("user", "changedPassword");
    }

    @Test
    public void testAdminChangesUserNoNeedForOld() throws Exception {
        ServletUnitClient client = TestHelper.login("test-admin", "password");
        JsonPasswordChangeRequest req = new JsonPasswordChangeRequest(user, null, "byAdminPassword");
        WebRequest request = new PostMethodWebRequest(URL, IOUtils.toInputStream(req.toJson()), JSON.CONTENT_TYPE);

        expectResponse(client, request, HttpServletResponse.SC_OK);

        TestHelper.login("user", "byAdminPassword");
        TestHelper.login("test-admin", "password");
    }
}