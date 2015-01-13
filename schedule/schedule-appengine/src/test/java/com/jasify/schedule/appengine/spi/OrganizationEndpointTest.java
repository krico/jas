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

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;

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
    private OrganizationEndpoint endpoint = new OrganizationEndpoint();

    @Before
    public void datastore() {
        TestHelper.initializeDatastore(); // Starts a inMemory AppEngine datastore
        testOrganizationServiceFactory.setUp(); // see comment above

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
        endpoint.getOrganizations(newCaller(1, false));
    }

    @Test(expected = ForbiddenException.class)
    public void testAddOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(newCaller(1, false), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newCaller(1, false), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.getOrganization(newCaller(1, false), null);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateOrganizationNotAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(1, false), null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateOrganizationCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(1, true), null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveOrganizationCheckNotFound() throws Exception {
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newCaller(1, true), null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveOrganizationNotFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        service.removeOrganization(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newCaller(55, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testGetOrganizationNotFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        service.getOrganization(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.getOrganization(newCaller(55, true), key);
    }


    @Test(expected = NotFoundException.class)
    public void testUpdateOrganizationNotFoundViaEntityNotFoundException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        service.updateOrganization(organization);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(55, true), key, organization);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOrganizationNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        service.updateOrganization(organization);
        expectLastCall().andThrow(new FieldValueException(null));
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(55, true), key, organization);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateOrganizationNotFoundViaUniqueConstraintException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        service.updateOrganization(organization);
        expectLastCall().andThrow(new UniqueConstraintException(null));
        testOrganizationServiceFactory.replay();
        endpoint.updateOrganization(newCaller(55, true), key, organization);
    }

    @Test(expected = NotFoundException.class)
    public void testAddOrganizationNotFoundViaEntityNotFoundException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        Key key = Datastore.allocateId(Organization.class);
        expect(service.addOrganization(organization)).andReturn(key);
        service.getOrganization(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(newCaller(55, true), organization);
    }

    @Test(expected = BadRequestException.class)
    public void testAddOrganizationNotFoundViaFieldValueException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        service.addOrganization(organization);
        expectLastCall().andThrow(new FieldValueException(null));
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(newCaller(55, true), organization);
    }

    @Test(expected = BadRequestException.class)
    public void testAddOrganizationNotFoundViaUniqueConstraintException() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        service.addOrganization(organization);
        expectLastCall().andThrow(new UniqueConstraintException(null));
        testOrganizationServiceFactory.replay();
        endpoint.addOrganization(newCaller(55, true), organization);
    }

    @Test
    public void testUpdateOrganizationCheckFound() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        final Key key = Datastore.allocateId(Organization.class);
        final Capture<Organization> capture = newCapture();

        expect(service.updateOrganization(EasyMock.capture(capture))).andAnswer(new IAnswer<Organization>() {
            public Organization answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testOrganizationServiceFactory.replay();

        Organization result = endpoint.updateOrganization(newCaller(55, true), key, organization);
        assertEquals(result, organization);
    }

    @Test
    public void testGetOrganizations() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        ArrayList<Organization> expected = new ArrayList<>();
        expect(service.getOrganizations()).andReturn(expected);
        testOrganizationServiceFactory.replay(); //recording finished

        JasifyEndpointUser caller = newCaller(55, true); //Helper method to create a fake caller (true means admin)

        List<Organization> organizations = endpoint.getOrganizations(caller);
        // I use == here since I know the method returns it directly
        assertNotNull(organizations == expected);
    }

    @Test
    public void testGetOrganization() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        expect(service.getOrganization(organization.getId())).andReturn(organization);
        testOrganizationServiceFactory.replay();
        Organization result = endpoint.getOrganization(newCaller(55, true), organization.getId());
        assertEquals(organization, result);
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        Key key = Datastore.allocateId(Organization.class);
        expect(service.updateOrganization(organization)).andReturn(organization);
        testOrganizationServiceFactory.replay();
        JasifyEndpointUser caller = newCaller(55, true);
        Organization result = endpoint.updateOrganization(caller, key, organization);
        assertEquals(organization, result);
    }

    @Test
    public void testAddOrganization() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        expect(service.addOrganization(organization)).andReturn(organization.getId());
        expect(service.getOrganization(organization.getId())).andReturn(organization);
        testOrganizationServiceFactory.replay();
        Organization result = endpoint.addOrganization(newCaller(55, true), organization);
        assertEquals(organization, result);
    }

    @Test
    public void testRemoveOrganization() throws Exception {
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        Key key = Datastore.allocateId(Organization.class);
        service.removeOrganization(key);
        expectLastCall().once();
        testOrganizationServiceFactory.replay();
        endpoint.removeOrganization(newCaller(55, true), key);
    }
}