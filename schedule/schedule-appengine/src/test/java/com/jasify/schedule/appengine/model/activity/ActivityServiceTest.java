package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

public class ActivityServiceTest {
    private static final String TEST_ACTIVITY_TYPE = "Test Activity Type";
    private ActivityService activityService;
    private User testUser = new User("testuser");
    private User testUser2 = new User("testuser2");
    private Organization organization1 = new Organization("Org1");
    private Organization organization2 = new Organization("Org2");
    private ActivityType activityType1OfOrganization1 = new ActivityType("AT1");
    private ActivityType activityType2OfOrganization1 = new ActivityType("AT2");

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        activityService = ActivityServiceFactory.getActivityService();
        Datastore.put(organization1, organization2, testUser, testUser2);
        activityType1OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        activityType2OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        Datastore.put(activityType1OfOrganization1, activityType2OfOrganization1);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testAddActivityType() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        assertNotNull(id);
    }

    @Test
    public void testAddActivityTypeSameNameInDifferentOrganizations() throws Exception {
        Key id1 = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        Key id2 = activityService.addActivityType(organization2, new ActivityType(TEST_ACTIVITY_TYPE));
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotSame(id1, id2);
    }

    @Test(expected = UniqueConstraintException.class)
    public void testAddActivityTypeThrowsUniqueNameConstraint() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddActivityTypeThrowsNotFound() throws Exception {
        activityService.addActivityType(new Organization("any"), new ActivityType(TEST_ACTIVITY_TYPE));
    }

    @Test(expected = FieldValueException.class)
    public void testAddActivityTypeThrowsFieldValueException() throws Exception {
        activityService.addActivityType(organization1, new ActivityType());
    }

    @Test
    public void testGetActivityTypeById() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType = activityService.getActivityType(id);
        assertNotNull(activityType);
        assertEquals(id, activityType.getId());
        assertEquals(TEST_ACTIVITY_TYPE, activityType.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByIdThrowsEntityNotFound() throws Exception {
        activityService.getActivityType(Datastore.allocateId(ActivityType.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetActivityTypeByIdThrowsIllegalArgumentException() throws Exception {
        activityService.getActivityType(organization1.getId());
    }

    @Test
    public void testGetActivityTypeByName() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType = activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE);
        assertNotNull(activityType);
        assertEquals(id, activityType.getId());
        assertEquals(TEST_ACTIVITY_TYPE, activityType.getName());
    }

    @Test
    public void testGetActivityTypeByNameCaseInsensitive() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE.toLowerCase());
    }

    @Test
    public void testGetActivityTypeByNameWithTwoOrganizations() throws Exception {
        Key id1 = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        Key id2 = activityService.addActivityType(organization2, new ActivityType(TEST_ACTIVITY_TYPE));
        ActivityType activityType1 = activityService.getActivityType(organization1, TEST_ACTIVITY_TYPE.toLowerCase());
        ActivityType activityType2 = activityService.getActivityType(organization2, TEST_ACTIVITY_TYPE.toLowerCase());
        assertNotNull(activityType1);
        assertNotNull(activityType2);
        assertEquals(id1, activityType1.getId());
        assertEquals(id2, activityType2.getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByNameWithNoOrganization() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(new Organization("foo"), TEST_ACTIVITY_TYPE);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypeByNameWithNoName() throws Exception {
        activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.getActivityType(organization1, "x" + TEST_ACTIVITY_TYPE);
    }

    @Test
    public void testGetActivityTypes() throws Exception {
        Datastore.delete(activityType1OfOrganization1.getId(), activityType2OfOrganization1.getId()); //clean slate ;-)
        List<ActivityType> activityTypes = activityService.getActivityTypes(organization1);
        assertNotNull(activityTypes);
        assertTrue(activityTypes.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            added.add(activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE + i)));
        }

        activityTypes = activityService.getActivityTypes(organization1);
        assertNotNull(activityTypes);
        assertEquals(20, activityTypes.size());
        assertEquals(20, added.size());
        for (ActivityType activityType : activityTypes) {
            assertTrue(added.remove(activityType.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypesThrowsNotFound() throws Exception {
        activityService.getActivityTypes(new Organization("Foo"));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityTypesThrowsNotFoundWithId() throws Exception {
        Organization foo = new Organization("Foo");
        foo.setId(Datastore.allocateId(Organization.class));
        activityService.getActivityTypes(foo);
    }

    @Test
    public void testUpdateActivityType() throws Exception {
        ActivityType activityType = new ActivityType(TEST_ACTIVITY_TYPE);
        Key id = activityService.addActivityType(organization1, activityType);
        activityType.setName("New Name");
        activityType.setDescription("Description");
        ActivityType updatedActivityType = activityService.updateActivityType(activityType);
        assertNotNull(updatedActivityType);
        assertEquals(id, updatedActivityType.getId());
        assertEquals("New Name", updatedActivityType.getName());
        assertEquals("Description", updatedActivityType.getDescription());
        assertEquals("New Name", activityService.getActivityType(id).getName());
    }

    @Test(expected = UniqueConstraintException.class)
    public void testUpdateActivityTypeThrowsUniqueConstraintException() throws Exception {
        ActivityType activityType1 = new ActivityType(TEST_ACTIVITY_TYPE);
        activityService.addActivityType(organization1, activityType1);
        activityType1.setName("New Name");
        activityType1.setDescription("Description");
        activityService.updateActivityType(activityType1);

        ActivityType activityType2 = new ActivityType(TEST_ACTIVITY_TYPE);
        activityService.addActivityType(organization1, activityType2);
        activityType2.setName("New Name");
        activityType2.setDescription("Description");
        activityService.updateActivityType(activityType2);
    }

    @Test(expected = FieldValueException.class)
    public void testUpdateActivityTypeThrowsFieldValueException() throws Exception {
        ActivityType activityType = new ActivityType();
        activityService.updateActivityType(activityType);
    }

    @Test
    public void testRemoveActivityType() throws Exception {
        Key id = activityService.addActivityType(organization1, new ActivityType(TEST_ACTIVITY_TYPE));
        activityService.removeActivityType(id);
        assertNull(Datastore.getOrNull(id));
    }

    @Test
    public void testAddActivity() throws Exception {
        Key id = activityService.addActivity(new Activity(activityType1OfOrganization1));
        assertNotNull(id);
        Key parent = id.getParent();
        assertNotNull(parent);
        assertEquals(organization1.getId(), parent);
    }

    @Test
    public void testGetActivity() throws Exception {
        Key id = activityService.addActivity(new Activity(activityType1OfOrganization1));
        Activity activity = activityService.getActivity(id);
        assertNotNull(activity);
        assertEquals(activityType1OfOrganization1.getName(), activity.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivityThrowsEntityNotFoundException() throws Exception {
        Key id = Datastore.allocateId(Activity.class);
        activityService.getActivity(id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivitiesByOrganizationNotFound() throws Exception {
        try {
            Datastore.delete(organization1.getId());
        } catch (Exception e) {
            fail();
        }
        activityService.getActivities(organization1);
    }

    @Test
    public void testGetActivitiesByOrganization() throws Exception {
        List<Activity> activities = activityService.getActivities(organization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());
        int total = 20;
        Set<Key> added = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            if (i % 3 == 0) {
                added.add(activityService.addActivity(new Activity(activityType1OfOrganization1)));
            } else {
                added.add(activityService.addActivity(new Activity(activityType2OfOrganization1)));
            }
        }

        activities = activityService.getActivities(organization1);
        assertNotNull(activities);
        assertEquals(20, activities.size());
        assertEquals(20, added.size());
        for (Activity activity : activities) {
            assertTrue(added.remove(activity.getId()));
        }
        assertTrue(added.isEmpty());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetActivitiesByActivityTypeNotFound() throws Exception {
        try {
            Datastore.delete(activityType1OfOrganization1.getId());
        } catch (Exception e) {
            fail();
        }
        activityService.getActivities(activityType1OfOrganization1);
    }

    @Test
    public void testGetActivitiesByActivityType() throws Exception {
        List<Activity> activities = activityService.getActivities(activityType1OfOrganization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());

        activities = activityService.getActivities(activityType2OfOrganization1);
        assertNotNull(activities);
        assertTrue(activities.isEmpty());

        int total = 20;
        Set<Key> addedType1 = new HashSet<>();
        Set<Key> addedType2 = new HashSet<>();
        for (int i = 0; i < total; ++i) {
            if (i % 3 == 0) {
                addedType1.add(activityService.addActivity(new Activity(activityType1OfOrganization1)));
            } else {
                addedType2.add(activityService.addActivity(new Activity(activityType2OfOrganization1)));
            }
        }

        activities = activityService.getActivities(activityType1OfOrganization1);
        assertNotNull(activities);
        assertEquals(addedType1.size(), activities.size());
        for (Activity activity : activities) {
            assertTrue(addedType1.remove(activity.getId()));
        }
        assertTrue(addedType1.isEmpty());

        activities = activityService.getActivities(activityType2OfOrganization1);
        assertNotNull(activities);
        assertEquals(addedType2.size(), activities.size());
        for (Activity activity : activities) {
            assertTrue(addedType2.remove(activity.getId()));
        }
        assertTrue(addedType2.isEmpty());
    }

    @Test
    public void testUpdateActivity() throws Exception {
        Activity activity = new Activity(activityType1OfOrganization1);
        Key id = activityService.addActivity(activity);
        Date expected = activity.getCreated();
        activity.setName("New Name");
        activity.setDescription("Description");
        activity.setCurrency("CHF");
        activity.setMaxSubscriptions(20);
        activity.setSubscriptionCount(10);
        activity.setStart(new Date(2));
        activity.setFinish(new Date(5));
        activity.setLocation("Location");

        activity.setCreated(new Date(99));
        activity.setModified(new Date(25));
        long before = System.currentTimeMillis();

        Activity updatedActivity = activityService.updateActivity(activity);

        assertNotNull(updatedActivity);
        assertEquals(id, updatedActivity.getId());
        assertEquals("New Name", updatedActivity.getName());
        assertEquals("Description", updatedActivity.getDescription());

        Activity fetched = activityService.getActivity(id);
        assertEquals("New Name", fetched.getName());
        assertEquals("Description", fetched.getDescription());
        assertEquals("CHF", fetched.getCurrency());
        assertEquals(20, fetched.getMaxSubscriptions());
        assertEquals(10, fetched.getSubscriptionCount());
        assertEquals(new Date(2), fetched.getStart());
        assertEquals(new Date(5), fetched.getFinish());
        assertEquals("Location", fetched.getLocation());
        assertEquals(expected, fetched.getCreated());
        assertTrue(before <= fetched.getModified().getTime());
    }

    @Test
    public void testRemoveActivity() throws Exception {
        Key id = activityService.addActivity(new Activity(activityType1OfOrganization1));
        activityService.removeActivity(id);
        assertNull(Datastore.getOrNull(id));
    }

    @Test
    public void testSubscribe() throws Exception {
        Activity activity = new Activity(activityType1OfOrganization1);
        activityService.addActivity(activity);
        Subscription subscription = activityService.subscribe(testUser, activity);
        assertNotNull(subscription);
        assertEquals(testUser.getId(), subscription.getUserRef().getKey());
        assertEquals(activity.getId(), subscription.getActivityRef().getKey());
        assertEquals(1, activity.getSubscriptionCount());
        List<Subscription> modelList = activity.getSubscriptionListRef().getModelList();
        assertEquals(1, modelList.size());
        assertEquals(subscription.getId(), modelList.get(0).getId());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testSubscribeThrowsEntityNotFoundException() throws Exception {
        User user = new User("TestUser");
        user.setId(Datastore.allocateId(User.class));
        activityService.subscribe(user, new Activity(activityType1OfOrganization1));
    }

    @Test(expected = UniqueConstraintException.class)
    public void testSubscribeTwice() throws Exception {
        Activity activity = new Activity(activityType1OfOrganization1);
        activityService.addActivity(activity);
        activityService.subscribe(testUser, activity);
        activityService.subscribe(testUser, activity);
    }

    @Test
    public void testCancel() throws Exception {
        Activity activity = new Activity(activityType1OfOrganization1);
        activityService.addActivity(activity);
        Subscription subscription = activityService.subscribe(testUser.getId(), activity.getId());

        // cache it in
        activity.getSubscriptionListRef().getModelList();

        activityService.cancel(subscription);

        activity = activityService.getActivity(activity.getId());

        assertEquals(0, activity.getSubscriptionCount());
        List<Subscription> modelList = activity.getSubscriptionListRef().getModelList();
        assertTrue(modelList.isEmpty());
        assertNull(Datastore.getOrNull(subscription.getId()));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCanceThrowsEntityNotFoundExceptionl() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setId(Datastore.allocateId(Subscription.class));
        activityService.cancel(subscription);
    }

    @Test
    public void testGetActivities() throws Exception {
        Activity activity = new Activity(activityType1OfOrganization1);
        activityService.addActivity(activity);
        Subscription subscription1 = activityService.subscribe(testUser.getId(), activity.getId());
        Subscription subscription2 = activityService.subscribe(testUser2, activity);
        List<Subscription> subscriptions = activityService.getSubscriptions(activity);
        assertNotNull(subscriptions);
        assertEquals(2, subscriptions.size());
        Set<Key> ids = new HashSet<>();
        for (Subscription subscription : subscriptions) {
            ids.add(subscription.getId());
        }
        assertTrue(ids.contains(subscription1.getId()));
        assertTrue(ids.contains(subscription2.getId()));
    }
}
