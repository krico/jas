package com.jasify.schedule.appengine.spi;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.TestUserServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserService;
import com.jasify.schedule.appengine.spi.JasifyEndpoint.OrgMemberChecker;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasApiInfo;
import org.easymock.EasyMockRunner;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

@RunWith(EasyMockRunner.class)
public class JasifyEndpointTest {
    private UserService userService;

    private TestUserServiceFactory testUserServiceFactory = new TestUserServiceFactory();
    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();
    private TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();

    private JasifyEndpoint endpoint = new JasifyEndpoint();

    static JasifyEndpointUser newCaller(long id) {
        return new JasifyEndpointUser("a@b", id, false, false);
    }

    static JasifyEndpointUser newAdminCaller(long id) {
        return new JasifyEndpointUser("a@b", id, true, false);
    }

    static JasifyEndpointUser newOrgMemberCaller(long id) {
        return new JasifyEndpointUser("a@b", id, false, true);
    }

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void setUpTestServiceFactory() {
        testUserServiceFactory.setUp();
        testActivityServiceFactory.setUp();
        testOrganizationServiceFactory.setUp();
        userService = testUserServiceFactory.getUserServiceMock();
    }

    @After
    public void cleanup() {
        UserContext.clearContext();
        testUserServiceFactory.tearDown();
        testActivityServiceFactory.tearDown();
        testOrganizationServiceFactory.tearDown();
    }

    @Test
    public void testApiInfoNoUser() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(null);
        assertNotNull(info);
        assertNotNull(info.getApiVersion());
        assertFalse(info.isAuthenticated());
    }

    @Test
    public void testApiInfoWithUser() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(newCaller(1));
        assertNotNull(info);
        assertNotNull(info.getApiVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
        assertFalse(info.isOrgMember());
    }

    @Test
    public void testApiInfoWithAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(newAdminCaller(1));
        assertNotNull(info);
        assertNotNull(info.getApiVersion());
        assertTrue(info.isAuthenticated());
        assertTrue(info.isAdmin());
        assertFalse(info.isOrgMember());
    }

    @Test
    public void testApiInfoWithOrgMember() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        replay(userService);
        JasApiInfo info = endpoint.getApiInfo(newOrgMemberCaller(1));
        assertNotNull(info);
        assertNotNull(info.getApiVersion());
        assertTrue(info.isAuthenticated());
        assertFalse(info.isAdmin());
        assertTrue(info.isOrgMember());
    }

    @Test
    public void testIsNotOrgMember() throws Exception {
        testOrganizationServiceFactory.replay();
        final User user = new User("Bob");
        user.setId(Datastore.allocateId(User.class));
        Organization organization = new Organization("Org");
        ActivityType activityType = new ActivityType("Type");
        activityType.getOrganizationRef().setModel(organization);
        Activity activity = new Activity(activityType);
        activity.setId(Datastore.allocateId(Activity.class));
        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivity(activity.getId())).andReturn(activity);
        testActivityServiceFactory.replay();
        expect(userService.get(user.getId().getId())).andReturn(user);
        replay(userService);
        OrgMemberChecker orgMemberChecker = JasifyEndpoint.createFromActivityId(activity.getId());
        assertFalse(orgMemberChecker.isOrgMember(user.getId().getId()));
    }

    @Test
    public void testOrgMemberCheckerViaActivityId() throws Exception {
        testOrganizationServiceFactory.replay();
        final User user = new User("Bob");
        user.setId(Datastore.allocateId(User.class));
        Organization organization = new Organization("Org") {
            @Override
            public List<User> getUsers() {
                return Arrays.asList(user);
            }
        };
        ActivityType activityType = new ActivityType("Type");
        activityType.getOrganizationRef().setModel(organization);
        Activity activity = new Activity(activityType);
        activity.setId(Datastore.allocateId(Activity.class));
        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivity(activity.getId())).andReturn(activity);
        testActivityServiceFactory.replay();
        expect(userService.get(user.getId().getId())).andReturn(user);
        replay(userService);
        OrgMemberChecker orgMemberChecker = JasifyEndpoint.createFromActivityId(activity.getId());
        assertTrue(orgMemberChecker.isOrgMember(user.getId().getId()));
    }

    @Test
    public void testOrgMemberCheckerViaActivityTypeId() throws Exception {
        testOrganizationServiceFactory.replay();
        final User user = new User("Bob");
        user.setId(Datastore.allocateId(User.class));
        Organization organization = new Organization("Org") {
            @Override
            public List<User> getUsers() {
                return Arrays.asList(user);
            }
        };
        ActivityType activityType = new ActivityType("Type");
        activityType.getOrganizationRef().setModel(organization);
        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivityType(activityType.getId())).andReturn(activityType);
        testActivityServiceFactory.replay();
        expect(userService.get(user.getId().getId())).andReturn(user);
        replay(userService);
        OrgMemberChecker orgMemberChecker = JasifyEndpoint.createFromActivityTypeId(activityType.getId());
        assertTrue(orgMemberChecker.isOrgMember(user.getId().getId()));
    }

    @Test
    public void testOrgMemberCheckerViaSubscriptionId() throws Exception {
        testOrganizationServiceFactory.replay();
        final User user = new User("Bob");
        user.setId(Datastore.allocateId(User.class));
        Organization organization = new Organization("Org") {
            @Override
            public List<User> getUsers() {
                return Arrays.asList(user);
            }
        };
        ActivityType activityType = new ActivityType("Type");
        activityType.getOrganizationRef().setModel(organization);
        Activity activity = new Activity(activityType);
        activity.setId(Datastore.allocateId(Activity.class));
        Subscription subscription = new Subscription();
        subscription.setId(Datastore.allocateId(Subscription.class));
        subscription.getActivityRef().setModel(activity);
        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getSubscription(subscription.getId())).andReturn(subscription);
        testActivityServiceFactory.replay();
        expect(userService.get(user.getId().getId())).andReturn(user);
        replay(userService);
        OrgMemberChecker orgMemberChecker = JasifyEndpoint.createFromSubscriptionId(subscription.getId());
        assertTrue(orgMemberChecker.isOrgMember(user.getId().getId()));
    }

    @Test
    public void testOrgMemberCheckerViaOrganizationId() throws Exception {
        testActivityServiceFactory.replay();
        final User user = new User("Bob");
        user.setId(Datastore.allocateId(User.class));
        Organization organization = new Organization("Org") {
            @Override
            public List<User> getUsers() {
                return Arrays.asList(user);
            }
        };
        organization.setId(Datastore.allocateId(Organization.class));
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        expect(organizationService.getOrganization(organization.getId())).andReturn(organization);
        testOrganizationServiceFactory.replay();
        expect(userService.get(user.getId().getId())).andReturn(user);
        replay(userService);
        OrgMemberChecker orgMemberChecker = JasifyEndpoint.createFromOrganizationId(organization.getId());
        assertTrue(orgMemberChecker.isOrgMember(user.getId().getId()));
    }
}