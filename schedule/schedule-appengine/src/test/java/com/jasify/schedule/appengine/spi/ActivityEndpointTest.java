package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import com.jasify.schedule.appengine.spi.dm.JasListQueryActivitiesRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.List;
import java.util.Random;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.*;

/**
 * @author wszarmach
 * @since 12/06/15.
 */
public class ActivityEndpointTest {
    private ActivityEndpoint endpoint;
    private Random generator = new Random();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private User createUser() {
        User user = new User("@email.com");
        Datastore.put(user);
        return user;
    }

    private ActivityType createActivityType(boolean store) {
        Organization organization = TestHelper.createOrganization(true);
        return TestHelper.createActivityType(organization, store);
    }

    private Activity createActivity(Organization organization, boolean store) {
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        return TestHelper.createActivity(activityType, store);
    }

    private Activity createActivity(boolean store) {
        return createActivity(TestHelper.createOrganization(true), store);
    }

    private ActivityPackage createActivityPackage(boolean store) {
        return TestHelper.createActivityPackage(TestHelper.createOrganization(true), store);
    }

    private void equals(ActivityType activityType1, ActivityType activityType2) {
        assertEquals(activityType1.getColourTag(), activityType2.getColourTag());
        assertEquals(activityType1.getCurrency(), activityType2.getCurrency());
        assertEquals(activityType1.getDescription(), activityType2.getDescription());
        assertEquals(activityType1.getId(), activityType2.getId());
        assertEquals(activityType1.getLcName(), activityType2.getLcName());
        assertEquals(activityType1.getLocation(), activityType2.getLocation());
        assertEquals(activityType1.getMaxSubscriptions(), activityType2.getMaxSubscriptions());
        assertEquals(activityType1.getName(), activityType2.getName());
        assertEquals(activityType1.getOrganizationRef().getKey(), activityType2.getOrganizationRef().getKey());
        assertEquals(activityType1.getPrice(), activityType2.getPrice());
    }

    private void equals(Activity activity1, Activity activity2) {
        assertEquals(activity1.getCurrency(), activity2.getCurrency());
        assertEquals(activity1.getDescription(), activity2.getDescription());
        assertEquals(activity1.getFinish(), activity2.getFinish());
        assertEquals(activity1.getId(), activity2.getId());
        assertEquals(activity1.getLocation(), activity2.getLocation());
        assertEquals(activity1.getMaxSubscriptions(), activity2.getMaxSubscriptions());
        assertEquals(activity1.getName(), activity2.getName());
        assertEquals(activity1.getPrice(), activity2.getPrice());
        assertEquals(activity1.getStart(), activity2.getStart());
        assertEquals(activity1.getSubscriptionCount(), activity2.getSubscriptionCount());
        assertEquals(activity1.getActivityTypeRef().getKey(), activity2.getActivityTypeRef().getKey());
    }

