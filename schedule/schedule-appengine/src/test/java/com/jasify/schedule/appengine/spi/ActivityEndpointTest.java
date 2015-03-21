package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.UserLogin;
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

import java.util.*;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
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
        endpoint.getActivityType(newCaller(1, false), null);
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
        assertEquals(activityType, endpoint.getActivityType(newCaller(55, true), key));
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        service.getActivityType(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newCaller(55, true), key);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newCaller(1, false), null, null);
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

        ActivityType result = endpoint.updateActivityType(newCaller(55, true), key, activityType);
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
        endpoint.updateActivityType(newCaller(55, true), key, activityType);
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
        endpoint.updateActivityType(newCaller(55, true), key, activityType);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivityType(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityTypeNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivityType(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddActivityTypeNoActivityTypeRequestThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivityType(newCaller(1, true), null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoActivityTypeThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        endpoint.addActivityType(newCaller(1, true), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoOrganizationThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        endpoint.addActivityType(newCaller(1, true), jasAddActivityTypeRequest);
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

        endpoint.addActivityType(newCaller(55, true), jasAddActivityTypeRequest);
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

        endpoint.addActivityType(newCaller(55, true), jasAddActivityTypeRequest);
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

        endpoint.addActivityType(newCaller(55, true), jasAddActivityTypeRequest);
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

        ActivityType result = endpoint.addActivityType(newCaller(55, true), jasAddActivityTypeRequest);
        assertEquals(result, activityType);
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityTypeNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(1, false), null);
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
        endpoint.removeActivityType(newCaller(55, true), key);
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(55, true), key);
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
        endpoint.getActivity(newCaller(55, true), key);
    }

    @Test
    public void testGetActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        Activity activity = new Activity();
        expect(service.getActivity(key)).andReturn(activity);
        testActivityServiceFactory.replay();
        assertEquals(activity, endpoint.getActivity(newCaller(55, true), key));
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(1, false), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.updateActivity(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateActivityNoActivityThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        Key key = Datastore.allocateId(Activity.class);
        endpoint.updateActivity(newCaller(1, true), key, null);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        Key key = Datastore.allocateId(Activity.class);
        service.updateActivity(activity);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(55, true), key, activity);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateActivityThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        Key key = Datastore.allocateId(Activity.class);
        service.updateActivity(activity);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(55, true), key, activity);
    }

    @Test
    public void testUpdateActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Activity activity = new Activity();
        final Key key = Datastore.allocateId(Activity.class);
        final Capture<Activity> capture = newCapture();

        expect(service.updateActivity(EasyMock.capture(capture))).andAnswer(new IAnswer<Activity>() {
            public Activity answer() throws Throwable {
                assertEquals(key, capture.getValue().getId());
                return capture.getValue();
            }
        });

        testActivityServiceFactory.replay();

        Activity result = endpoint.updateActivity(newCaller(55, true), key, activity);
        assertEquals(result, activity);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivity(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityNoUserThrowsUnauthorizedException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addActivity(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddActivityNoActivityThrowsNullPointerException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityTypeRefKeyThrowsNotFoundException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
    }

    @Test(expected = EntityNotFoundRuntimeException.class)
    public void testAddActivityNoActivityTypeRefModelThrowsEntityNotFoundRuntimeException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        Activity activity = new Activity();
        activity.getActivityTypeRef().setKey(Datastore.allocateId(ActivityType.class));
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
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
        endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
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
        endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
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
        assertEquals(activity, endpoint.addActivity(newCaller(1, true), jasAddActivityRequest).get(0));
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
        List<Activity> result = endpoint.addActivity(newCaller(1, true), jasAddActivityRequest);
        assertEquals(2, result.size());
        assertEquals(activity1, result.get(0));
        assertEquals(activity2, result.get(1));
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityNotAdminThrowsForbiddenException() throws Exception {
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1, false), null);
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
        endpoint.removeActivity(newCaller(1, true), key);
    }

    @Test
    public void testRemoveActivity() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1, true), key);
    }

    @Test(expected = NotFoundException.class)
    public void testAddSubscriptionNoUserThrowsNotFoundException() throws Exception{
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.addSubscription(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddSubscriptionNotLoggedInThrowsUnauthorizedException() throws Exception{
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
        endpoint.addSubscription(newCaller(1, true), userId, activityId);
    }

    @Test(expected = BadRequestException.class)
    public void testAddSubscriptionThrowsBadRequestException() throws Exception {
        testOrganizationServiceFactory.replay();
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key userId = Datastore.allocateId(UserLogin.class);
        Key activityId = Datastore.allocateId(Activity.class);
        expect(service.subscribe(userId, activityId)).andThrow(new UniqueConstraintException(""));
        testActivityServiceFactory.replay();
        endpoint.addSubscription(newCaller(1, true), userId, activityId);
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
        Subscription result = endpoint.addSubscription(newCaller(1, true), userId, activityId);
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
        Subscription result = endpoint.addSubscription(newCaller(userId.getId(), false), userId, activityId);
        assertEquals(subscription, result);
    }


    @Test(expected = NotFoundException.class)
    public void testGetSubscriptionNoUserThrowsNotFoundException() throws Exception{
        testOrganizationServiceFactory.replay();
        testActivityServiceFactory.replay();
        endpoint.getSubscription(null, null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetSubscriptionNotLoggedInThrowsUnauthorizedException() throws Exception{
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
        endpoint.getSubscription(newCaller(1, true), userId, activityId);
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
        endpoint.getSubscription(newCaller(1, true), userId, activityId);
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
        Subscription result = endpoint.getSubscription(newCaller(1, true), userId, activityId);
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
        Subscription result = endpoint.getSubscription(newCaller(userId.getId(), false), userId, activityId);
        assertEquals(subscription, result);
    }
}
