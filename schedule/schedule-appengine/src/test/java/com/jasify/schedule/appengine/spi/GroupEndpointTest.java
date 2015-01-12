package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.*;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

public class GroupEndpointTest {

    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();

    /**
     * The endpoint we are testing.  Currently the Endpoints are basically controlling permissions and calling through to
     * the actual data model.
     */
    private GroupEndpoint endpoint = new GroupEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore(); // Starts a inMemory AppEngine datastore
        testOrganizationServiceFactory.setUp();

    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore(); // Stops it
        testOrganizationServiceFactory.tearDown();
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetGroupsNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroups(null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroup(null, new Group().getId());
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addGroup(null, new Group());
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        Group group = new Group();
        endpoint.updateGroup(null, group.getId(), group);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(null, new Group().getId());
    }

    @Test(expected = ForbiddenException.class)
    public void testGetGroupsNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroups(newCaller(1, false));
    }

    @Test(expected = ForbiddenException.class)
    public void testGetGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroup(newCaller(1, false), new Group().getId());
    }

    @Test(expected = ForbiddenException.class)
    public void testAddGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addGroup(newCaller(1, false), new Group());
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        Group group = new Group();
        endpoint.updateGroup(newCaller(1, false), group.getId(), group);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(newCaller(1, false), new Group().getId());
    }

    @Test
    public void testGetGroups() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        ArrayList<Group> expected = new ArrayList<>();
        expect(service.getGroups()).andReturn(expected);
        testOrganizationServiceFactory.replay();

        JasifyEndpointUser caller = newCaller(55, true);

        List<Group> groups = endpoint.getGroups(caller);

        assertNotNull(groups == expected);
    }

    @Test
    public void testGetGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();

        Group expected = new Group();

        expect(service.getGroup(expected.getId())).andReturn(expected);
        testOrganizationServiceFactory.replay();

        JasifyEndpointUser caller = newCaller(55, true);

        Group group = endpoint.getGroup(caller, expected.getId());

        assertNotNull(group == expected);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateGroupCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();

        JasifyEndpointUser caller = newCaller(55, true);

        endpoint.updateGroup(caller, null, new Group());
    }

    @Test
    public void testUpdateGroupCheckFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();

        Group expected = new Group();
        Key key = KeyFactory.createKey("Test", 22);

        expect(service.updateGroup(expected)).andReturn(expected);

        testOrganizationServiceFactory.replay();

        JasifyEndpointUser caller = newCaller(55, true);

        Group group = endpoint.updateGroup(caller, key, expected);

        assertNotNull(group == expected);
        assertEquals(key, expected.getId());
    }

    @Test
    public void testAddGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();

        Group expected = new Group();

        expect(service.addGroup(expected)).andReturn(expected.getId());
        expect(service.getGroup(expected.getId())).andReturn(expected);
        testOrganizationServiceFactory.replay();

        JasifyEndpointUser caller = newCaller(55, true);

        Group group = endpoint.addGroup(caller, expected);

        assertNotNull(group == expected);
    }

    @Test
    public void testRemoveGroupCheckFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();

        Group expected = new Group();
        JasifyEndpointUser caller = newCaller(55, true);
        Key key = KeyFactory.createKey("Test", 22);

        endpoint.removeGroup(caller, key);
        expectLastCall().times(8); // TODO
        testOrganizationServiceFactory.replay();

        endpoint.removeGroup(caller, key);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveGroupCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        JasifyEndpointUser caller = newCaller(55, true);
        endpoint.removeGroup(caller, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveGroupNotFound() throws Exception {
        testOrganizationServiceFactory.replay(); // TODO
        JasifyEndpointUser caller = newCaller(55, true);
        endpoint.removeGroup(caller, null);
    }
}