    private void equals(User user1, User user2) {
        assertEquals(user1.getAbout(), user2.getAbout());
        assertEquals(user1.getDisplayName(), user2.getDisplayName());
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getLocale(), user2.getLocale());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getRealName(), user2.getRealName());
        assertEquals(user1.getPassword(), user2.getPassword());
    }

    private void equals(Subscription subscription1, Subscription subscription2) {
        assertEquals(subscription1.getCreated(), subscription2.getCreated());
        assertEquals(subscription1.getId(), subscription2.getId());
        assertEquals(subscription1.getModified(), subscription2.getModified());
    }

    private void equals(ActivityPackage activityPackage1, ActivityPackage activityPackage2) {
        assertEquals(activityPackage1.getCreated(), activityPackage2.getCreated());
        assertEquals(activityPackage1.getCurrency(), activityPackage2.getCurrency());
        assertEquals(activityPackage1.getDescription(), activityPackage2.getDescription());
        assertEquals(activityPackage1.getExecutionCount(), activityPackage2.getExecutionCount());
        assertEquals(activityPackage1.getId(), activityPackage2.getId());
        assertEquals(activityPackage1.getItemCount(), activityPackage2.getItemCount());
        assertEquals(activityPackage1.getMaxExecutions(), activityPackage2.getMaxExecutions());
        assertEquals(activityPackage1.getName(), activityPackage2.getName());
        assertEquals(activityPackage1.getPrice(), activityPackage2.getPrice());
        assertEquals(activityPackage1.getValidFrom(), activityPackage2.getValidFrom());
        assertEquals(activityPackage1.getValidUntil(), activityPackage2.getValidUntil());
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        endpoint = new ActivityEndpoint();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    // GetActivityTypes
    @Test
    public void testGetActivityTypesByNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivityTypes(newCaller(1), null);
    }

    @Test
    public void testGetActivityTypesByUnknownId() throws Exception {
        List<ActivityType> result = endpoint.getActivityTypes(newCaller(1), Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivityTypes() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        List<ActivityType> activityTypes = endpoint.getActivityTypes(newCaller(1), organization.getId());
        assertEquals(1, activityTypes.size());
        equals(activityType, activityTypes.get(0));
    }

    // GetActivityType
    @Test
    public void testGetActivityTypeNoUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getActivityType(null, null);
    }

    @Test
    public void testGetActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getActivityType(newCaller(1), null);
    }

    @Test
    public void testGetActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getActivityType(newAdminCaller(1), null);
    }

    @Test
    public void testGetActivityTypeForUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivityType(newAdminCaller(1), Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testGetActivityType() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        ActivityType result = endpoint.getActivityType(newAdminCaller(1), activityType.getId());
        equals(activityType, result);
    }

    // UpdateActivityType
    @Test
    public void testUpdateActivityTypeNoUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.updateActivityType(null, null, null);
    }

    @Test
    public void testUpdateActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivityType(newCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.updateActivityType(newAdminCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityTypeNullActivityType() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.updateActivityType(newAdminCaller(1), Datastore.allocateId(ActivityType.class), null);
    }

    @Test
    public void testUpdateActivityTypeWithNoName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name");
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        activityType.setName(null);
        endpoint.updateActivityType(newAdminCaller(1), activityType.getId(), activityType);
    }

    @Test
    public void testUpdateActivityTypeWithUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        endpoint.updateActivityType(newAdminCaller(1), Datastore.allocateId(ActivityType.class), activityType);
    }

    @Test
    public void testUpdateActivityTypeName() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        ActivityType dbActivityType = endpoint.getActivityType(newAdminCaller(1), activityType.getId());

        dbActivityType.setName(dbActivityType.getName() + "Changed");
        endpoint.updateActivityType(newAdminCaller(1), activityType.getId(), dbActivityType);

        ActivityType result = endpoint.getActivityType(newAdminCaller(1), activityType.getId());
        equals(dbActivityType, result);
        assertFalse(result.getName().equals(activityType.getName()));
    }

    @Test
    public void testUpdateActivityType() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        ActivityType dbActivityType = endpoint.getActivityType(newAdminCaller(1), activityType.getId());

        dbActivityType.setMaxSubscriptions(dbActivityType.getMaxSubscriptions() + 1);
        endpoint.updateActivityType(newAdminCaller(1), activityType.getId(), dbActivityType);

        ActivityType result = endpoint.getActivityType(newAdminCaller(1), activityType.getId());
        equals(dbActivityType, result);
        assertEquals(activityType.getMaxSubscriptions() + 1, result.getMaxSubscriptions());
    }

    // AddActivityType
    @Test
    public void testAddActivityTypeNullRequest() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.addActivityType(null, null);
    }

    @Test
    public void testAddActivityTypeNoUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.addActivityType(null, new JasAddActivityTypeRequest());
    }

    @Test
    public void testAddActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.addActivityType(newCaller(1), new JasAddActivityTypeRequest());
    }

    @Test
    public void testAddActivityTypeNullActivityType() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeNullOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeNullActivityTypeName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name");
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(new ActivityType());
        jasAddActivityTypeRequest.setOrganizationId(Datastore.allocateId(Organization.class));
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeUnknownOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(TestHelper.createActivityType(TestHelper.createOrganization(false), false));
        jasAddActivityTypeRequest.setOrganizationId(Datastore.allocateId(Organization.class));
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeDuplicate() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name=" + activityType.getName() + ", Organization.id=" + organization.getId());
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(activityType);
        jasAddActivityTypeRequest.setOrganizationId(organization.getId());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityType() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(activityType);
        jasAddActivityTypeRequest.setOrganizationId(organization.getId());
        ActivityType result = endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
        assertNotNull(result);
        equals(activityType, result);
    }

    // RemoveActivityType
    @Test
    public void testRemoveActivityTypeNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.removeActivityType(null, null);
    }

    @Test
    public void testRemoveActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityType(newCaller(1), null);
    }

    @Test
    public void testRemoveActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.removeActivityType(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityTypeUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.removeActivityType(newAdminCaller(1), Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testRemoveActivityTypeWithActivities() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType has activities");
        Activity activity = createActivity(true);
        endpoint.removeActivityType(newAdminCaller(1), activity.getActivityTypeRef().getKey());
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        endpoint.removeActivityType(newAdminCaller(1), activityType.getId());
    }

    // GetActivities
    @Test
    public void testGetActivitiesBothKeysSet() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Must choose one: activityTypeId or organizationId");
        endpoint.getActivities(newCaller(1), Datastore.allocateId(Organization.class), Datastore.allocateId(ActivityType.class), null, null, null, null);
    }

    @Test
    public void testGetActivitiesNoKeySet() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Must choose one: activityTypeIds or organizationIds");
        endpoint.getActivities(newCaller(1), null, null, null, null, null, null);
    }

    @Test
    public void testGetActivitiesByUnknownOrganizationId() throws Exception {
        List<Activity> result = endpoint.getActivities(newCaller(1), Datastore.allocateId(Organization.class), null, null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesByUnknownActivityTypeId() throws Exception {
        List<Activity> result = endpoint.getActivities(newCaller(1), null, Datastore.allocateId(ActivityType.class), null, null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivitiesByOrganizationId() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            createActivity(organization, true);
        }
        List<Activity> result = endpoint.getActivities(newCaller(1), organization.getId(), null, null, null, null, null);
        assertEquals(count, result.size());
    }

    @Test
    public void testGetActivitiesByActivityId() throws Exception {
        ActivityType activityType = createActivityType(true);
        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            TestHelper.createActivity(activityType, true);
        }
        List<Activity> result = endpoint.getActivities(newCaller(1), null, activityType.getId(), null, null, null, null);
        assertEquals(count, result.size());
    }

    // FilterActivities
    @Test
    public void testFilterOffset() throws Exception {
        ActivityType activityType = createActivityType(true);
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        List<Activity> result = endpoint.getActivities(newCaller(1), null, activityType.getId(), null, null, 3, null);
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterOffsetGreaterSize() throws Exception {
        ActivityType activityType = createActivityType(true);
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        List<Activity> result = endpoint.getActivities(newCaller(1), null, activityType.getId(), null, null, 7, null);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterLimit() throws Exception {
        ActivityType activityType = createActivityType(true);
        for (int i = 0; i < 5; i++) {
            TestHelper.createActivity(activityType, true);
        }
        List<Activity> result = endpoint.getActivities(newCaller(1), null, activityType.getId(), null, null, null, 3);
        assertEquals(3, result.size());
    }

    // TODO Add following tests
    // Zero Offset
    // Zero Limit
    // Negative Offset
    // Negative Limit
    // FromDate After last activity
    // FromDate Before first activity
    // ToDate After last activity
    // ToDate Before first activity

    // GetActivitiesByIds
    @Test
    public void getActivitiesByIdsNullRequest() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Must choose one: activityTypeIds or organizationIds");
        endpoint.getActivitiesByIds(newCaller(1), null);
    }

    @Test
    public void getActivitiesByIdsBothIdsSet() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Must choose one: activityTypeIds or organizationIds");
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        jasListQueryActivitiesRequest.getActivityTypeIds().add(Datastore.allocateId(ActivityType.class));
        jasListQueryActivitiesRequest.getOrganizationIds().add(Datastore.allocateId(Organization.class));
        endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
    }

    @Test
    public void getActivitiesByIdsNoIdsSet() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Must choose one: activityTypeIds or organizationIds");
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
    }

    @Test
    public void getActivitiesByIdsUnknownOrganizationId() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        jasListQueryActivitiesRequest.getOrganizationIds().add(Datastore.allocateId(Organization.class));
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getActivitiesByIdsUnknownActivityTypeId() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        jasListQueryActivitiesRequest.getActivityTypeIds().add(Datastore.allocateId(ActivityType.class));
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getActivitiesByIdsByOrganizationId() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        Organization organization = TestHelper.createOrganization(true);
        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            createActivity(organization, true);
        }
        jasListQueryActivitiesRequest.getOrganizationIds().add(organization.getId());
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertEquals(count, result.size());
    }

    @Test
    public void getActivitiesByIdsByActivityId() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();
        ActivityType activityType = createActivityType(true);
        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            TestHelper.createActivity(activityType, true);
        }
        jasListQueryActivitiesRequest.getActivityTypeIds().add(activityType.getId());
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertEquals(count, result.size());
    }

    @Test
    public void getActivitiesByIdsByOrganizationIds() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();

        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            Organization organization = TestHelper.createOrganization(true);
            createActivity(organization, true);
            jasListQueryActivitiesRequest.getOrganizationIds().add(organization.getId());
        }
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertEquals(count, result.size());
    }

    @Test
    public void getActivitiesByIdsByActivityIds() throws Exception {
        JasListQueryActivitiesRequest jasListQueryActivitiesRequest = new JasListQueryActivitiesRequest();

        int count = generator.nextInt(5) + 1;
        for (int i = 0; i < count; i++) {
            ActivityType activityType = createActivityType(true);
            TestHelper.createActivity(activityType, true);
            jasListQueryActivitiesRequest.getActivityTypeIds().add(activityType.getId());
        }
        List<Activity> result = endpoint.getActivitiesByIds(newCaller(1), jasListQueryActivitiesRequest);
        assertEquals(count, result.size());
    }

    // TODO: Expand

    // GetActivity
    @Test
    public void testGetActivityNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getActivity(null, null);
    }

    @Test
    public void testGetActivityNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getActivity(newAdminCaller(1), null);
    }

    @Test
    public void testGetActivityUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivity(newAdminCaller(1), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetActivity() throws Exception {
        Activity activity = createActivity(true);
        Activity result = endpoint.getActivity(newAdminCaller(1), activity.getId());
        assertNotNull(result);
        equals(activity, result);
    }

    // UpdateActivity
    @Test
    public void testUpdateActivityNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.updateActivity(null, null, new Activity());
    }

    @Test
    public void testUpdateActivityNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivity(newCaller(1), null, new Activity());
    }

    @Test
    public void testUpdateActivityNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.updateActivity(newAdminCaller(1), null, new Activity());
    }

    @Test
    public void testUpdateActivityNullActivity() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), null);
    }

    @Test
    public void testUpdateActivityNullActivityTypeKey() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), new Activity());
    }

    @Test
    public void testUpdateActivityUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Activity not found");
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), createActivity(false));
    }

    @Test
    public void testUpdateActivityWithNullNameAndNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Activity not found");
        ActivityType activityType = new ActivityType();
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), activity);
    }

    @Test
    public void testUpdateActivityWithInvalidValue() throws Exception {
        thrown.expect(BadRequestException.class);
        Activity activity = createActivity(true);
        activity.setStart(null);
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), activity);
    }

    @Test
    public void testUpdateActivityWithNullName() throws Exception {
        Activity activity = createActivity(true);
        activity.setName(null);
        Activity result = endpoint.updateActivity(newAdminCaller(1), activity.getId(), activity);
        assertEquals(activity.getActivityTypeRef().getModel().getName(), result.getName());
    }

    @Test
    public void testUpdateActivity() throws Exception {
        Activity activity1 = createActivity(true);
        Activity activity2 = TestHelper.createActivity(activity1.getActivityTypeRef().getModel(), false);
        Activity result = endpoint.updateActivity(newAdminCaller(1), activity1.getId(), activity2);
        equals(activity2, result);
    }

    // AddActivity
    @Test
    public void testAddActivityNullRequest() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.addActivity(null, null);
    }

    @Test
    public void testAddActivityNullActivity() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.addActivity(null, new JasAddActivityRequest());
    }

    @Test
    public void testAddActivityNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(null, jasAddActivityRequest);
    }

    @Test
    public void testAddActivityNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        ActivityType activityType = new ActivityType();
        Datastore.put(activityType);
        jasAddActivityRequest.setActivity(new Activity(activityType));
        endpoint.addActivity(null, jasAddActivityRequest);
    }

    @Test
    public void testAddActivityNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        endpoint.addActivity(newCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityInvalidValue() throws Exception {
        thrown.expect(BadRequestException.class);
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNullNameAndNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        ActivityType activityType = new ActivityType();
        activityType.setId(Datastore.allocateId(ActivityType.class));
        Activity activity = new Activity(activityType);
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivity() throws Exception {
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        Activity activity = createActivity(false);
        jasAddActivityRequest.setActivity(activity);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(1, result.size());
        equals(activity, result.get(0));
    }

    @Test
    public void testAddActivityWithNullName() throws Exception {
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        Activity activity = createActivity(false);
        activity.setName(null);
        jasAddActivityRequest.setActivity(activity);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(activity.getActivityTypeRef().getModel().getName(), result.get(0).getName());
    }

    @Test
    public void testAddActivityWithRepeatDetails() throws Exception {
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        Activity activity = createActivity(false);
        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatEvery(1);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(5);
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(5, result.size());
    }

    // RemoveActivity
    @Test
    public void testRemoveActivityNoUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.removeActivity(null, null);
    }

    @Test
    public void testRemoveActivityNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivity(newCaller(1), null);
    }

    @Test
    public void testRemoveActivityNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.removeActivity(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityWithUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.removeActivity(newAdminCaller(1), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityWithSubscriptions() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity has subscriptions");
        Activity activity = createActivity(true);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        endpoint.removeActivity(newAdminCaller(1), activity.getId());
    }

    @Test
    public void testRemoveActivityInActivityPackage() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity is linked to Activity Package");
        Organization organization = TestHelper.createOrganization(true);
        Activity activity = createActivity(organization, true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), activityPackage.getId(), activity.getId());
        endpoint.removeActivity(newAdminCaller(1), activity.getId());
    }

    @Test
    public void testRemoveActivity() throws Exception {
        Activity activity = createActivity(true);
        endpoint.removeActivity(newAdminCaller(1), activity.getId());
        List<Activity> result = endpoint.getActivities(newAdminCaller(1), null, activity.getActivityTypeRef().getKey(), null, null, null, null);
        assertTrue(result.isEmpty());
    }

    // AddSubscription
    @Test
    public void testAddSubscriptionNoUserThrowsNotFoundException() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addSubscription(null, null, null);
    }

    @Test
    public void testAddSubscriptionNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.addSubscription(null, Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionNullUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addSubscription(newAdminCaller(1), null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionWithNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.addSubscription(newAdminCaller(1), Datastore.allocateId(User.class), null);
    }

    @Test
    public void testAddSubscriptionWithUnknownUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addSubscription(newAdminCaller(1), Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionWithUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionDoubleSubscription() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("User already subscribed");
        User user = createUser();
        Activity activity = createActivity(true);
        endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
        endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
    }

    @Test
    public void testAddSubscriptionOverSubscribe() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity fully subscribed");
        Activity activity = createActivity(true);
        activity.setMaxSubscriptions(1);
        Datastore.put(activity);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
    }

    @Test
    public void testAddSubscription() throws Exception {
        Activity activity = createActivity(true);
        activity.setMaxSubscriptions(1);
        Datastore.put(activity);
        User user = createUser();
        Subscription subscription = endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
        activity = endpoint.getActivity(newAdminCaller(1), activity.getId());
        equals(activity, subscription.getActivityRef().getModel());
        equals(user, subscription.getUserRef().getModel());
    }

    // GetSubscription
    @Test
    public void testGetSubscriptionNoUserThrowsNotFoundException() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getSubscription(null, null, null);
    }

    @Test
    public void testGetSubscriptionNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getSubscription(null, Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionNullUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getSubscription(newAdminCaller(1), null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionWithNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getSubscription(newAdminCaller(1), Datastore.allocateId(User.class), null);
    }

    @Test
    public void testGetSubscriptionWithUnknownUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getSubscription(newAdminCaller(1), Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionWithUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getSubscription(newAdminCaller(1), createUser().getId(), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionWithNotSubscribedUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No such subscription");
        Activity activity = createActivity(true);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        endpoint.getSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
    }

    @Test
    public void testGetSubscription() throws Exception {
        User user = createUser();
        Activity activity = createActivity(true);
        Subscription subscription = endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
        Subscription result = endpoint.getSubscription(newAdminCaller(1), user.getId(), activity.getId());
        equals(subscription, result);
    }

    // GetSubscriptions
    @Test
    public void testGetSubscriptionsNulUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getSubscriptions(null, null);
    }

    @Test
    public void testGetSubscriptionsNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getSubscriptions(newCaller(1), null);
    }

    @Test
    public void testGetSubscriptionsNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getSubscriptions(newAdminCaller(1), null);
    }

    @Test
    public void testGetSubscriptionsUnknownId() throws Exception {
        List<Subscription> result = endpoint.getSubscriptions(newAdminCaller(1), Datastore.allocateId(ActivityType.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSubscriptions() throws Exception {
        Activity activity = createActivity(true);
        activity.setMaxSubscriptions(5);
        Datastore.put(activity);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        List<Subscription> result = endpoint.getSubscriptions(newAdminCaller(1), activity.getId());
        assertEquals(2, result.size());
    }

    // CancelSubscriptions
    @Test
    public void testCancelSubscriptionNulUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.cancelSubscription(null, null);
    }

    @Test
    public void testCancelSubscriptionNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.cancelSubscription(newCaller(1), null);
    }

    @Test
    public void testCancelSubscriptionNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.cancelSubscription(newAdminCaller(1), null);
    }

    @Test
    public void testCancelSubscriptionUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.cancelSubscription(newAdminCaller(1), Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testCancelSubscription() throws Exception {
        Activity activity = createActivity(true);
        Subscription subscription = endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        assertEquals(1, endpoint.getSubscriptions(newAdminCaller(1), activity.getId()).size());
        endpoint.cancelSubscription(newAdminCaller(1), subscription.getId());
        assertEquals(0, endpoint.getSubscriptions(newAdminCaller(1), activity.getId()).size());
    }

    // GetActivityPackages
    @Test
    public void testGetActivityPackagesNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getActivityPackages(null, null);
    }

    @Test
    public void testGetActivityPackagesUnknownId() throws Exception {
        List<ActivityPackage> result = endpoint.getActivityPackages(null, Datastore.allocateId(Organization.class));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetActivityPackages() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        List<ActivityPackage> result = endpoint.getActivityPackages(null, organization.getId());
        assertEquals(1, result.size());
        equals(activityPackage, result.get(0));
    }

    // GetActivityPackage
    @Test
    public void testGetActivityPackageNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.getActivityPackage(null, null);
    }

    @Test
    public void testGetActivityPackageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivityPackage(null, Datastore.allocateId(ActivityPackage.class));
    }

    @Test
    public void testGetActivityPackage() throws Exception {
        ActivityPackage activityPackage = createActivityPackage(true);
        ActivityPackage result = endpoint.getActivityPackage(null, activityPackage.getId());
        equals(activityPackage, result);
    }

    // UpdateActivityPackage
    @Test
    public void testUpdateActivityPackageNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.updateActivityPackage(null, null, null);
    }

    @Test
    public void testUpdateActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivityPackage(newCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityPackageNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.updateActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityPackageNullRequest() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testUpdateActivityPackageNullNullActivityPackage() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), new JasActivityPackageRequest());
    }

    @Test
    public void testUpdateActivityPackageNullNullActivities() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(new ActivityPackage());
        jasActivityPackageRequest.setActivities(null);
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), jasActivityPackageRequest);
    }

    @Test
    public void testUpdateActivityPackageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(new ActivityPackage());
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), jasActivityPackageRequest);
    }

    @Test
    public void testUpdateActivityPackage() throws Exception {
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        ActivityPackage activityPackage = createActivityPackage(true);
        ActivityPackage dbActivityPackage = endpoint.getActivityPackage(newAdminCaller(1), activityPackage.getId());
        dbActivityPackage.setName(dbActivityPackage.getName() + "Changed");
        jasActivityPackageRequest.setActivityPackage(dbActivityPackage);
        ActivityPackage result = endpoint.updateActivityPackage(newAdminCaller(1), activityPackage.getId(), jasActivityPackageRequest);
        equals(dbActivityPackage, result);
        assertFalse(result.getName().equals(activityPackage.getName()));
    }

    // AddActivityPackage
    @Test
    public void testAddActivityPackageNullNullRequest() throws Exception {
        // TODO: FIX THIS
        thrown.expect(NullPointerException.class);
        endpoint.addActivityPackage(newAdminCaller(1), null);
    }

    @Test
    public void testAddActivityPackageNullNullActivityPackage() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activityPackage == NULL");
        endpoint.addActivityPackage(newAdminCaller(1), new JasActivityPackageRequest());
    }

    @Test
    public void testAddActivityPackageNullNullOrganizationKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activityPackage.organization == NULL");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(new ActivityPackage());
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageNullNullActivities() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activities == NULL");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setModel(TestHelper.createOrganization(true));
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.setActivities(null);
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageEmptyActivities() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("request.activities.isEmpty");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setModel(TestHelper.createOrganization(true));
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        endpoint.addActivityPackage(null, jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        endpoint.addActivityPackage(newCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageUnknownOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(null, true);
        activityPackage.getOrganizationRef().setKey(Datastore.allocateId(Organization.class));
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackageInvalidValue() throws Exception {
        thrown.expect(BadRequestException.class);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
    }

    @Test
    public void testAddActivityPackage() throws Exception {
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        activityPackage.setItemCount(2);
        Datastore.put(activityPackage);
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        jasActivityPackageRequest.getActivities().add(createActivity(organization, true));
        ActivityPackage result = endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
        equals(activityPackage, result);
        assertEquals(2, result.getActivities().size());
    }

    // RemoveActivityPackage
    @Test
    public void testRemoveActivityPackageNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.removeActivityPackage(null, null);
    }

    @Test
    public void testRemoveActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityPackage(newCaller(1), null);
    }

    @Test
    public void testRemoveActivityPackageNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.removeActivityPackage(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityPackageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.removeActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class));
    }

    @Test
    public void testRemoveActivityPackageWithExecutions() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityPackage has executions");
        ActivityPackage activityPackage = createActivityPackage(true);
        activityPackage.setExecutionCount(1);
        Datastore.put(activityPackage);
        endpoint.removeActivityPackage(newAdminCaller(1), activityPackage.getId());
    }

    @Test
    public void testRemoveActivityPackage() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        endpoint.removeActivityPackage(newAdminCaller(1), activityPackage.getId());
        List<ActivityPackage> result = endpoint.getActivityPackages(newAdminCaller(1), organization.getId());
        assertTrue(result.isEmpty());
    }

    // GetActivityPackageActivities
    @Test
    public void testGetActivityPackageActivitiesNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivityPackageActivities(null, null);
    }

    @Test
    public void testGetActivityPackageActivitiesUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.getActivityPackageActivities(null, Datastore.allocateId(ActivityPackage.class));
    }

    @Test
    public void testGetActivityPackageActivities() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        activityPackage.setItemCount(2);
        Datastore.put(activityPackage);
        Activity activity1 = createActivity(organization, true);
        Activity activity2 = createActivity(organization, true);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(activityPackage);
        jasActivityPackageRequest.getActivities().add(activity1);
        jasActivityPackageRequest.getActivities().add(activity2);
        endpoint.addActivityPackage(newAdminCaller(1), jasActivityPackageRequest);
        List<Activity> result = endpoint.getActivityPackageActivities(newAdminCaller(1), activityPackage.getId());
        assertEquals(2, result.size());
    }

    // AddActivityToActivityPackage
    @Test
    public void testAddActivityToActivityPackagePackageNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.addActivityToActivityPackage(null, null, null);
    }

    @Test
    public void testAddActivityToActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.addActivityToActivityPackage(newCaller(1), null, null);
    }

    @Test
    public void testAddActivityToActivityPackageNullActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.addActivityToActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testAddActivityToActivityPackageNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.addActivityToActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testAddActivityToActivityPackageUnknownActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddActivityToActivityPackageUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), createActivityPackage(true).getId(), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddActivityToActivityPackage() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        Activity activity = createActivity(organization, true);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), activityPackage.getId(), activity.getId());
        List<Activity> result = endpoint.getActivityPackageActivities(newAdminCaller(1), activityPackage.getId());
        assertEquals(1, result.size());
        equals(activity, result.get(0));
    }

    // RemoveActivityFromActivityPackage
    @Test
    public void testRemoveActivityFromActivityPackagePackageNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.removeActivityFromActivityPackage(null, null, null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityFromActivityPackage(newCaller(1), null, null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageNullActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Not found");
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageUnknownActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityFromActivityPackageUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), createActivityPackage(true).getId(), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityFromActivityPackage() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, true);
        Activity activity = createActivity(organization, true);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), activityPackage.getId(), activity.getId());
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), activityPackage.getId(), activity.getId());
        List<Activity> result = endpoint.getActivityPackageActivities(newAdminCaller(1), activityPackage.getId());
        assertTrue(result.isEmpty());
    }
}
