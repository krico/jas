package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static org.easymock.EasyMock.*;

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
    public void testAddUserToGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addUserToGroup(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetGroupUsersNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroupUsers(null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveUserFromGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromGroup(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateGroup(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveGroupNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(null, null);
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
        endpoint.updateGroup(newCaller(1, false), null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(newCaller(1, false), new Group().getId());
    }

    @Test(expected = ForbiddenException.class)
    public void testAddUserToGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addUserToGroup(newCaller(1, false), null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveUserFromGroupNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromGroup(newCaller(1, false), null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGroupUsersNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getGroupUsers(newCaller(1, false), null);
    }

    @Test
    public void testGetGroups() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        ArrayList<Group> expected = new ArrayList<>();
        expect(service.getGroups()).andReturn(expected);
        testOrganizationServiceFactory.replay();
        List<Group> result = endpoint.getGroups(newCaller(55, true));
        assertEquals(expected, result);
    }

    @Test
    public void testGetGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        expect(service.getGroup(group.getId())).andReturn(group);
        testOrganizationServiceFactory.replay();
        Group result = endpoint.getGroup(newCaller(55, true), group.getId());
        assertEquals(group, result);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateGroupCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateGroup(newCaller(55, true), null, new Group());
    }

    @Test
    public void testUpdateGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        final Key key = Datastore.allocateId(Group.class);
        final Capture<Group> capture = newCapture();

        expect(service.updateGroup(EasyMock.capture(capture))).andAnswer(new IAnswer<Group>() {
            public Group answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testOrganizationServiceFactory.replay();

        Group result = endpoint.updateGroup(newCaller(55, true), key, group);
        assertEquals(result, group);
    }

    @Test
    public void testAddGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();

        expect(service.addGroup(group)).andReturn(group.getId());
        expect(service.getGroup(group.getId())).andReturn(group);
        testOrganizationServiceFactory.replay();

        Group result = endpoint.addGroup(newCaller(55, true), group);

        assertEquals(group, result);
    }

    @Test
    public void testRemoveGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Group.class);
        service.removeGroup(key);
        expectLastCall().once();
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(newCaller(55, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveGroupCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(newCaller(55, true), null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetGroupNotFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Group.class);
        service.getGroup(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.getGroup(newCaller(55, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateGroupNotFoundViaEntityNotFoundException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        Key key = Datastore.allocateId(Group.class);
        service.updateGroup(group);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.updateGroup(newCaller(55, true), key, group);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateGroupNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        Key key = Datastore.allocateId(Group.class);
        service.updateGroup(group);
        expectLastCall().andThrow(new FieldValueException(null));
        testOrganizationServiceFactory.replay();
        endpoint.updateGroup(newCaller(55, true), key, group);
    }

    @Test(expected = NotFoundException.class)
    public void testAddGroupNotFoundViaEntityNotFoundException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        Key key = Datastore.allocateId(Group.class);

        expect(service.addGroup(group)).andReturn(key);
        service.getGroup(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.addGroup(newCaller(55, true), group);
    }

    @Test(expected = BadRequestException.class)
    public void testAddGroupNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Group group = new Group();
        service.addGroup(group);
        expectLastCall().andThrow(new FieldValueException(null));
        testOrganizationServiceFactory.replay();
        endpoint.addGroup(newCaller(55, true), group);
    }

    @Test(expected = NotFoundException.class)
    public void testGetGroupUsersNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Group.class);
        service.getGroup(key);
        expectLastCall().andThrow(new EntityNotFoundException(null));
        testOrganizationServiceFactory.replay();
        endpoint.getGroupUsers(newCaller(55, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testAddUserToGroupNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key groupId = Datastore.allocateId(Group.class);
        Key userId = Datastore.allocateId(User.class);
        service.addUserToGroup(groupId, userId);
        expectLastCall().andThrow(new EntityNotFoundException(null));
        testOrganizationServiceFactory.replay();
        endpoint.addUserToGroup(newCaller(55, true), groupId, userId);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveGroupNotFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Group.class);
        service.removeGroup(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.removeGroup(newCaller(55, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveUserFromGroupNotFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key groupId = Datastore.allocateId(Group.class);
        Key userId = Datastore.allocateId(User.class);
        service.removeUserFromGroup(groupId, userId);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromGroup(newCaller(55, true), groupId, userId);
    }

    @Test
    public void testAddUserToGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key groupId = Datastore.allocateId(Group.class);
        Key userId = Datastore.allocateId(User.class);
        service.addUserToGroup(groupId, userId);
        expectLastCall().once();
        testOrganizationServiceFactory.replay();
        endpoint.addUserToGroup(newCaller(55, true), groupId, userId);
    }

    @Test
    public void testRemoveUserFromGroup() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key groupId = Datastore.allocateId(Group.class);
        Key userId = Datastore.allocateId(User.class);
        service.removeUserFromGroup(groupId, userId);
        expectLastCall().once();
        testOrganizationServiceFactory.replay();
        endpoint.removeUserFromGroup(newCaller(55, true), groupId, userId);
    }

    @Test
    public void testGroupUsers() throws Exception {
        Group mockGroup = createMock(Group.class);
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        expect(mockGroup.getUsers()).andReturn(userList);
        expectLastCall().once();
        replay(mockGroup);

        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Group.class);
        expect(service.getGroup(key)).andReturn(mockGroup);
        expectLastCall().once();
        testOrganizationServiceFactory.replay();

        List<User> result = endpoint.getGroupUsers(newCaller(55, true), key);

        assertEquals(userList, result);
    }
}