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
import com.jasify.schedule.appengine.spi.dm.JasListQueryActivitiesRequest;
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

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.datastore;
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

    private ActivityEndpoint endpoint;

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
        endpoint = new ActivityEndpoint();
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


    // TEST: GetActivityType

//
//    // TEST: UpdateActivityType
//
//
//    // TEST: AddActivityType
//
//    // TEST: GetActivities
//    @Test(expected = BadRequestException.class)
//    public void testGetActivitiesThrowsBadRequestException() throws Exception {
//        testOrganizationServiceFactory.replay();
//        testActivityServiceFactory.replay();
//        endpoint.getActivities(null, defaultOrganization.getId(), defaultActivityType.getId(), null, null, null, null);
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testGetActivitiesGetActivityTypeThrowsNotFoundException() throws Exception {
//        testOrganizationServiceFactory.replay();
//        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new EntityNotFoundException());
//        testActivityServiceFactory.replay();
//        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testGetActivitiesGetActivityTypeThrowsIllegalArgumentException() throws Exception {
//        testOrganizationServiceFactory.replay();
//        expect(activityService.getActivityType(defaultActivityType.getId())).andThrow(new IllegalArgumentException());
//        testActivityServiceFactory.replay();
//        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
//    }
//
//    @Test(expected = NotFoundException.class)
//    public void testGetActivitiesGetOrganizationThrowsNotFoundException() throws Exception {
//        testActivityServiceFactory.replay();
//        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new EntityNotFoundException());
//        testOrganizationServiceFactory.replay();
//        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testGetActivitiesGetOrganizationThrowsIllegalArgumentException() throws Exception {
//        testActivityServiceFactory.replay();
//        expect(organizationService.getOrganization(defaultOrganization.getId())).andThrow(new IllegalArgumentException());
//        testOrganizationServiceFactory.replay();
//        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testGetActivitiesGetActivitiesByOrganizationThrowsIllegalArgumentException() throws Exception {
//        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
//        testOrganizationServiceFactory.replay();
//        expect(activityService.getActivities(defaultOrganization)).andThrow(new IllegalArgumentException());
//        testActivityServiceFactory.replay();
//        endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
//    }
//
//    @Test
//    public void testGetActivitiesByActivityTypeNoFilter() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(defaultActivity);
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesByOrganizationNoFilter() throws Exception {
//        expect(organizationService.getOrganization(defaultOrganization.getId())).andReturn(defaultOrganization);
//        testOrganizationServiceFactory.replay();
//
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(defaultActivity);
//        expect(activityService.getActivities(defaultOrganization)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//
//        List<Activity> result = endpoint.getActivities(null, defaultOrganization.getId(), null, null, null, null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterFromDateBefore() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        defaultActivity.setStart(new Date(5));
//        activityList.add(defaultActivity);
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), new Date(4), null, null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterFromDateAfter() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        defaultActivity.setStart(new Date(5));
//        activityList.add(defaultActivity);
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), new Date(7), null, null, null);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void testGetActivitiesFilterToDateBefore() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        defaultActivity.setFinish(new Date(5));
//        activityList.add(defaultActivity);
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, new Date(4), null, null);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void testGetActivitiesFilterToDateAfter() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        defaultActivity.setFinish(new Date(5));
//        activityList.add(defaultActivity);
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, new Date(7), null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity(new ActivityType("One")));
//        activityList.add(new Activity(new ActivityType("Two")));
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, 1);
//        assertEquals(1, result.size());
//        assertEquals("One", result.get(0).getName());
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithOffset() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity(new ActivityType("One")));
//        activityList.add(new Activity(new ActivityType("Two")));
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 1, null);
//        assertEquals(1, result.size());
//        assertEquals("Two", result.get(0).getName());
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithNullLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithNullOffset() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithZeroLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, 0);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithZeroOffset() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 0, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithNegativeLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, null, -1);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithNegativeOffset() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, -1, null);
//        assertEquals(activityList, result);
//    }
//
//    @Test
//    public void testGetActivitiesFilterWithOffsetAndNegativeLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity(new ActivityType("One")));
//        activityList.add(new Activity(new ActivityType("Two")));
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        List<Activity> result = endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, 1, -4);
//        assertEquals(1, result.size());
//        assertEquals("Two", result.get(0).getName());
//    }
//
//    @Test(expected = IndexOutOfBoundsException.class)
//    public void testGetActivitiesFilterWithNegativeOffsetAndLimit() throws Exception {
//        testOrganizationServiceFactory.replay();
//        List<Activity> activityList = new ArrayList<>();
//        activityList.add(new Activity());
//        activityList.add(new Activity());
//        expect(activityService.getActivityType(defaultActivityType.getId())).andReturn(defaultActivityType);
//        expect(activityService.getActivities(defaultActivityType)).andReturn(activityList);
//        testActivityServiceFactory.replay();
//        endpoint.getActivities(null, null, defaultActivityType.getId(), null, null, -4, 1);
//    }
//
//
//    // TEST: GetActivity
//
//    // TEST: UpdateActivity
//
//    // TEST: AddActivity
//
//    // TEST: RemoveActivity
//
//    // TEST: AddSubscription
//
//    // TEST: GetSubscription
//
//    // TEST: GetSubscriptions
//
//    // TEST: CancelSubscription

//
//    // TEST: GetActivityPackage
//
//    // TEST: AddActivityPackage

//
//    // TEST: UpdateActivityPackage

//    // TEST: AddActivityToActivityPackage

//
//    // TEST: RemoveActivityFromActivityPackage

}
