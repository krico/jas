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
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationService;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.common.TestOrganizationServiceFactory;
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
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static org.easymock.EasyMock.*;

/**
 * Created by wszarmach on 19/01/15.
 */
public class ActivityEndpointTest {

    private TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
    private TestOrganizationServiceFactory testOrganizationServiceFactory = new TestOrganizationServiceFactory();

    private ActivityEndpoint endpoint = new ActivityEndpoint();

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        testActivityServiceFactory.setUp();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        testActivityServiceFactory.tearDown();
    }

    @Test
    public void testGetActivityTypes() throws Exception {
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
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        service.getActivityTypes(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        User user = new User("Email");
        endpoint.getActivityTypes(user, key);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetActivityTypeNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetActivityTypeNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.getActivityType(null, null);
    }

    @Test
    public void testGetActivityType() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        ActivityType activityType = new ActivityType();
        expect(service.getActivityType(key)).andReturn(activityType);
        testActivityServiceFactory.replay();
        assertEquals(activityType, endpoint.getActivityType(newCaller(55, true), key));
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityTypeThrowsNotFoundException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Organization.class);
        service.getActivityType(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivityType(newCaller(55, true), key);
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityTypeNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newCaller(1, false), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityTypeNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(null, null, null);
    }

    @Test
    public void testUpdateActivityType() throws Exception {
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
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        ActivityType activityType = new ActivityType();
        service.updateActivityType(activityType);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.updateActivityType(newCaller(55, true), key, activityType);
    }

    @Test(expected = ForbiddenException.class)
    public void testAddActivityTypeNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.addActivityType(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityTypeNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.addActivityType(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoActivityType() throws Exception {
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        endpoint.addActivityType(newCaller(1, true), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeNoOrganization() throws Exception {
        testActivityServiceFactory.replay();
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        endpoint.addActivityType(newCaller(1, true), jasAddActivityTypeRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnGetOrganization() throws Exception {
        try {
            testActivityServiceFactory.replay();
            testOrganizationServiceFactory.setUp();
            OrganizationService organizationService = OrganizationServiceFactory.getOrganizationService();
            Key organizationKey = Datastore.allocateId(Organization.class);
            organizationService.getOrganization(organizationKey);
            expectLastCall().andThrow(new EntityNotFoundException());
            testOrganizationServiceFactory.replay();

            JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
            jasAddActivityTypeRequest.setActivityType(new ActivityType());
            jasAddActivityTypeRequest.setOrganizationId(organizationKey);

            endpoint.addActivityType(newCaller(55, true), jasAddActivityTypeRequest);
        } finally {
            testOrganizationServiceFactory.tearDown(false);
        }
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityTypeThrowsBadRequestException() throws Exception {
        try {
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
        } finally {
            testOrganizationServiceFactory.tearDown(false);
        }
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityTypeThrowsNotFoundExceptionOnAddId() throws Exception {
        try {
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
        } finally {
            testOrganizationServiceFactory.tearDown(false);
        }
    }

    @Test
    public void testAddActivityType() throws Exception {
        try {
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
        } finally {
            testOrganizationServiceFactory.tearDown(false);
        }
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityTypeNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveActivityTypeNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityTypeThrowsNotFoundException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(55, true), key);
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(ActivityType.class);
        service.removeActivityType(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivityType(newCaller(55, true), key);
    }

    // TODO getActivities

    @Test(expected = ForbiddenException.class)
    public void testGetActivityNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.getActivity(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testGetActivityNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.getActivity(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testGetActivityThrowsNotFoundException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.getActivity(key);
        expectLastCall().andThrow(new EntityNotFoundException());
        testActivityServiceFactory.replay();
        endpoint.getActivity(newCaller(55, true), key);
    }

    @Test
    public void testGetActivity() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        Activity activity = new Activity();
        expect(service.getActivity(key)).andReturn(activity);
        testActivityServiceFactory.replay();
        assertEquals(activity, endpoint.getActivity(newCaller(55, true), key));
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateActivityNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.updateActivity(newCaller(1, false), null, null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testUpdateActivityNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.updateActivity(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateActivityNoActivity() throws Exception {
        testActivityServiceFactory.replay();
        Key key = Datastore.allocateId(Activity.class);
        endpoint.updateActivity(newCaller(1, true), key, null);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateActivityThrowsNotFoundException() throws Exception {
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
    public void testAddActivityNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.addActivity(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testAddActivityNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.addActivity(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddActivityNoActivity() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.addActivity(newCaller(1, true), null);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityTypeRefKey() throws Exception {
        testActivityServiceFactory.replay();
        Activity activity = new Activity();
        endpoint.addActivity(newCaller(1, true), activity);
    }

    @Test(expected = EntityNotFoundRuntimeException.class)
    public void testAddActivityNoActivityTypeRefModel() throws Exception {
        testActivityServiceFactory.replay();
        Activity activity = new Activity();
        activity.getActivityTypeRef().setKey(Datastore.allocateId(ActivityType.class));
        endpoint.addActivity(newCaller(1, true), activity);
    }

    @Test(expected = BadRequestException.class)
    public void testAddActivityNoActivityThrowsBadRequestException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        service.addActivity(activity);
        expectLastCall().andThrow(new FieldValueException(""));
        testActivityServiceFactory.replay();
        endpoint.addActivity(newCaller(1, true), activity);
    }

    @Test(expected = NotFoundException.class)
    public void testAddActivityNoActivityThrowsNotFoundException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        service.addActivity(activity);
        expectLastCall().andThrow(new EntityNotFoundException(""));
        testActivityServiceFactory.replay();
        endpoint.addActivity(newCaller(1, true), activity);
    }

    @Test
    public void testAddActivity() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        ActivityType activityType = new ActivityType("TEST");
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        expect(service.addActivity(activity)).andReturn(Datastore.allocateId(Activity.class));
        expectLastCall().once();
        testActivityServiceFactory.replay();
        assertEquals(activity, endpoint.addActivity(newCaller(1, true), activity));
    }

    @Test(expected = ForbiddenException.class)
    public void testRemoveActivityNotAdmin() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1, false), null);
    }

    @Test(expected = UnauthorizedException.class)
    public void testRemoveActivityNoUser() throws Exception {
        testActivityServiceFactory.replay();
        endpoint.removeActivity(null, null);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveActivityNoActivityThrowsNotFoundException() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().andThrow(new EntityNotFoundException(""));
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1, true), key);
    }

    @Test
    public void testRemoveActivity() throws Exception {
        ActivityService service = ActivityServiceFactory.getActivityService();
        Key key = Datastore.allocateId(Activity.class);
        service.removeActivity(key);
        expectLastCall().once();
        testActivityServiceFactory.replay();
        endpoint.removeActivity(newCaller(1, true), key);
    }
}
