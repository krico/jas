package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newOrgMemberCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class JasifyEndpointNotMockedTest {

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetUserService() throws UnauthorizedException {
        UserService service = UserServiceFactory.getUserService();
        assertNotNull(service);
        assertEquals(UserServiceFactory.getUserService(), service);
    }

    @Test(expected = UnauthorizedException.class)
    public void testMustBeLoggedInThrowsNonAuthorizedOnNull() throws UnauthorizedException {
        JasifyEndpoint.mustBeLoggedIn(null);
    }

    @Test
    public void testMustBeLoggedIn() throws UnauthorizedException {
        JasifyEndpointUser user = newCaller(1);
        assertEquals(user, JasifyEndpoint.mustBeLoggedIn(user));
    }

    @Test(expected = UnauthorizedException.class)
    public void testMustBeSameUserOrAdminThrowsNonAuthorizedOnNull() throws UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeSameUserOrAdmin(null, 1);
    }

    @Test(expected = ForbiddenException.class)
    public void testMustBeSameUserOrAdminThrowsForbiddenWhenNotSameUser() throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser user = newCaller(1);
        JasifyEndpoint.mustBeSameUserOrAdmin(user, 2);
    }

    @Test
    public void testMustBeSameUserSameUser() throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser user = newCaller(1);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 1));
    }

    @Test(expected = UnauthorizedException.class)
    public void testMustBeAdminThrowsNonAuthorizedOnNull() throws UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeAdmin(null);
    }

    @Test(expected = ForbiddenException.class)
    public void testMustBeAdminThrowsForbiddenWhenNotSameUser() throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser user = newCaller(1);
        JasifyEndpoint.mustBeAdmin(user);
    }

    @Test
    public void testMustBeAdmin() throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser user = newAdminCaller(1);
        assertEquals(user, JasifyEndpoint.mustBeAdmin(user));
    }

    @Test
    public void testMustBeSameUserOrAdminWithAdmin() throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser user = newAdminCaller(1);
        assertEquals(user, JasifyEndpoint.mustBeSameUserOrAdmin(user, 2));
    }

    @Test
    public void testKey() throws NotFoundException {
        Key id = KeyFactory.createKey("k", 1);
        assertEquals(id, JasifyEndpoint.checkFound(id));
    }

    @Test(expected = NotFoundException.class)
    public void testKeyThrowsNotFound() throws NotFoundException {
        JasifyEndpoint.checkFound(null);
    }

    @Test(expected = NotFoundException.class)
    public void mustBeSameUserOrAdminWithNullKey() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeSameUserOrAdmin(newAdminCaller(1), null);
    }

    @Test(expected = ForbiddenException.class)
    public void mustBeSameUserOrAdminWithKeyNotAdmin() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeSameUserOrAdmin(newCaller(1), KeyFactory.createKey("K", 2));
    }

    @Test(expected = ForbiddenException.class)
    public void mustBeAdminOrOrgMemberThrowsForbiddenException() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeAdminOrOrgMember(newCaller(1), new JasifyEndpoint.OrgMemberChecker(null) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                return null;
            }
            @Override
            public boolean isOrgMember(long userId) throws EntityNotFoundException {
                return false;
            }
        });
    }


    @Test(expected = NotFoundException.class)
    public void mustBeAdminOrOrgMemberThrowsNotFoundException() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeAdminOrOrgMember(newOrgMemberCaller(1), new JasifyEndpoint.OrgMemberChecker(null) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                throw new EntityNotFoundException();
            }
        });
    }

    @Test(expected = NotFoundException.class)
    public void mustBeSameUserOrAdminOrOrgMemberThrowsNotFoundException() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeSameUserOrAdminOrOrgMember(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void mustBeSameUserOrAdminOrOrgMemberThrowsForbiddenException() throws NotFoundException, UnauthorizedException, ForbiddenException {
        JasifyEndpoint.mustBeSameUserOrAdminOrOrgMember(newCaller(1), 2, null);
    }
}