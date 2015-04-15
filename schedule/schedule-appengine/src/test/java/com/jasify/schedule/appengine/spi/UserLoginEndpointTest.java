package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

public class UserLoginEndpointTest {
    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();

    private UserLoginEndpoint endpoint = new UserLoginEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore();
        testUserServiceFactory.setUp();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testUserServiceFactory.tearDown();
    }

    @Test(expected = UnauthorizedException.class)
    public void testListLoginsNoUserThrows() throws Exception {
        testUserServiceFactory.replay();
        endpoint.listLogins(null, Datastore.createKey(User.class, 1));
    }

    @Test(expected = ForbiddenException.class)
    public void testListLoginsOtherUserThrows() throws Exception {
        testUserServiceFactory.replay();
        JasifyEndpointUser user = newCaller(5);
        endpoint.listLogins(user, Datastore.createKey(User.class, 1));
    }

    @Test
    public void testListLoginsSame() throws Exception {
        expect(UserServiceFactory.getUserService().getUserLogins(Datastore.createKey(User.class, 5))).andReturn(Collections.<UserLogin>emptyList());
        testUserServiceFactory.replay();
        JasifyEndpointUser user = newCaller(5);
        assertNotNull(endpoint.listLogins(user, Datastore.createKey(User.class, 5)));
    }

    @Test
    public void testListLoginsOtherAdmin() throws Exception {
        expect(UserServiceFactory.getUserService().getUserLogins(Datastore.createKey(User.class, 5))).andReturn(Collections.<UserLogin>emptyList());
        testUserServiceFactory.replay();
        JasifyEndpointUser user = newAdminCaller(2);
        assertNotNull(endpoint.listLogins(user, Datastore.createKey(User.class, 5)));
    }

    @Test
    public void testListLogins() throws Exception {

        List<UserLogin> ret = new ArrayList<>();
        ret.add(new UserLogin("Google", "1234"));

        expect(UserServiceFactory.getUserService().getUserLogins(Datastore.createKey(User.class, 23))).andReturn(ret);
        testUserServiceFactory.replay();
        User u1 = new User();
        u1.setId(Datastore.createKey(User.class, 23));
        JasifyEndpointUser user = newCaller(u1.getId().getId());
        List<UserLogin> logins = endpoint.listLogins(user, u1.getId());
        assertNotNull(logins);
        assertEquals(1, logins.size());
        UserLogin userLogin = logins.get(0);
        assertEquals(ret.get(0).getProvider(), userLogin.getProvider());
        assertEquals(ret.get(0).getUserId(), userLogin.getUserId());
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveLoginNoUser() throws Exception {
        testUserServiceFactory.replay();
        endpoint.removeLogin(null, "");
    }

    @Test
    public void testRemoveLoginThatDoesNotExist() throws Exception {
        expect(UserServiceFactory.getUserService().getLogin(EasyMock.<Key>anyObject())).andReturn(null);
        testUserServiceFactory.replay();
        endpoint.removeLogin(newCaller(1), KeyFactory.keyToString(Datastore.createKey(UserLogin.class, 23)));
    }

    @Test
    public void testRemoveLogin() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 1));
        expect(UserServiceFactory.getUserService().getLogin(login.getId())).andReturn(login);
        UserServiceFactory.getUserService().removeLogin(login.getId());
        expectLastCall();
        testUserServiceFactory.replay();
        endpoint.removeLogin(newCaller(1), KeyFactory.keyToString(login.getId()));
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveLoginOtherUserThrows() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 2));
        expect(UserServiceFactory.getUserService().getLogin(login.getId())).andReturn(login);
        testUserServiceFactory.replay();
        endpoint.removeLogin(newCaller(1), KeyFactory.keyToString(login.getId()));
    }

    @Test
    public void testRemoveLoginOtherUserAdmin() throws Exception {
        UserLogin login = new UserLogin();
        login.setId(Datastore.createKey(UserLogin.class, 23));
        login.getUserRef().setKey(Datastore.createKey(User.class, 1));
        expect(UserServiceFactory.getUserService().getLogin(login.getId())).andReturn(login);
        UserServiceFactory.getUserService().removeLogin(login.getId());
        expectLastCall();
        testUserServiceFactory.replay();
        endpoint.removeLogin(newCaller(1), KeyFactory.keyToString(login.getId()));
    }
}