package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Query;
import com.google.gson.reflect.TypeToken;
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
import com.meterware.servletunit.ServletUnitClient;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.jasify.schedule.appengine.TestHelper.assertEqualsNoMillis;
import static com.jasify.schedule.appengine.http.servlet.ServletTestHelper.expectResponse;
import static junit.framework.TestCase.*;
import static org.apache.commons.io.IOUtils.toInputStream;

public class UserServletTest {
    public static final TypeToken<Collection<JsonUser>> TYPE_TOKEN = new TypeToken<Collection<JsonUser>>() {
    };
    private User admin;
    private User user;
    private List<User> allUsers;

    private static String createUrl(int page, int size, boolean ascending) {
        return "http://schedule.jasify.com/user?page=" + page + "&size=" + size + "&sort=" + (ascending ? "asc" : "desc");
    }

    @Before
    public void servletRunner() throws UsernameExistsException {
        TestHelper.initializeServletRunner();
        user = UserServiceFactory.getUserService().newUser();
        user.setName("Jas");
        user = UserServiceFactory.getUserService().create(user, "password");

        admin = UserServiceFactory.getUserService().newUser();
        admin.setName("TestAdmin");
        admin.setAdmin(true);
        admin.setEmail("boss@jasify.com");
        admin = UserServiceFactory.getUserService().create(admin, "password");

        TestHelper.createUsers(100);

        allUsers = new ArrayList<>();
        allUsers.addAll(UserServiceFactory.getUserService().list(Query.SortDirection.ASCENDING, 0, 0));
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testUnauthenticatedGetIsUnauthorized() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testUnauthenticatedPostIsUnauthorized() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testGet() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("jas", "password");
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
        assertEquals(user.getEmail(), jUser.getEmail());
        assertEquals(user.getAbout(), jUser.getAbout());
    }

    @Test
    public void testPost() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("jas", "password");
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
        assertEquals(user.getEmail(), jUser.getEmail());

        assertEquals(updatedUser.getAbout(), jUser.getAbout());
        User db = UserServiceFactory.getUserService().get(jUser.getId());
        assertEquals(db.getAbout(), jUser.getAbout());
    }

    @Test
    public void testPostSaveAdmin() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("TestAdmin", "password");
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
        assertEquals(user.getEmail(), jUser.getEmail());

        assertEquals(updatedUser.getAbout(), jUser.getAbout());
        User db = UserServiceFactory.getUserService().get(jUser.getId());
        assertEquals(db.getAbout(), jUser.getAbout());
    }

    @Test
    public void testPostSaveAdminInvalidId() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("TestAdmin", "password");
        JsonUser updatedUser = new JsonUser(user);
        updatedUser.setAbout("Now I have an about...");
        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user/" + 1976071, toInputStream(updatedUser.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testGetAnotherUserIsUnauthorized() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + (user.getId().getId() + 1));
        expectResponse(client, request, HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testGetBadPath() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/wrong");
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testPostNewUserFailsWhenMissing() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        JsonSignUpUser signUp = new JsonSignUpUser();
        signUp.setName("new");

        WebRequest request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);

        signUp.setEmail("nEw@jasify.com");

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);

        signUp.setPassword("abcde");

        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        InvocationContext ic = expectResponse(client, request, HttpServletResponse.SC_OK);
        assertEquals(JSON.CONTENT_TYPE, ic.getServletResponse().getContentType());
        String text = ic.getServletResponse().getText();
        assertNotNull(text);
        JsonUser newUser = JsonUser.parse(text);
        assertTrue("reg should work", newUser.getId() > 0);
        assertEquals(signUp.getName(), newUser.getName());
        assertEquals(StringUtils.lowerCase(signUp.getEmail()), newUser.getEmail());
        assertNotNull(newUser.getCreated());

        //try again, user should exist
        request = new PostMethodWebRequest("http://schedule.jasify.com/user", toInputStream(signUp.toJson()), JSON.CONTENT_TYPE);
        expectResponse(client, request, HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testUnauthorizedAccessNoUser() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new GetMethodWebRequest(createUrl(1, 10, true));
        expectResponse(client, request, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testUnauthorizedAccessUserNotAdmin() throws Exception {
        ServletUnitClient client = ServletTestHelper.login("jas", "password");
        WebRequest request = new GetMethodWebRequest(createUrl(1, 10, true));
        expectResponse(client, request, HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testGetWithNoQuery() throws Exception {

        Collections.sort(allUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Long.compare(o1.getId().getId(), o2.getId().getId());
            }
        });


        ServletUnitClient client = ServletTestHelper.login("TestAdmin", "password");

        InvocationContext ic = client.newInvocation(new GetMethodWebRequest(createUrl(1, 10, true)));

        ic.service();

        List<JsonUser> users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(10, users.size());

        for (int i = 0; i < 10; ++i) {
            assertEquals(allUsers.get(i).getId().getId(), users.get(i).getId());
        }

        ic = client.newInvocation(new GetMethodWebRequest(createUrl(3, 5, true)));

        ic.service();

        users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(5, users.size());

        for (int i = 0; i < 5; ++i) {
            assertEquals(allUsers.get(i + 10).getId().getId(), users.get(i).getId());
        }


        ic = client.newInvocation(new GetMethodWebRequest(createUrl(3, 5, false)));

        ic.service();

        users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(5, users.size());

        for (int i = 0; i < 5; ++i) {
            assertEquals(allUsers.get((allUsers.size() - 1) - (10 + i)).getId().getId(), users.get(i).getId());
        }
    }

    @Test
    public void testGetByName() throws Exception {

        ServletUnitClient client = ServletTestHelper.login("TestAdmin", "password");

        InvocationContext ic = client.newInvocation(new GetMethodWebRequest(createUrl(1, 10, true) + "&field=name&query=testadmin"));

        ic.service();

        List<JsonUser> users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(1, users.size());

        assertEquals(admin.getId().getId(), users.get(0).getId());
    }

    @Test
    public void testGetByEmail() throws Exception {

        ServletUnitClient client = ServletTestHelper.login("TestAdmin", "password");

        InvocationContext ic = client.newInvocation(new GetMethodWebRequest(createUrl(1, 10, true) + "&field=email&query=boss"));

        ic.service();

        List<JsonUser> users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(1, users.size());

        assertEquals(admin.getId().getId(), users.get(0).getId());
    }
}