package com.jasify.schedule.appengine.http.servlet;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertEquals;

public class UserServletTest {
    private ServletRunner servletRunner;
    private User user;

    @Before
    public void servletRunner() throws UsernameExistsException {
        TestHelper.initializeServletRunner();
        user = UserServiceFactory.getUserService().newUser();
        user.setName("jas");
        UserServiceFactory.getUserService().create(user, "password");

    }

    @After
    public void stopDatastore() {
        TestHelper.cleanupServletRunner();
    }

    @Test
    public void testUnauthenticatedGet() throws Exception {
        ServletUnitClient client = TestHelper.servletRunner().newClient();
        WebRequest request = new GetMethodWebRequest("http://schedule.jasify.com/user/" + user.getId().getId());
        InvocationContext ic = client.newInvocation(request);
        ic.service();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, ic.getServletResponse().getResponseCode());
    }

    public void testGet() throws Exception {
    }


}