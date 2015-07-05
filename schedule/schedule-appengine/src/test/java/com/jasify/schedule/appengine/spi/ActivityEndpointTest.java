package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTimeConstants;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
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

import java.util.Date;
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
        assertEquals(activityType1.getLcName().toLowerCase(), activityType2.getLcName());
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
        thrown.expectMessage("organizationId == null");
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
        endpoint.getActivityType(null, Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testGetActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getActivityType(newCaller(1), Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testGetActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.getActivityType(newAdminCaller(1), null);
    }

    @Test
    public void testGetActivityTypeForUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(ActivityType.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.getActivityType(newAdminCaller(1), id);
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
        endpoint.updateActivityType(null, Datastore.allocateId(ActivityType.class), new ActivityType());
    }

    @Test
    public void testUpdateActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivityType(newCaller(1), Datastore.allocateId(ActivityType.class), new ActivityType());
    }

    @Test
    public void testUpdateActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.updateActivityType(newAdminCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityTypeNullActivityType() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityType == null");
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
    public void testUpdateActivityTypeWithBlankName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name");
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        activityType.setName("       ");
        endpoint.updateActivityType(newAdminCaller(1), activityType.getId(), activityType);
    }

    @Test
    public void testUpdateActivityTypeWithUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(ActivityType.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, false);
        endpoint.updateActivityType(newAdminCaller(1), id, activityType);
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
    public void testUpdateActivityTypeExistingNameForSameOrganization() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType1 = TestHelper.createActivityType(organization, true);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name=" + activityType1.getName() + ", Organization.id=" + organization.getId());
        ActivityType activityType2 = TestHelper.createActivityType(organization, true);
        activityType2.setName(activityType1.getName());

        endpoint.updateActivityType(newAdminCaller(1), activityType2.getId(), activityType2);
    }

    @Test
    public void testUpdateActivityTypeExistingNameForDifferentOrganization() throws Exception {
        ActivityType activityType1 = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        ActivityType activityType2 = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        activityType2.setName(activityType1.getName());

        endpoint.updateActivityType(newAdminCaller(1), activityType2.getId(), activityType2);
    }

    @Test
    public void testUpdateActivityType() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        ActivityType dbActivityType = endpoint.getActivityType(newAdminCaller(1), activityType.getId());

        dbActivityType.setColourTag(dbActivityType.getColourTag() + "1");
        dbActivityType.setCurrency(dbActivityType.getCurrency() + "1");
        dbActivityType.setDescription(dbActivityType.getDescription() + "1");
        dbActivityType.setLocation(dbActivityType.getLocation() + "1");
        dbActivityType.setMaxSubscriptions(dbActivityType.getMaxSubscriptions() + 1);
        dbActivityType.setName(dbActivityType.getName() + "1");
        dbActivityType.setPrice(dbActivityType.getPrice() + 1);

        endpoint.updateActivityType(newAdminCaller(1), activityType.getId(), dbActivityType);

        ActivityType result = endpoint.getActivityType(newAdminCaller(1), activityType.getId());
        equals(dbActivityType, result);
        assertEquals(activityType.getColourTag() + "1", result.getColourTag());
        assertEquals(activityType.getCurrency() + "1", result.getCurrency());
        assertEquals(activityType.getDescription() + "1", result.getDescription());
        assertEquals(activityType.getLocation() + "1", result.getLocation());
        assertEquals(activityType.getMaxSubscriptions() + 1, result.getMaxSubscriptions());
        assertEquals(activityType.getName() + "1", result.getName());
        assertEquals(activityType.getPrice() + 1, result.getPrice());
    }

    // AddActivityType
    @Test
    public void testAddActivityTypeNullRequest() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
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
        thrown.expectMessage("request.activityType == null");
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeNullOrganizationId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.organizationId == null");
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
        jasAddActivityTypeRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeEmptyActivityTypeName() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name");
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        ActivityType activityType = new ActivityType();
        activityType.setName("      ");
        jasAddActivityTypeRequest.setActivityType(activityType);
        jasAddActivityTypeRequest.setOrganizationId(TestHelper.createOrganization(true).getId());
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeUnknownOrganizationId() throws Exception {
        thrown.expect(BadRequestException.class);
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setActivityType(TestHelper.createActivityType(TestHelper.createOrganization(false), false));
        jasAddActivityTypeRequest.setOrganizationId(Datastore.allocateId(Organization.class));
        endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
    }

    @Test
    public void testAddActivityTypeExistingNameForSameOrganization() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        String name = "Duplicate";
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("ActivityType.name=" + name + ", Organization.id=" + organization.getId());
        JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
        jasAddActivityTypeRequest.setOrganizationId(organization.getId());
        for (int i = 0; i < 2; i++) {
            ActivityType activityType = TestHelper.createActivityType(organization, false);
            activityType.setName(name);
            jasAddActivityTypeRequest.setActivityType(activityType);
            endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
        }
    }

    @Test
    public void testAddActivityTypeExistingNameForDifferentOrganization() throws Exception {
        for (int i = 0; i < 2; i++) {
            Organization organization = TestHelper.createOrganization(true);
            ActivityType activityType = TestHelper.createActivityType(organization, false);
            activityType.setName("SameName");
            JasAddActivityTypeRequest jasAddActivityTypeRequest = new JasAddActivityTypeRequest();
            jasAddActivityTypeRequest.setActivityType(activityType);
            jasAddActivityTypeRequest.setOrganizationId(organization.getId());
            ActivityType result = endpoint.addActivityType(newAdminCaller(1), jasAddActivityTypeRequest);
            assertNotNull(result);
        }
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
        endpoint.removeActivityType(null, Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testRemoveActivityTypeNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityType(newCaller(1), Datastore.allocateId(ActivityType.class));
    }

    @Test
    public void testRemoveActivityTypeNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.removeActivityType(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityTypeUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(ActivityType.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.removeActivityType(newAdminCaller(1), id);
    }

    @Test
    public void testRemoveActivityTypeWithActivities() throws Exception {
        ActivityType activityType = TestHelper.createActivityType(TestHelper.createOrganization(true), true);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Cannot delete activity type with activities! id=" + activityType.getId() + " (1 activities).");
        Activity activity = TestHelper.createActivity(activityType, true);
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
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
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
        thrown.expectMessage("id == null");
        endpoint.getActivity(newAdminCaller(1), null);
    }

    @Test
    public void testGetActivityUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(Activity.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.getActivity(newAdminCaller(1), id);
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
        endpoint.updateActivity(null, Datastore.allocateId(Activity.class), new Activity());
    }

    @Test
    public void testUpdateActivityNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivity(newCaller(1), Datastore.allocateId(Activity.class), new Activity());
    }

    @Test
    public void testUpdateActivityNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.updateActivity(newAdminCaller(1), null, new Activity());
    }

    @Test
    public void testUpdateActivityNullActivity() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activity == null");
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(Activity.class), null);
    }

    @Test
    public void testUpdateActivityNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Activity not found");
        Organization organization = TestHelper.createOrganization(true);
        endpoint.updateActivity(newAdminCaller(1), Datastore.allocateId(organization.getId(), ActivityMeta.get()), new Activity());
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
        Activity activity = TestHelper.createActivity(true);
        activity.setStart(null);
        endpoint.updateActivity(newAdminCaller(1), activity.getId(), activity);
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
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
        endpoint.addActivity(null, null);
    }

    @Test
    public void testAddActivityNullActivity() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activity == null");
        endpoint.addActivity(null, new JasAddActivityRequest());
    }

    @Test
    public void testAddActivityNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activity.activityType == null");
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(new Activity());
        endpoint.addActivity(null, jasAddActivityRequest);
    }

    @Test
    public void testAddActivityNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");

        ActivityType activityType = new ActivityType();
        Datastore.put(activityType);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
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
    public void testAddActivityWithNullStart() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.start");

        Activity activity = createActivity(false);
        activity.setStart(null);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithStartInThePast() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.start");

        Activity activity = createActivity(false);
        activity.setStart(new DateTime(2000, 1, 1, 10, 0, 0).toDate());

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNullFinish() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.finish");

        Activity activity = createActivity(false);
        activity.setFinish(null);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithFinishBeforeStart() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.finish");

        Activity activity = createActivity(false);
        DateTime finish = new DateTime(activity.getStart());
        finish = finish.minusDays(1);
        activity.setFinish(finish.toDate());

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNegativePrice() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.price");

        Activity activity = createActivity(false);
        activity.setPrice(new Double("-1"));

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityNegativeMaxSubscriptions() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Activity.maxSubscriptions");

        Activity activity = createActivity(false);
        activity.setMaxSubscriptions(-1);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNullNameAndNullActivityTypeKey() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activity.activityType == null");
        Activity activity = new Activity(new ActivityType());
        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivity() throws Exception {
        Activity activity = createActivity(false);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(1, result.size());
        equals(activity, result.get(0));
    }

    @Test
    public void testAddActivityWithNullName() throws Exception {
        Activity activity = createActivity(false);
        activity.setName(null);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
        assertEquals(activity.getActivityTypeRef().getModel().getName(), result.get(0).getName());
    }

    @Test
    public void testAddActivityWithNullRepeatType() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.repeatType");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(null);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithInvalidRepeatEvery() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.repeatEvery");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatEvery(0);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilType() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.repeatUntilType");


        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNullRepeatUntilDate() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.untilDate");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Date);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithRepeatUntilDateInThePast() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.untilDate");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Date);
        repeatDetails.setUntilDate(new Date(20));

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithInvalidRepeatDailyUntilCount() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.untilCount");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(0);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithNoRepeatDays() throws Exception {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("RepeatDetails.repeatDays");

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(1);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(createActivity(false));
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);
    }

    @Test
    public void testAddActivityWithRepeatDailyUntilCount() throws Exception {
        Activity activity = createActivity(false);
        DateTime date = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        date = date.plusDays(1);
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyUntilDate() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Date);
        DateTime date2 = date1.plusDays(1);
        repeatDetails.setUntilDate(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate());

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyEveryWeek() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(7); // Every 7 days

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date2 = date1.plusDays(7);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyEveryTwoWeeks() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(14); // Every two weeks

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date2 = date1.plusDays(14);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatDailyDoesNotExceedMaximum() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Daily);
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(50);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(ActivityCreator.MaximumRepeatCounter, result.size());
    }

    private void setRepeatDay(RepeatDetails repeatDetails, int jodaDayOfWeek) {
        switch (jodaDayOfWeek) {
            case DateTimeConstants.MONDAY:
                repeatDetails.setMondayEnabled(true);
                break;
            case DateTimeConstants.TUESDAY:
                repeatDetails.setTuesdayEnabled(true);
                break;
            case DateTimeConstants.WEDNESDAY:
                repeatDetails.setWednesdayEnabled(true);
                break;
            case DateTimeConstants.THURSDAY:
                repeatDetails.setThursdayEnabled(true);
                break;
            case DateTimeConstants.FRIDAY:
                repeatDetails.setFridayEnabled(true);
                break;
            case DateTimeConstants.SATURDAY:
                repeatDetails.setSaturdayEnabled(true);
                break;
            case DateTimeConstants.SUNDAY:
                repeatDetails.setSundayEnabled(true);
                break;
            default:
                break;
        }
    }

    @Test
    public void testAddActivityWithRepeatWeeklyCount() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date2 = date1.plusDays(7);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyDate() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Date);
        DateTime date2 = date1.plusDays(8);
        repeatDetails.setUntilDate(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate());

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date3 = date1.plusDays(7);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyEveryTwoWeeks() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(2);
        repeatDetails.setRepeatEvery(2); // Every two weeks

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(2, result.size());

        Activity activity1 = result.get(0);
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date3 = date1.plusDays(14);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());
    }

    @Test
    public void testAddActivityWithRepeatWeeklyTwoDaysEveryTwoWeeks() throws Exception {
        Activity activity = createActivity(false);
        DateTime date1 = new DateTime().plusDays(1);

        activity.setStart(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date1.getYear(), date1.getMonthOfYear(), date1.getDayOfMonth(), 11, 0, 0).toDate());

        RepeatDetails repeatDetails = new RepeatDetails();
        repeatDetails.setRepeatType(RepeatDetails.RepeatType.Weekly);
        setRepeatDay(repeatDetails, date1.plusDays(1).getDayOfWeek());
        setRepeatDay(repeatDetails, date1.plusDays(3).getDayOfWeek());
        repeatDetails.setRepeatUntilType(RepeatDetails.RepeatUntilType.Count);
        repeatDetails.setUntilCount(4);
        repeatDetails.setRepeatEvery(2); // Every two weeks

        JasAddActivityRequest jasAddActivityRequest = new JasAddActivityRequest();
        jasAddActivityRequest.setActivity(activity);
        jasAddActivityRequest.setRepeatDetails(repeatDetails);
        List<Activity> result = endpoint.addActivity(newAdminCaller(1), jasAddActivityRequest);

        assertNotNull(result);
        assertEquals(4, result.size());

        Activity activity1 = result.get(0);
        DateTime date2 = date1.plusDays(1);
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 10, 0, 0).toDate(), activity1.getStart());
        assertEquals(new DateTime(date2.getYear(), date2.getMonthOfYear(), date2.getDayOfMonth(), 11, 0, 0).toDate(), activity1.getFinish());

        Activity activity2 = result.get(1);
        DateTime date3 = date2.plusDays(2);
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 10, 0, 0).toDate(), activity2.getStart());
        assertEquals(new DateTime(date3.getYear(), date3.getMonthOfYear(), date3.getDayOfMonth(), 11, 0, 0).toDate(), activity2.getFinish());

        Activity activity3 = result.get(2);
        DateTime date4 = date2.plusDays(14);
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 10, 0, 0).toDate(), activity3.getStart());
        assertEquals(new DateTime(date4.getYear(), date4.getMonthOfYear(), date4.getDayOfMonth(), 11, 0, 0).toDate(), activity3.getFinish());

        Activity activity4 = result.get(3);
        DateTime date5 = date4.plusDays(2);
        assertEquals(new DateTime(date5.getYear(), date5.getMonthOfYear(), date5.getDayOfMonth(), 10, 0, 0).toDate(), activity4.getStart());
        assertEquals(new DateTime(date5.getYear(), date5.getMonthOfYear(), date5.getDayOfMonth(), 11, 0, 0).toDate(), activity4.getFinish());
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
        endpoint.removeActivity(null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivity(newCaller(1), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.removeActivity(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityWithUnknownId() throws Exception {
        thrown.expect(BadRequestException.class);
        Organization organization = TestHelper.createOrganization(true);
        endpoint.removeActivity(newAdminCaller(1), Datastore.allocateId(organization.getId(), ActivityMeta.get()));
    }

    @Test
    public void testRemoveActivityWithSubscriptions() throws Exception {
        Activity activity = createActivity(true);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Cannot delete activity with subscriptions! id=" + activity.getId() + " (1 subscriptions).");
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activity.getId());
        endpoint.removeActivity(newAdminCaller(1), activity.getId());
    }

    @Test
    public void testRemoveActivityInActivityPackage() throws Exception {
        Organization organization = TestHelper.createOrganization(true);
        Activity activity = createActivity(organization, true);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Cannot delete activity linked to activity packages! id=" + activity.getId() + " (1 activity packages).");
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
        thrown.expectMessage("userId == null");
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
        thrown.expectMessage("userId == null");
        endpoint.addSubscription(newAdminCaller(1), null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionWithNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityId == null");
        endpoint.addSubscription(newAdminCaller(1), Datastore.allocateId(User.class), null);
    }

    @Test
    public void testAddSubscriptionWithUnknownUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key userId = Datastore.allocateId(User.class);
        thrown.expectMessage("No entity was found matching the key: " + userId);
        endpoint.addSubscription(newAdminCaller(1), userId, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddSubscriptionWithUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityId = Datastore.allocateId(Activity.class);
        thrown.expectMessage("No entity was found matching the key: " + activityId);
        endpoint.addSubscription(newAdminCaller(1), createUser().getId(), activityId);
    }

    @Test
    public void testAddSubscriptionDoubleSubscription() throws Exception {
        User user = createUser();
        Activity activity = createActivity(true);
        endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
        endpoint.addSubscription(newAdminCaller(1), user.getId(), activity.getId());
        List<Subscription> subscriptions = endpoint.getSubscriptions(newAdminCaller(1), activity.getId());
        assertEquals(2, subscriptions.size());
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
    public void testGetSubscriptionNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getSubscription(null, Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionNullUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("userId == null");
        endpoint.getSubscription(newAdminCaller(1), null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionWithNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityId == null");
        endpoint.getSubscription(newAdminCaller(1), Datastore.allocateId(User.class), null);
    }

    @Test
    public void testGetSubscriptionWithUnknownUserId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No such subscription");
        endpoint.getSubscription(newAdminCaller(1), Datastore.allocateId(User.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionWithUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("No such subscription");
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
    public void testGetSubscriptionsNullUser() throws Exception {
        thrown.expect(UnauthorizedException.class);
        thrown.expectMessage("Only authenticated users can call this method");
        endpoint.getSubscriptions(null, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionsNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.getSubscriptions(newCaller(1), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testGetSubscriptionsNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityId == null");
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
        endpoint.cancelSubscription(null, Datastore.allocateId(Subscription.class));
    }

    @Test
    public void testCancelSubscriptionNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.cancelSubscription(newCaller(1), Datastore.allocateId(Subscription.class));
    }

    @Test
    public void testCancelSubscriptionNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("subscriptionId == null");
        endpoint.cancelSubscription(newAdminCaller(1), null);
    }

    @Test
    public void testCancelSubscriptionUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key subscriptionId = Datastore.allocateId(Subscription.class);
        thrown.expectMessage("No entity was found matching the key: " + subscriptionId);
        endpoint.cancelSubscription(newAdminCaller(1), subscriptionId);
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
        thrown.expectMessage("organizationId == null");
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
        thrown.expectMessage("id == null");
        endpoint.getActivityPackage(null, null);
    }

    @Test
    public void testGetActivityPackageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key id = Datastore.allocateId(ActivityPackage.class);
        thrown.expectMessage("No entity was found matching the key: " + id);
        endpoint.getActivityPackage(null, id);
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
        endpoint.updateActivityPackage(null, Datastore.allocateId(ActivityPackage.class), new JasActivityPackageRequest());
    }

    @Test
    public void testUpdateActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.updateActivityPackage(newCaller(1), Datastore.allocateId(ActivityPackage.class), new JasActivityPackageRequest());
    }

    @Test
    public void testUpdateActivityPackageNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.updateActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testUpdateActivityPackageNullRequest() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testUpdateActivityPackageNullNullActivityPackage() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activityPackage == null");
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), new JasActivityPackageRequest());
    }

    @Test
    public void testUpdateActivityPackageNullActivities() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request.activities == null");
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(new ActivityPackage());
        jasActivityPackageRequest.setActivities(null);
        endpoint.updateActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), jasActivityPackageRequest);
    }

    @Test
    public void testUpdateActivityPackageUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityPackageId = Datastore.allocateId(ActivityPackage.class);
        thrown.expectMessage("No entity was found matching the key: " + activityPackageId);
        JasActivityPackageRequest jasActivityPackageRequest = new JasActivityPackageRequest();
        jasActivityPackageRequest.setActivityPackage(new ActivityPackage());
        endpoint.updateActivityPackage(newAdminCaller(1), activityPackageId, jasActivityPackageRequest);
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
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("request == null");
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
        endpoint.removeActivityPackage(null, Datastore.allocateId(ActivityPackage.class));
    }

    @Test
    public void testRemoveActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityPackage(newCaller(1), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityPackageNullId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("id == null");
        endpoint.removeActivityPackage(newAdminCaller(1), null);
    }

    @Test
    public void testRemoveActivityPackageUnknownId() throws Exception {
        thrown.expect(BadRequestException.class);
        endpoint.removeActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class));
    }

    @Test
    public void testRemoveActivityPackageWithExecutions() throws Exception {
        ActivityPackage activityPackage = createActivityPackage(true);
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Cannot delete activity package with executions! id=" + activityPackage.getId() + " (1 executions).");
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
        thrown.expectMessage("activityPackageId == null");
        endpoint.getActivityPackageActivities(null, null);
    }

    @Test
    public void testGetActivityPackageActivitiesUnknownId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityPackageId = Datastore.allocateId(ActivityPackage.class);
        thrown.expectMessage("No entity was found matching the key: " + activityPackageId);
        endpoint.getActivityPackageActivities(null, activityPackageId);
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
        endpoint.addActivityToActivityPackage(null, Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddActivityToActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.addActivityToActivityPackage(newCaller(1), Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddActivityToActivityPackageNullActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityPackageId == null");
        endpoint.addActivityToActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testAddActivityToActivityPackageNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityId == null");
        endpoint.addActivityToActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testAddActivityToActivityPackageUnknownActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityPackageId = Datastore.allocateId(ActivityPackage.class);
        thrown.expectMessage("No entity was found matching the key: " + activityPackageId);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), activityPackageId, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testAddActivityToActivityPackageUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityId = Datastore.allocateId(Activity.class);
        thrown.expectMessage("No entity was found matching the key: " + activityId);
        endpoint.addActivityToActivityPackage(newAdminCaller(1), createActivityPackage(true).getId(), activityId);
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
        endpoint.removeActivityFromActivityPackage(null, Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityFromActivityPackageNotAdmin() throws Exception {
        thrown.expect(ForbiddenException.class);
        thrown.expectMessage("Must be admin");
        endpoint.removeActivityFromActivityPackage(newCaller(1), Datastore.allocateId(ActivityPackage.class), Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityFromActivityPackageNullActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityPackageId == null");
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), null, null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageNullActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("activityId == null");
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), Datastore.allocateId(ActivityPackage.class), null);
    }

    @Test
    public void testRemoveActivityFromActivityPackageUnknownActivityPackageId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityPackageId = Datastore.allocateId(ActivityPackage.class);
        thrown.expectMessage("No entity was found matching the key: " + activityPackageId);
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), activityPackageId, Datastore.allocateId(Activity.class));
    }

    @Test
    public void testRemoveActivityFromActivityPackageUnknownActivityId() throws Exception {
        thrown.expect(NotFoundException.class);
        Key activityId = Datastore.allocateId(Activity.class);
        thrown.expectMessage("No entity was found matching the key: " + activityId);
        endpoint.removeActivityFromActivityPackage(newAdminCaller(1), createActivityPackage(true).getId(), activityId);
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
