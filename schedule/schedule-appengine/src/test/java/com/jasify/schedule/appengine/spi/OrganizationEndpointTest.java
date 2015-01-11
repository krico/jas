package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.expect;

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

    // 4)
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
        testOrganizationServiceFactory.replay();
//TODO
    }

    @Test
    public void testUpdateOrganization() throws Exception {
        testOrganizationServiceFactory.replay();
//TODO
    }

    @Test
    public void testAddOrganization() throws Exception {
        testOrganizationServiceFactory.replay();
//TODO
    }

    @Test
    public void testRemoveOrganization() throws Exception {
        testOrganizationServiceFactory.replay();
//TODO
    }
}