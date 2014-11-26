package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Query;
import com.google.gson.reflect.TypeToken;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.jasify.schedule.appengine.util.JSON;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class UsersServletTest {

    public static final TypeToken<Collection<JsonUser>> TYPE_TOKEN = new TypeToken<Collection<JsonUser>>() {
    };
    private User admin;
    private User user;
    private List<User> allUsers;

    private static String createUrl(int page, int size, boolean ascending) {
        return "http://schedule.jasify.com/users/page/" + page + "/size/" + size + "/sort/" + (ascending ? "asc" : "desc");
    }

    @Before
    public void servletRunner() throws UsernameExistsException {
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
        TestHelper.createUsers(100);

        allUsers = new ArrayList<>();
        allUsers.addAll(UserServiceFactory.getUserService().list(Query.SortDirection.ASCENDING, 0,0));
    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testUnauthorizedAccessNoUser() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new GetMethodWebRequest(createUrl(1, 10, true));
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testUnauthorizedAccessUserNotAdmin() throws Exception {
        ServletUnitClient client = TestHelper.login("user", "password");
        WebRequest request = new GetMethodWebRequest(createUrl(1, 10, true));
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    @Test
    public void testGetWithNoQuery() throws Exception {

        Collections.sort(allUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return Long.compare(o1.getId().getId(), o2.getId().getId());
            }
        });


        ServletUnitClient client = TestHelper.login("test-admin", "password");

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

        ServletUnitClient client = TestHelper.login("test-admin", "password");

        InvocationContext ic = client.newInvocation(new GetMethodWebRequest(createUrl(1, 10, true) + "?field=name&query=test-admin"));

        ic.service();

        List<JsonUser> users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(1, users.size());

        assertEquals(admin.getId().getId(), users.get(0).getId());
    }

    @Test
    public void testGetByEmail() throws Exception {

        ServletUnitClient client = TestHelper.login("test-admin", "password");

        InvocationContext ic = client.newInvocation(new GetMethodWebRequest(createUrl(1, 10, true) + "?field=email&query=boss"));

        ic.service();

        List<JsonUser> users = JSON.fromJson(ic.getServletResponse().getText(), TYPE_TOKEN.getType());
        assertNotNull(users);
        assertEquals(1, users.size());

        assertEquals(admin.getId().getId(), users.get(0).getId());
    }
}