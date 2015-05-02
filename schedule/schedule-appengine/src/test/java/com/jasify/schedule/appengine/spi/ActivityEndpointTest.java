package com.jasify.schedule.appengine.spi;

import com.google.api.client.util.Lists;
import com.google.api.server.spi.auth.common.User;
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
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityPackageRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.*;

/**
 * @author wszarmach
 * @since 19/01/15.
 */
public class ActivityEndpointTest {

    private TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();

    private ActivityEndpoint endpoint = new ActivityEndpoint();

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        testActivityServiceFactory.setUp();
        testOrganizationServiceFactory.setUp();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testActivityServiceFactory.tearDown();
        testOrganizationServiceFactory.tearDown();
    }

    @Test
    public void testGetActivityTypes() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        List<ActivityType> activityTypeList = new ArrayList<>();
        activityTypeList.add(new ActivityType());
        expect(service.getActivityTypes(key)).andReturn(activityTypeList);
        testActivityServiceFactory.replay();
        User user = new User("Email");
        assertEquals(activityTypeList, endpoint.getActivityTypes(user, key));
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypesThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        service.getActivityTypes(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        User user = new User("Email");
        endpoint.getActivityTypes(user, key);
    }

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

    @Test
    public void testGetActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        ActivityType activityType = new ActivityType();
        expect(service.getActivityType(key)).andReturn(activityType);
        testActivityServiceFactory.replay();
        assertEquals(activityType, endpoint.getActivityType(newAdminCaller(55), key));
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        service.getActivityType(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newAdminCaller(55), key);
    }

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
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        final Key key = Datastore.allocateId(ActivityType.class);
        final Capture<ActivityType> capture = newCapture();

        expect(service.updateActivityType(EasyMock.capture(capture))).andAnswer(new IAnswer<ActivityType>() {
            public ActivityType answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testActivityServiceFactory.replay();

        ActivityType result = endpoint.updateActivityType(newAdminCaller(55), key, activityType);
        assertEquals(result, activityType);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        ActivityType activityType = new ActivityType();
        service.updateActivityType(activityType);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newAdminCaller(55), key, activityType);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateActivityTypeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        ActivityType activityType = new ActivityType();
        service.updateActivityType(activityType);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newAdminCaller(55), key, activityType);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        endpoint.addActivityType(newCaller(1), jasAddActivityTypeRequest);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
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
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnGetOrganization() throws Exception {
        testActivityServiceFactory.replay();
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Key organizationKey = Datastore.allocateId(Organization.class);
        organizationService.getOrganization(organizationKey);
        expectLastCall().andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();

        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        jasAddActivityTypeRequest.setOrganizationId(organizationKey);

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityTypeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.setUp();
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Key organizationKey = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        jasAddActivityTypeRequest.setOrganizationId(organizationKey);
        activityService.addActivityType(organization, jasAddActivityTypeRequest.getActivityType());
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnAddId() throws Exception {
        testOrganizationServiceFactory.setUp();
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Key organizationKey = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        ActivityType activityType = new ActivityType();
        jasAddActivityTypeRequest.setActivityType(activityType);
        jasAddActivityTypeRequest.setOrganizationId(organizationKey);
        expect(activityService.addActivityType(organization, activityType)).andReturn(organizationKey);
        expect(activityService.getActivityType(organizationKey)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();

        endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityType() throws Exception {
        testOrganizationServiceFactory.setUp();
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Key organizationKey = Datastore.allocateId(Organization.class);
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        ActivityType activityType = new ActivityType();
        jasAddActivityTypeRequest.setActivityType(activityType);
        jasAddActivityTypeRequest.setOrganizationId(organizationKey);
        expect(activityService.addActivityType(organization, activityType)).andReturn(organizationKey);
        expect(activityService.getActivityType(organizationKey)).andReturn(activityType);
        testActivityServiceFactory.replay();

        ActivityType result = endpoint.addActivityType(newAdminCaller(55), jasAddActivityTypeRequest);
        assertEquals(result, activityType);
    }

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
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), key);
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveActivityTypeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().andThrow(new OperationException(""));
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), key);
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newAdminCaller(55), key);
    }

    @Test(expected = BadRequestException.class)
    public void testGetActivitiesThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        Key organizationKey = Datastore.allocateId(Organization.class);
        endpoint.getActivities(null, organizationKey, activityTypeKey, null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        expect(service.getActivityType(activityTypeKey)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetActivityTypeThrowsIllegalArgumentException() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        expect(service.getActivityType(activityTypeKey)).andThrow(new IllegalArgumentException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetActivitiesByActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetOrganizationThrowsNotFoundException() throws Exception {
        testActivityServiceFactory.replay();
        Key organizationKey = Datastore.allocateId(Organization.class);
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        expect(service.getOrganization(organizationKey)).andThrow(new EntityNotFoundException());
        testOrganizationServiceFactory.replay();
        endpoint.getActivities(null, organizationKey, null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetOrganizationThrowsIllegalArgumentException() throws Exception {
        testActivityServiceFactory.replay();
        Key organizationKey = Datastore.allocateId(Organization.class);
        OrganizationService service = OrganizationServiceFactory.getOrganizationService();
        expect(service.getOrganization(organizationKey)).andThrow(new IllegalArgumentException());
        testOrganizationServiceFactory.replay();
        endpoint.getActivities(null, organizationKey, null, null, null, null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesGetActivitiesByOrganizationThrowsNotFoundException() throws Exception {
        Key organizationKey = Datastore.allocateId(Organization.class);
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivities(organization)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();

        endpoint.getActivities(null, organizationKey, null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivitiesGetActivitiesByOrganizationThrowsIllegalArgumentException() throws Exception {
        Key organizationKey = Datastore.allocateId(Organization.class);
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivities(organization)).andThrow(new IllegalArgumentException());
        testActivityServiceFactory.replay();

        endpoint.getActivities(null, organizationKey, null, null, null, null, null);
    }

    @Test
    public void testGetActivitiesByActivityTypeNoFilter() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesByOrganizationNoFilter() throws Exception {
        Key organizationKey = Datastore.allocateId(Organization.class);
        OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
        Organization organization = new Organization();
        expect(organizationService.getOrganization(organizationKey)).andReturn(organization);
        testOrganizationServiceFactory.replay();

        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        ActivityService activityService = ActivityServiceFactory.getActivityService();
        expect(activityService.getActivities(organization)).andReturn(activityList);
        testActivityServiceFactory.replay();

        List<Activity> result = endpoint.getActivities(null, organizationKey, null, null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterFromDateBefore() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        Activity activity = new Activity();
        activity.setStart(new Date(5));
        activityList.add(activity);
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, new Date(4), null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterFromDateAfter() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        Activity activity = new Activity();
        activity.setStart(new Date(5));
        activityList.add(activity);
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, new Date(7), null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesFilterToDateBefore() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        Activity activity = new Activity();
        activity.setFinish(new Date(5));
        activityList.add(activity);
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, new Date(4), null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesFilterToDateAfter() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        Activity activity = new Activity();
        activity.setFinish(new Date(5));
        activityList.add(activity);
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, new Date(7), null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, 1);
        assertEquals(1, result.size());
        assertEquals("One", result.get(0).getName());
    }

    @Test
    public void testGetActivitiesFilterWithOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, 1, null);
        assertEquals(1, result.size());
        assertEquals("Two", result.get(0).getName());
    }

    @Test
    public void testGetActivitiesFilterWithNullLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNullOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithZeroLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, 0);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithZeroOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, 0, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNegativeLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, null, -1);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithNegativeOffset() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, -1, null);
        assertEquals(activityList, result);
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndNegativeLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, 1, -4);
        assertEquals(1, result.size());
        assertEquals("Two", result.get(0).getName());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetActivitiesFilterWithNegativeOffsetAndLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity());
        activityList.add(new Activity());
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, activityTypeKey, null, null, -4, 1);
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndLimit() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        activityList.add(new Activity(new ActivityType("Three")));
        activityList.add(new Activity(new ActivityType("Four")));
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, 1, 2);
        assertEquals(2, result.size());
        assertEquals("Two", result.get(0).getName());
        assertEquals("Three", result.get(1).getName());
    }

    @Test
    public void testGetActivitiesFilterWithOffsetAndLimitReturnsEmpty() throws Exception {
        testOrganizationServiceFactory.replay();
        Key activityTypeKey = Datastore.allocateId(ActivityType.class);
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType();
        List<Activity> activityList = new ArrayList<>();
        activityList.add(new Activity(new ActivityType("One")));
        activityList.add(new Activity(new ActivityType("Two")));
        activityList.add(new Activity(new ActivityType("Three")));
        activityList.add(new Activity(new ActivityType("Four")));
        expect(service.getActivityType(activityTypeKey)).andReturn(activityType);
        expect(service.getActivities(activityType)).andReturn(activityList);
        testActivityServiceFactory.replay();
        List<Activity> result = endpoint.getActivities(null, null, activityTypeKey, null, null, 5, 2);
        assertTrue(result.isEmpty());

    }

    @Test(expected = NotFoundException.class)
    public void testGetActivitiesNoKeyThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivities(null, null, null, null, null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getActivity(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.getActivity(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivity(newAdminCaller(55), key);
    }

    @Test
    public void testGetActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        Activity activity = new Activity();
        expect(service.getActivity(key)).andReturn(activity);
        testActivityServiceFactory.replay();
        assertEquals(activity, endpoint.getActivity(newAdminCaller(55), key));
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(1), null, new Activity());
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(null, null, new Activity());
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateActivityNoActivityThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        Key key = Datastore.allocateId(Activity.class);
        endpoint.updateActivity(newAdminCaller(1), key, null);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        activity.setName("Test");
        Key key = Datastore.allocateId(Activity.class);
        service.updateActivity(activity);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newAdminCaller(55), key, activity);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        activity.setName("Test");
        Key key = Datastore.allocateId(Activity.class);
        service.updateActivity(activity);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newAdminCaller(55), key, activity);
    }

    @Test
    public void testUpdateActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        activity.setName("Test");
        final Key key = Datastore.allocateId(Activity.class);
        final Capture<Activity> capture = newCapture();

        expect(service.updateActivity(EasyMock.capture(capture))).andAnswer(new IAnswer<Activity>() {
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
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("Test");
        Activity activity = new Activity();
        activity.getActivityTypeRef().setModel(activityType);
        final Key key = Datastore.allocateId(Activity.class);
        final Capture<Activity> capture = newCapture();

        expect(service.updateActivity(EasyMock.capture(capture))).andAnswer(new IAnswer<Activity>() {
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

    @Test(expected = ForbiddenException.class)
    public void testAddActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(newCaller(1), jasAddActivityRequest);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(null, jasAddActivityRequest);
    }

    @Test(expected = NullPointerException.class)
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
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = EntityNotFoundRuntimeException.class)
    public void testAddActivityNoActivityTypeRefModelThrowsEntityNotFoundRuntimeException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        Activity activity = new Activity();
        activity.getActivityTypeRef().setKey(Datastore.allocateId(ActivityType.class));
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityNoActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        service.addActivity(activity, null);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        service.addActivity(activity, null);
        expectLastCall().andThrow(new EntityNotFoundException(""));
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        Key id = Datastore.allocateId(Activity.class);
        expect(service.addActivity(activity, null)).andReturn(Arrays.asList(id));
        expect(service.getActivity(id)).andReturn(activity);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        assertEquals(activity, endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest).get(0));
    }

    @Test
    public void testAddActivityWithNoName() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity();
        activity.getActivityTypeRef().setModel(activityType);
        Key id = Datastore.allocateId(Activity.class);
        assertNull(activity.getName());
        expect(service.addActivity(activity, null)).andReturn(Arrays.asList(id));
        expect(service.getActivity(id)).andReturn(activity);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        assertEquals(activity, endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest).get(0));
        assertEquals(activityType.getName(), activity.getName());
    }

    @Test
    public void testAddRepeatingActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        RepeatDetails repeatDetails = new RepeatDetails();
        Activity activity = new Activity(activityType);
        Activity activity1 = new Activity(activityType);
        Activity activity2 = new Activity(activityType);
        Key id1 = Datastore.allocateId(Activity.class);
        Key id2 = Datastore.allocateId(Activity.class);
        expect(service.addActivity(activity, repeatDetails)).andReturn(Arrays.asList(id1, id2));
        expect(service.getActivity(id1)).andReturn(activity1);
        expect(service.getActivity(id2)).andReturn(activity2);
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
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().andThrow(new EntityNotFoundException(""));
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), key);
    }

    @Test(expected = BadRequestException.class)
    public void testRemoveActivityNoActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().andThrow(new OperationException(""));
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), key);
    }

    @Test
    public void testRemoveActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newAdminCaller(1), key);
    }

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
        User user = new User("Test");
        Key key = Datastore.allocateId(UserLogin.class);
        endpoint.addSubscription(user, key, null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddSubscriptionThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.subscribe(userId, activityId)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), userId, activityId);
    }

    @Test(expected = BadRequestException.class)
    public void testAddSubscriptionThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.subscribe(userId, activityId)).andThrow(new UniqueConstraintException(""));
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), userId, activityId);
    }

    @Test(expected = BadRequestException.class)
    public void testAddSubscriptionOverSubscribeThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.subscribe(userId, activityId)).andThrow(new OperationException(""));
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newAdminCaller(1), userId, activityId);
    }

    @Test
    public void testAddSubscriptionAsAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        expect(service.subscribe(userId, activityId)).andReturn(subscription);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.addSubscription(newAdminCaller(1), userId, activityId);
        assertEquals(subscription, result);
    }

    @Test
    public void testAddSubscriptionAsSignedIn() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        expect(service.subscribe(userId, activityId)).andReturn(subscription);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.addSubscription(newCaller(userId.getId()), userId, activityId);
        assertEquals(subscription, result);
    }


    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionNoUserThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getSubscription(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetSubscriptionNotLoggedInThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        User user = new User("Test");
        Key key = Datastore.allocateId(UserLogin.class);
        endpoint.getSubscription(user, key, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.getSubscriptions(activityId)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getSubscription(newAdminCaller(1), userId, activityId);
    }

    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionNoResultThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        expect(service.getSubscriptions(activityId)).andReturn(subscriptionList);
        testActivityServiceFactory.replay();
        endpoint.getSubscription(newAdminCaller(1), userId, activityId);
    }

    @Test
    public void testGetSubscriptionAsAdmin() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(com.jasify.schedule.appengine.model.users.User.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        subscription.getUserRef().setKey(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        expect(service.getSubscriptions(activityId)).andReturn(subscriptionList);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.getSubscription(newAdminCaller(1), userId, activityId);
        assertEquals(subscription, result);
    }

    @Test
    public void testGetSubscriptionAsSignedIn() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(com.jasify.schedule.appengine.model.users.User.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        subscription.getUserRef().setKey(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        expect(service.getSubscriptions(activityId)).andReturn(subscriptionList);
        testActivityServiceFactory.replay();
        Subscription result = endpoint.getSubscription(newCaller(userId.getId()), userId, activityId);
        assertEquals(subscription, result);
    }

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
    public void testGetSubscriptionsThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.getSubscriptions(activityId)).andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getSubscriptions(newAdminCaller(1), activityId);
    }

    @Test
    public void testGetSubscriptions() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(com.jasify.schedule.appengine.model.users.User.class);
        Key activityId = Datastore.allocateId(Activity.class);
        Subscription subscription = new Subscription();
        subscription.getUserRef().setKey(userId);
        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription);
        expect(service.getSubscriptions(activityId)).andReturn(subscriptionList);
        testActivityServiceFactory.replay();
        List<Subscription> result = endpoint.getSubscriptions(newAdminCaller(userId.getId()), activityId);
        assertEquals(1, result.size());
    }

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
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key subscriptionId = Datastore.allocateId(Subscription.class);
        service.cancelSubscription(subscriptionId);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(newAdminCaller(1), subscriptionId);
    }

    @Test
    public void testCancelSubscription() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key subscriptionId = Datastore.allocateId(Subscription.class);
        service.cancelSubscription(subscriptionId);
        expectLastCall();
        testActivityServiceFactory.replay();
        endpoint.cancelSubscription(newAdminCaller(1), subscriptionId);
    }

    @Test
    public void testGetActivityPackages() throws Exception {
        testOrganizationServiceFactory.replay();
        Key organizationId = Datastore.allocateId(Organization.class);
        ArrayList<ActivityPackage> resp = Lists.newArrayList();
        resp.add(new ActivityPackage());
        EasyMock.expect(ActivityServiceFactory.getActivityService().getActivityPackages(organizationId))
                .andReturn(resp);
        testActivityServiceFactory.replay();
        List<ActivityPackage> activityPackages = endpoint.getActivityPackages(newAdminCaller(1), organizationId);
        assertEquals(resp, activityPackages);
    }

    @Test
    public void testGetActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        Key apId = Datastore.allocateId(ActivityPackage.class);
        ActivityPackage resp = new ActivityPackage();
        EasyMock.expect(ActivityServiceFactory.getActivityService().getActivityPackage(apId))
                .andReturn(resp);
        testActivityServiceFactory.replay();
        ActivityPackage activityPackage = endpoint.getActivityPackage(newAdminCaller(1), apId);
        assertEquals(resp, activityPackage);
    }

    @Test
    public void testAddActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        JasAddActivityPackageRequest request = new JasAddActivityPackageRequest();
        ActivityPackage activityPackage = new ActivityPackage();

        Key orgId = Datastore.allocateId(Organization.class);
        activityPackage.getOrganizationRef().setKey(orgId);
        request.setActivityPackage(activityPackage);

        request.getActivities().add(new Activity());

        Key apId = Datastore.allocateId(ActivityPackage.class);

        ActivityService mock = ActivityServiceFactory.getActivityService();
        EasyMock.expect(mock.addActivityPackage(activityPackage, request.getActivities()))
                .andReturn(apId);

        EasyMock.expect(mock.getActivityPackage(apId)).andReturn(activityPackage);

        testActivityServiceFactory.replay();

        ActivityPackage fetched = endpoint.addActivityPackage(newAdminCaller(1), request);
        assertEquals(activityPackage, fetched);
    }

    @Test
    public void testUpdateActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityPackage activityPackage = new ActivityPackage();
        Key id = Datastore.allocateId(ActivityPackage.class);
        activityPackage.setId(id);
        EasyMock.expect(ActivityServiceFactory.getActivityService().updateActivityPackage(activityPackage)).andReturn(activityPackage);
        testActivityServiceFactory.replay();

        ActivityPackage fetched = endpoint.updateActivityPackage(newAdminCaller(1), id, activityPackage);
        assertEquals(activityPackage, fetched);
    }

    @Test
    public void testAddActivityToActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        Key apId = Datastore.allocateId(ActivityPackage.class);
        Key aId = Datastore.allocateId(Activity.class);
        ActivityServiceFactory.getActivityService().addActivityToActivityPackage(apId, aId);
        EasyMock.expectLastCall();
        testActivityServiceFactory.replay();

        endpoint.addActivityToActivityPackage(newAdminCaller(1), apId, aId);
    }

    @Test
    public void testRemoveActivityToActivityPackage() throws Exception {
        testOrganizationServiceFactory.replay();
        Key apId = Datastore.allocateId(ActivityPackage.class);
        Key aId = Datastore.allocateId(Activity.class);
        ActivityServiceFactory.getActivityService().removeActivityFromActivityPackage(apId, aId);
        EasyMock.expectLastCall();
        testActivityServiceFactory.replay();

        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), apId, aId);
    }
}
