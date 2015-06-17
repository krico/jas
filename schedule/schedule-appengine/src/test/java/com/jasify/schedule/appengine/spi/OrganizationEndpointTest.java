package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.*;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.*;

public class OrganizationEndpointTest {
    /**
     * The "Test*ServiceFactory" replaced the instance used by *ServiceFactory.get*Service();
     * Way to use them is, in case of * being Organization:
     * t = new TestOrganizationServiceFactory();
     * t.setUp();
     * EasyMock.expect( OrganizationServiceFactory.getOrganizationService().whatever() ).andWhatever();
     * t.replay();
     * <p/>
     * your test code of something that uses OrganizationServiceFactory.getOrganizationService
     * <p/>
     * t.tearDown(); //this verifies the mock and resets the service factory back to normal
     */
    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();

    /**
     * The endpoint we are testing.  Currently the Endpoints are basically controlling permissions and calling through to
     * the actual data model.
     */
    private OrganizationEndpoint endpoint;

    @Before
    public void datastore() {
        TestHelper.initializeDatastore(); // Starts a inMemory AppEngine datastore
        testOrganizationServiceFactory.setUp(); // see comment above
        endpoint = new OrganizationEndpoint();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore(); // Stops it
        testOrganizationServiceFactory.tearDown(); // you should know by now :-)
    }

    /**
     * Since the main role of an endpoint is to protect the service from the outside world,
     * we focus our test and should make sure that
     * 1) Unauthenticated users don't get access
     * 2) Unauthenticated (not logged in) get the proper exception so that the web-client can handle by telling them to login
     * 3) Unauthorized (in this case, not admin, but on user service, user can only change his on profile for example) get the proper exception (Forbidden)
     * 4) The proper parameters are passed to the Service (this you do with the Mock)
     */


    // 1) + 2)
    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationsNoUser() throws Exception {
        testOrganizationServiceFactory.replay(); //I don't like doing this, I wish I knew how to detect if this is replayed or not in the tearDown method
        endpoint.getOrganizations(null);
    }

    // 3)
    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationsNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganizations(newCaller(1));
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddOrganizationsNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(newCaller(1), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganization(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddUserToOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addUserToOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddUserToOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addUserToOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveUserFromOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveUserFromOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddGroupToOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addGroupToOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddGroupToOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addGroupToOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveGroupFromOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroupFromOrganization(null, null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveGroupFromOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroupFromOrganization(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationUsersNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganizationUsers(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationUsersNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganizationUsers(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetOrganizationGroupsNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganizationGroups(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationGroupsNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganizationGroups(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateOrganizationNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(null, null, new Organization());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(1), null, new Organization());
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateOrganizationCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newAdminCaller(1), null, new Organization());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveOrganizationCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newAdminCaller(1), null);
    }
}