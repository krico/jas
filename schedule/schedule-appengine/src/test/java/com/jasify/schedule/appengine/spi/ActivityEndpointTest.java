package com.jasify.schedule.appengine.spi;

import com.google.api.client.util.Lists;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.*;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

/**
 * @author wszarmach
 * @since 19/01/15.
 */
public class ActivityEndpointTest {
    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();
    private TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
    private OrganizationService organizationService;
    private ActivityService activityService;
    private Organization defaultOrganization;
    private ActivityType defaultActivityType;
    private Activity defaultActivity;
    private ActivityPackage defaultActivityPackage;
    private User defaultUser;
    private Subscription defaultSubscription;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ActivityEndpoint endpoint = new ActivityEndpoint();

    private Organization createOrganization() {
        Organization organization = new Organization();
        Datastore.put(organization);
        return organization;
    }

    private ActivityType createActivityType(Organization organization) {
        ActivityType activityType = new ActivityType();
        activityType.getOrganizationRef().setModel(organization);
        Datastore.put(activityType);
        return activityType;
    }

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        Datastore.put(activity);
        return activity;
    }

    private ActivityPackage createActivityPackage() {
        ActivityPackage activityPackage = new ActivityPackage();
        Datastore.put(activityPackage);
        return activityPackage;
    }

    private User createUser() {
        User user = new User("@email.com");
        Datastore.put(user);
        return user;
    }

    private Subscription createSubscription() {
        Subscription subscription = new Subscription();
        Datastore.put(subscription);
        return subscription;
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        testActivityServiceFactory.setUp();
        testOrganizationServiceFactory.setUp();
        activityService = ActivityServiceFactory.getActivityService();
        organizationService = OrganizationServiceFactory.getOrganizationService();
        defaultOrganization = createOrganization();
        defaultActivityType = createActivityType(defaultOrganization);
        defaultActivity = createActivity(defaultActivityType);
        defaultActivityPackage = createActivityPackage();
        defaultUser = createUser();
        defaultSubscription = createSubscription();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testActivityServiceFactory.tearDown();
        testOrganizationServiceFactory.tearDown();
    }

    // TEST: GetActivityTypes
    @Test
    public void testGetActivityTypes() throws Exception {
        List<ActivityType> activityTypeList = new ArrayList<>();
        activityTypeList.add(defaultActivityType);
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityTypes(defaultOrganization)).andReturn(activityTypeList);
        testActivityServiceFactory.replay();
        com.google.api.server.spi.auth.common.User user = new com.google.api.server.spi.auth.common.User("Email");
        assertEquals(activityTypeList, endpoint.getActivityTypes(user, defaultOrganization.getId()));
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypesThrowsNotFoundException() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        com.google.api.server.spi.auth.common.User user = new com.google.api.server.spi.auth.common.User("Email");
        endpoint.getActivityTypes(user, defaultOrganization.getId());
    }

    // TEST: GetActivityType
    @Test(expected = ForbiddenException.class)
    public void testGetActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivityType(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newAdminCaller(55), defaultActivityType.getId());
    }

    @Test
    public void testGetActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        testActivityServiceFactory.replay();
        ActivityType result = endpoint.getActivityType(newAdminCaller(55), defaultActivityType.getId());
        assertEquals(defaultActivityType, result);
    }

    // TEST: UpdateActivityType
    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newCaller(1), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(null, null, null);
    }

    @Test
    public void testUpdateActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        final Capture<ActivityType> capture = newCapture();

        expect(activityService.updateActivityType(EasyMock.capture(capture))).andAnswer(new IAnswer<ActivityType>() {
            public ActivityType answer() throws Throwable {
                assertEquals(defaultActivityType.getId(), capture.getValue().getId());
                return capture.getValue();
            }
        });

        testActivityServiceFactory.replay();

        ActivityType result = endpoint.updateActivityType(newAdminCaller(55), defaultActivityType.getId(), defaultActivityType);
        assertEquals(result, defaultActivityType);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.updateActivityType(defaultActivityType)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newAdminCaller(55), defaultActivityType.getId(), defaultActivityType);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateActivityTypeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        activityService.updateActivityType(defaultActivityType);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newAdminCaller(55), defaultActivityType.getId(), defaultActivityType);
    }

    // TEST: AddActivityType
    @Test(expected = ForbiddenException.class)
    public void testAddActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        endpoint.addActivityType(newCaller(1), jasAddActivityTypeRequest);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        endpoint.addActivityType(null, jasAddActivityTypeRequest);
    }

    @Test(expected = NullPointerException.class)
    public void testAddActivityTypeNoActivityTypeRequestThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivityType(newAdminCaller(1), null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoOrganizationThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeNoNameThrowsBadRequestException() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name");
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        jasAddActivityTypeRequest.setOrganizationId(defaultOrganization.getId());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnGetOrganization() throws Exception {
        testActivityServiceFactory.replay();
        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();

        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        defaultActivityType.setName("Name");
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        jasAddActivityTypeRequest.setOrganizationId(defaultOrganization.getId());

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityTypeThrowsBadRequestException() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();

        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        defaultActivityType.setName("Name");
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        jasAddActivityTypeRequest.setOrganizationId(defaultOrganization.getId());
        activityService.addActivityType(defaultOrganization, jasAddActivityTypeRequest.getActivityType());
        expectLastCall().andThrow(new UniqueConstraintException(""));
        testActivityServiceFactory.replay();

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnAddId() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();

        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        defaultActivityType.setName("Name");
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        jasAddActivityTypeRequest.setOrganizationId(defaultOrganization.getId());
        expect(activityService.addActivityType(defaultOrganization, defaultActivityType)).andReturn(defaultActivityType.getId());
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityType() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();

        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        defaultActivityType.setName("Name");
        jasAddActivityTypeRequest.setActivityType(defaultActivityType);
        jasAddActivityTypeRequest.setOrganizationId(defaultOrganization.getId());
        expect(activityService.addActivityType(defaultOrganization, defaultActivityType)).andReturn(defaultActivityType.getId());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        testActivityServiceFactory.replay();

        ActivityType result = endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
        assertEquals(result, defaultActivityType);
    }

    // TEST: RemoveActivityType
    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), defaultActivityType.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveActivityTypeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(Arrays.asList(defaultActivity));
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), defaultActivityType.getId());
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(Collections.EMPTY_LIST);
        activityService.removeActivityType(defaultActivityType);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), defaultActivityType.getId());
    }

    // TEST: GetActivities
    @Test(expected = BadRequestException.class)
    public void testGetActivitiesThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, defaultOrganization.getId(), defaultActivityType.getId(), null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetActivityTypeThrowsIllegalArgumentException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new IllegalArgumentException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetOrganizationThrowsNotFoundException() throws Exception {
        testActivityServiceFactory.replay();
        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetOrganizationThrowsIllegalArgumentException() throws Exception {
        testActivityServiceFactory.replay();
        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new IllegalArgumentException());
        testOrganizationServiceFactory.replay();
        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetActivitiesByOrganizationThrowsIllegalArgumentException() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivities(defaultOrganization)).andThrow(new IllegalArgumentException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
    }

    @Test
    public void testGetActivitiesByActivityTypeNoFilter() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(defaultActivity);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesByOrganizationNoFilter() throws Exception {
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();

        List<Activity> activityList = new ArrayList<>();
        activityList.add(defaultActivity);
        expect(activityService.getActivities(defaultOrganization)).andReturn(activityList);
        testActivityServiceFactory.replay();

        List<Activity> result = endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterFromDateBefore() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        defaultActivity.setStart(new Date(5));
        activityList.add(defaultActivity);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), new Date(4), null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterFromDateAfter() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        defaultActivity.setStart(new Date(5));
        activityList.add(defaultActivity);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), new Date(7), null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesFilterToDateBefore() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        defaultActivity.setFinish(new Date(5));
        activityList.add(defaultActivity);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, new Date(4), null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesFilterToDateAfter() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        defaultActivity.setFinish(new Date(5));
        activityList.add(defaultActivity);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, new Date(7), null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, 1);
        assertEquals(1, result.size());
        assertEquals("One", result.get(0).getName());
    }

    @Test
    public void testGetActivitiesFilterWithOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 1, null);
        assertEquals(1, result.size());
        assertEquals("Two", result.get(0).getName());
    }

    @Test
    public void testGetActivitiesFilterWithNullLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNullOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithZeroLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, 0);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithZeroOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 0, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNegativeLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, -1);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNegativeOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, -1, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndNegativeLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 1, -4);
        assertEquals(1, result.size());
        assertEquals("Two", result.get(0).getName());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetActivitiesFilterWithNegativeOffsetAndLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, -4, 1);
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        activityList.add(new Activity(new ActivityType("Three")));
        activityList.add(new Activity(new ActivityType("Four")));
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 1, 2);
        assertEquals(2, result.size());
        assertEquals("Two", result.get(0).getName());
        assertEquals("Three", result.get(1).getName());
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndLimitReturnsEmpty() throws Exception {
        testOrganizationServiceFactory.replay();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        activityList.add(new Activity(new ActivityType("Three")));
        activityList.add(new Activity(new ActivityType("Four")));
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 5, 2);
        assertTrue(result.isEmpty());
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesNoKeyThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, null, null, null, null, null);
    }

    // TEST: GetActivitiesByIds
    @Test(expected = BadRequestException.class)
    public void testGetActivitiesByIdsWithNullIdsThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivitiesByIds(null, null, null, null, null, null, null);
    }

    @Test(expected = BadRequestException.class)
    public void testGetActivitiesByIdsWithBothIdsNotNullThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivitiesByIds(null, new Key[]{}, new Key[]{}, null, null, null, null);
    }

    @Test(expected = BadRequestException.class)
    public void testGetActivitiesByIdsEmptyArrayOfActivityTypeKeysThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivitiesByIds(null, new Key[]{}, null, null, null, null, null);
    }

    @Test(expected = BadRequestException.class)
    public void testGetActivitiesByIdsEmptyArrayOfOrganizationKeysThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivitiesByIds(null, null, new Key[]{}, null, null, null, null);
    }

    @Test
    public void testGetActivitiesByActivityTypes() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityType activityType1 = new ActivityType();
        ActivityType activityType2 = new ActivityType();
        Datastore.put(activityType1, activityType2);
        expect(activityService.getActivityType(activityType1.getId())).andReturn(activityType1);
        expect(activityService.getActivities(activityType1)).andReturn(Arrays.asList(new Activity()));
        expect(activityService.getActivityType(activityType2.getId())).andReturn(activityType2);
        expect(activityService.getActivities(activityType2)).andReturn(Arrays.asList(new Activity()));
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivitiesByIds(null, null, new Key[]{activityType1.getId(), activityType2.getId()}, null, null, null, null);
        assertEquals(2, result.size());
    }

    @Test
    public void testGetActivitiesByOrganizations() throws Exception {
        Organization organization1 = new Organization();
        Organization organization2 = new Organization();
        Datastore.put(organization1, organization2);
        expect(organizationService.getOrganization(organization1.getId())).andReturn(organization1);
        expect(activityService.getActivities(organization1)).andReturn(Arrays.asList(new Activity()));
        expect(organizationService.getOrganization(organization2.getId())).andReturn(organization2);
        expect(activityService.getActivities(organization2)).andReturn(Arrays.asList(new Activity()));
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivitiesByIds(null, new Key[]{organization1.getId(), organization2.getId()}, null, null, null, null, null);
        assertEquals(2, result.size());
    }

    // TEST: GetActivity
    @Test(expected = UnauthorizedException.class)
    public void testGetActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivity(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivity(newAdminCaller(55), defaultActivity.getId());
    }

    @Test
    public void testGetActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        testActivityServiceFactory.replay();
        assertEquals(defaultActivity, endpoint.getActivity(newAdminCaller(55), defaultActivity.getId()));
    }

    // TEST: UpdateActivity
    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(1), null, defaultActivity);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(null, null, defaultActivity);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateActivityNoActivityThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newAdminCaller(1), defaultActivity.getId(), null);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultActivity.setName("Test");
        expect(activityService.updateActivity(defaultActivity)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newAdminCaller(55), defaultActivity.getId(), defaultActivity);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultActivity.setName("Test");
        expect(activityService.updateActivity(defaultActivity)).andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newAdminCaller(55), defaultActivity.getId(), defaultActivity);
    }

    @Test
    public void testUpdateActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        Activity activity = new Activity();
        activity.setName("Test");
        final Key key = Datastore.allocateId(Activity.class);
        final Capture<Activity> capture = newCapture();

        expect(activityService.updateActivity(EasyMock.capture(capture))).andAnswer(new IAnswer<Activity>() {
            public Activity answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testActivityServiceFactory.replay();

        Activity result = endpoint.updateActivity(newAdminCaller(55), key, activity);
        assertEquals(result, activity);
    }

    @Test
    public void testUpdateActivityWithNoName() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityType activityType = new ActivityType("Test");
        Activity activity = new Activity();
        activity.getActivityTypeRef().setModel(activityType);
        final Key key = Datastore.allocateId(Activity.class);
        final Capture<Activity> capture = newCapture();

        expect(activityService.updateActivity(EasyMock.capture(capture))).andAnswer(new IAnswer<Activity>() {
            public Activity answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testActivityServiceFactory.replay();

        assertNull(activity.getName());
        Activity result = endpoint.updateActivity(newAdminCaller(55), key, activity);
        assertEquals(result, activity);
        assertEquals(activityType.getName(), activity.getName());
    }

    // TEST: AddActivity
    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityTypeRefKeyThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(newCaller(1), jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityTypeRefModelThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        defaultActivity.getActivityTypeRef().setModel(null);
        jasAddActivityRequest.setActivity(defaultActivity);
        endpoint.addActivity(newCaller(1), jasAddActivityRequest);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        endpoint.addActivity(newCaller(1), jasAddActivityRequest);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        endpoint.addActivity(null, jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityMissingActivityTypeThrowsNotFoundRuntimeException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityNoActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.addActivity(defaultActivityType, defaultActivity, null)).andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.addActivity(defaultActivityType, defaultActivity, null)).andReturn(Arrays.asList(defaultActivity.getId()));
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        assertEquals(defaultActivity, endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest).get(0));
    }

    @Test
    public void testAddActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.addActivity(defaultActivityType, defaultActivity, null)).andReturn(Arrays.asList(defaultActivity.getId()));
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        assertEquals(defaultActivity, endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest).get(0));
    }

    @Test
    public void testAddActivityWithNoName() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultActivityType.setName("TEST");
        assertNull(defaultActivity.getName());
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.addActivity(defaultActivityType, defaultActivity, null)).andReturn(Arrays.asList(defaultActivity.getId()));
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(defaultActivity);
        assertEquals(defaultActivity, endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest).get(0));
        assertEquals(defaultActivityType.getName(), defaultActivity.getName());
    }

    @Test
    public void testAddRepeatingActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        RepeatDetails repeatDetails = new RepeatDetails();
        Activity activity = new Activity(defaultActivityType);
        Activity activity1 = new Activity(defaultActivityType);
        Activity activity2 = new Activity(defaultActivityType);
        Key id1 = Datastore.allocateId(Activity.class);
        Key id2 = Datastore.allocateId(Activity.class);
        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
        expect(activityService.addActivity(defaultActivityType, activity, repeatDetails)).andReturn(Arrays.asList(id1, id2));
        expect(activityService.getActivity(id1)).andReturn(activity1);
        expect(activityService.getActivity(id2)).andReturn(activity2);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(2, result.size());
        assertEquals(activity1, result.get(0));
        assertEquals(activity2, result.get(1));
    }

    // TEST: RemoveActivity
    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityNoActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), defaultActivity.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveActivityWithSubscriptionsThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Arrays.asList(defaultSubscription));
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), defaultActivity.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveActivityNoActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Collections.EMPTY_LIST);
        expect(activityService.getActivityPackageActivities(defaultActivity)).andReturn(Arrays.asList(new ActivityPackageActivity()));
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), defaultActivity.getId());
    }

    @Test
    public void testRemoveActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Collections.EMPTY_LIST);
        expect(activityService.getActivityPackageActivities(defaultActivity)).andReturn(Collections.EMPTY_LIST);
        activityService.removeActivity(defaultActivity);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), defaultActivity.getId());
    }

    // TEST: AddSubscription
    @Test(expected = NotFoundException.class)
    public void testAddSubscriptionNoUserThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addSubscription(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddSubscriptionNotLoggedInThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        com.google.api.server.spi.auth.common.User user = new com.google.api.server.spi.auth.common.User("Test");
        endpoint.addSubscription(user, defaultUser.getId(), null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddSubscriptionThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testAddSubscriptionThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.subscribe(defaultUser, defaultActivity)).andThrow(new UniqueConstraintException(""));
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
    }

    @Test(expected = BadRequestException.class)
    public void testAddSubscriptionOverSubscribeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.subscribe(defaultUser, defaultActivity)).andThrow(new OperationException(""));
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
    }

    @Test
    public void testAddSubscriptionAsAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.subscribe(defaultUser, defaultActivity)).andReturn(defaultSubscription);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.addSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
        assertEquals(defaultSubscription, result);
    }

    @Test
    public void testAddSubscriptionAsSignedIn() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.subscribe(defaultUser, defaultActivity)).andReturn(defaultSubscription);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.addSubscription(newCaller(defaultUser.getId().getId()), defaultUser.getId(), defaultActivity.getId());
        assertEquals(defaultSubscription, result);
    }

    // TEST: GetSubscription
    @Test(expected = UnauthorizedException.class)
    public void testGetSubscriptionNotLoggedInThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        com.google.api.server.spi.auth.common.User user = new com.google.api.server.spi.auth.common.User("Test");
        endpoint.getSubscription(user, defaultUser.getId(), null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionNoUserThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getSubscription(null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionNoResultThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Collections.EMPTY_LIST);
        testActivityServiceFactory.replay();
        endpoint.getSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
    }

    @Test
    public void testGetSubscriptionAsAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultSubscription.getUserRef().setKey(defaultUser.getId());
        defaultSubscription.getUserRef().setModel(defaultUser);
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Arrays.asList(defaultSubscription));
        testActivityServiceFactory.replay();
        Subscription result = endpoint.getSubscription(newAdminCaller(1), defaultUser.getId(), defaultActivity.getId());
        assertEquals(defaultSubscription, result);
    }

    @Test
    public void testGetSubscriptionAsSignedIn() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultSubscription.getUserRef().setKey(defaultUser.getId());
        defaultSubscription.getUserRef().setModel(defaultUser);
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(Arrays.asList(defaultSubscription));
        testActivityServiceFactory.replay();
        Subscription result = endpoint.getSubscription(newCaller(defaultUser.getId().getId()), defaultUser.getId(), defaultActivity.getId());
        assertEquals(defaultSubscription, result);
    }

    // TEST: GetSubscriptions
    @Test(expected = UnauthorizedException.class)
    public void testGetSubscriptionsNoUserThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getSubscriptions(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetSubscriptionsNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getSubscriptions(newCaller(1), null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionsActivityNotFoundThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getSubscriptions(newAdminCaller(1), defaultActivity.getId());
    }

    @Test
    public void testGetSubscriptions() throws Exception {
        testOrganizationServiceFactory.replay();
        defaultSubscription.getUserRef().setKey(defaultUser.getId());
        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(defaultSubscription);
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        expect(activityService.getSubscriptions(defaultActivity)).andReturn(subscriptionList);
        testActivityServiceFactory.replay();
        List<Subscription> result = endpoint.getSubscriptions(newAdminCaller(defaultUser.getId().getId()), defaultActivity.getId());
        assertEquals(1, result.size());
    }

    // TEST: CancelSubscription
    @Test(expected = UnauthorizedException.class)
    public void testCancelSubscriptionsNoUserThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(null, null);
    }

    @Test(expected = ForbiddenException.class)
    public void testCancelSubscriptionsNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(newCaller(1), null);
    }

    @Test(expected = NotFoundException.class)
    public void testCancelSubscriptionThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        activityService.cancelSubscription(defaultSubscription.getId());
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(newAdminCaller(1), defaultSubscription.getId());
    }

    @Test
    public void testCancelSubscription() throws Exception {
        testOrganizationServiceFactory.replay();
        activityService.cancelSubscription(defaultSubscription.getId());
        expectLastCall();
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(newAdminCaller(1), defaultSubscription.getId());
    }

    // TEST: GetActivityPackage
    @Test
    public void testGetActivityPackages() throws Exception {
        ArrayList<ActivityPackage> resp = Lists.newArrayList();
        resp.add(defaultActivityPackage);
        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackages(defaultOrganization)).andReturn(resp);
        testActivityServiceFactory.replay();
        List<ActivityPackage> activityPackages = endpoint.getActivityPackages(newAdminCaller(1), defaultOrganization.getId());
        assertEquals(resp, activityPackages);
    }

    @Test
    public void testGetActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackage(defaultActivityPackage.getId())).andReturn(defaultActivityPackage);
        testActivityServiceFactory.replay();
        ActivityPackage activityPackage = endpoint.getActivityPackage(newAdminCaller(1), defaultActivityPackage.getId());
        assertEquals(defaultActivityPackage, activityPackage);
    }

    // TEST: AddActivityPackage
    @Test
    public void testAddActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        JasActivityPackageRequest request = new JasActivityPackageRequest();
        defaultActivityPackage.getOrganizationRef().setKey(defaultOrganization.getId());
        request.setActivityPackage(defaultActivityPackage);

        request.getActivities().add(new Activity());

        Key apId = Datastore.allocateId(ActivityPackage.class);

        expect(activityService.addActivityPackage(defaultActivityPackage, request.getActivities())).andReturn(apId);
        expect(activityService.getActivityPackage(apId)).andReturn(defaultActivityPackage);

        testActivityServiceFactory.replay();

        ActivityPackage fetched = endpoint.addActivityPackage(newAdminCaller(1), request);
        assertEquals(defaultActivityPackage, fetched);
    }

    // TEST: UpdateActivityPackage
    @Test
    public void testUpdateActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        JasActivityPackageRequest request = new JasActivityPackageRequest();
        request.setActivityPackage(defaultActivityPackage);
        request.setActivities(new ArrayList<Activity>());

        expect(activityService.updateActivityPackage(defaultActivityPackage, request.getActivities())).andReturn(defaultActivityPackage);
        testActivityServiceFactory.replay();

        ActivityPackage fetched = endpoint.updateActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), request);
        assertEquals(defaultActivityPackage, fetched);
    }

    // TEST: AddActivityToActivityPackage
    @Test
    public void testAddActivityToActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackage(defaultActivityPackage.getId())).andReturn(defaultActivityPackage);
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        activityService.addActivityToActivityPackage(defaultActivityPackage, defaultActivity);
        EasyMock.expectLastCall();
        testActivityServiceFactory.replay();

        endpoint.addActivityToActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), defaultActivity.getId());
    }

    // TEST: RemoveActivityFromActivityPackage
    @Test(expected = NotFoundException.class)
    public void testRemoveActivityFromActivityPackageNoActivityPackageIdThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), null, defaultActivity.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityFromActivityPackageNoActivityIdThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveActivityFromActivityPackageThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(null, defaultActivityPackage.getId(), defaultActivity.getId());
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityFromActivityPackageThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(newCaller(1), defaultActivityPackage.getId(), defaultActivity.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityFromActivityPackageActivityPackageNotFoundThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackage(defaultActivityPackage.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), defaultActivity.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityFromActivityPackageActivityNotFoundThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackage(defaultActivityPackage.getId())).andReturn(defaultActivityPackage);
        expect(activityService.getActivity(defaultActivity.getId())).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), defaultActivity.getId());
    }

    @Test
    public void testRemoveActivityFromActivityPackageAsAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        expect(activityService.getActivityPackage(defaultActivityPackage.getId())).andReturn(defaultActivityPackage);
        expect(activityService.getActivity(defaultActivity.getId())).andReturn(defaultActivity);
        activityService.removeActivityFromActivityPackage(defaultActivityPackage, defaultActivity);
        EasyMock.expectLastCall();
        testActivityServiceFactory.replay();

        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), defaultActivityPackage.getId(), defaultActivity.getId());
    }
}
