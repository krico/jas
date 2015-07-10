package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraints;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.*;

import static junit.framework.TestCase.*;

public class ActivityServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ActivityService activityService;
    private User testUser1;
    private User testUser2;
    private Activity activity1Organization1;
    private ActivityPackage activityPackage10Organization;
    private ActivityPackageExecution activityPackageExecution;

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        DateTime date = new DateTime();
        date = date.plusDays(1);
        activity.setStart(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activity.setFinish(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 11, 0, 0).toDate());
        return activity;
    }

    private ActivityPackage createActivityPackage(Organization organization) {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setKey(organization.getId());
        activityPackage.setDescription("New Desc");
        DateTime date = new DateTime();
        activityPackage.setCreated(date.toDate());
        activityPackage.setModified(date.toDate());
        activityPackage.setItemCount(1);
        activityPackage.setPrice(999d);
        activityPackage.setExecutionCount(0);
        activityPackage.setName("New Name");
        activityPackage.setCurrency("BRL");
        activityPackage.setMaxExecutions(200);
        activityPackage.setValidFrom(new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), 10, 0, 0).toDate());
        activityPackage.setValidUntil(new DateTime(date.getYear(), date.getMonthOfYear() + 1, 25, 10, 0, 0).toDate());
        return activityPackage;
    }

    private Organization createOrganization(String name) {
        return new Organization(name);
    }

    private User createUser(String name) {
        User user = new User(name);
        user.setEmail(name + "@test.com");
        user.setRealName("Real " + name);
        return user;
    }


    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        UniqueConstraints.ensureAllConstraintsExist();
        activityService = ActivityServiceFactory.getActivityService();
        Organization organization1 = createOrganization("Org1");
        Organization organization2 = createOrganization("Org2");
        testUser1 = createUser("testUser1");
        testUser2 = createUser("testUser2");
        Datastore.put(organization1, organization2, testUser1, testUser2);
        ActivityType activityType1OfOrganization1 = new ActivityType("AT1");
        ActivityType activityType2OfOrganization1 = new ActivityType("AT2");
        activityType1OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        activityType1OfOrganization1.getOrganizationRef().setKey(organization1.getId());
        activityType2OfOrganization1.setId(Datastore.allocateId(organization1.getId(), ActivityTypeMeta.get()));
        activityType2OfOrganization1.getOrganizationRef().setKey(organization1.getId());
        Datastore.put(activityType1OfOrganization1, activityType2OfOrganization1);
        activity1Organization1 = createActivity(activityType1OfOrganization1);
        activityPackage10Organization = createActivityPackage(organization1);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testSubscribe() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        Subscription subscription = activityService.subscribe(testUser1, activity);
        assertNotNull(subscription);
        assertEquals(testUser1.getId(), subscription.getUserRef().getKey());
        assertEquals(activity.getId(), subscription.getActivityRef().getKey());
        assertEquals(1, activity.getSubscriptionCount());
        List<Subscription> modelList = activity.getSubscriptionListRef().getModelList();
        assertEquals(1, modelList.size());
        assertEquals(subscription.getId(), modelList.get(0).getId());
    }

    @Test
    public void testSubscribeNotifiesIfUserNameIsNull() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        service.clearSentMessages();
        User user = new User();
        Datastore.put(user);
        activityService.subscribe(user, activity);
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(0, sentMessages.size());
    }

    @Test
    public void testSubscribeNotifiesIfActivityNameIsNull() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        activity.setName(null);
        Datastore.put(activity);
        LocalMailService service = LocalMailServiceTestConfig.getLocalMailService();
        service.clearSentMessages();
        activityService.subscribe(testUser1, activity);
        List<MailServicePb.MailMessage> sentMessages = service.getSentMessages();
        assertNotNull(sentMessages);
        assertEquals(0, sentMessages.size());
    }

    @Test
    public void testOversubscribe() throws Exception {
        thrown.expect(OperationException.class);
        thrown.expectMessage("Activity fully subscribed");
        Activity activity = TestHelper.createActivity(true);
        activity.setMaxSubscriptions(1);
        Datastore.put(activity);
        activityService.subscribe(testUser1, activity);
        activityService.subscribe(testUser2, activity);
    }

    @Test
    public void testSubscribeForZeroMaxSubscriptions() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        activity.setMaxSubscriptions(0);
        Datastore.put(activity);
        assertNotNull(activityService.subscribe(testUser1, activity));
        assertNotNull(activityService.subscribe(testUser2, activity));
        assertEquals(2, Datastore.get(ActivityMeta.get(), activity.getId()).getSubscriptionCount());
    }

    @Test
    public void testCancel() throws Exception {
        Activity activity = TestHelper.createActivity(true);
        Subscription subscription = activityService.subscribe(testUser1, activity);

        // cache it in
        activity1Organization1.getSubscriptionListRef().getModelList();

        activityService.cancelSubscription(subscription.getId());

        activity = Datastore.get(ActivityMeta.get(), activity.getId());

        assertEquals(0, activity.getSubscriptionCount());
        List<Subscription> modelList = activity.getSubscriptionListRef().getModelList();
        assertTrue(modelList.isEmpty());
        assertNull(Datastore.getOrNull(subscription.getId()));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCancelThrowsEntityNotFoundExceptionl() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setId(Datastore.allocateId(Subscription.class));
        activityService.cancelSubscription(subscription.getId());
    }

    @Test
    public void testSubscribeToActivityPackage() throws Exception {
        Activity activity1 = TestHelper.createActivity(true);
        Activity activity2 = TestHelper.createActivity(true);

        activityPackage10Organization.setItemCount(2);

        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1, activity2));
        activityPackageExecution = activityService.subscribe(testUser1, activityPackage10Organization, Arrays.asList(activity1, activity2));
        assertNotNull(activityPackageExecution);
        assertEquals(activityPackage10Organization.getId(), activityPackageExecution.getActivityPackageRef().getKey());
        assertEquals(testUser1.getId(), activityPackageExecution.getUserRef().getKey());
        assertNull(activityPackageExecution.getTransferRef().getKey());
        List<ActivityPackageSubscription> subscriptions = activityPackageExecution.getSubscriptionListRef().getModelList();
        assertEquals(2, subscriptions.size());
        HashSet<Key> activities = new HashSet<>();
        for (ActivityPackageSubscription subscription : subscriptions) {
            assertEquals(activityPackageExecution.getId(), subscription.getActivityPackageExecutionRef().getKey());
            assertNull(subscription.getTransferRef().getKey());
            assertEquals(testUser1.getId(), subscription.getUserRef().getKey());
            assertNotNull(subscription.getActivityRef().getKey());
            activities.add(subscription.getActivityRef().getKey());
        }
        assertTrue(activities.contains(activity1.getId()));
        assertTrue(activities.contains(activity2.getId()));
    }

    @Test
    public void testCancelActivityPackageExecution() throws Exception {
        testSubscribeToActivityPackage();
        ActivityPackage activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage10Organization.getId());
        assertEquals(1, activityPackage.getExecutionCount());

        List<ActivityPackageSubscription> subscriptions = activityPackageExecution.getSubscriptionListRef().getModelList();
        List<Activity> activities = new ArrayList<>();
        for (ActivityPackageSubscription subscription : subscriptions) {
            Activity activity = subscription.getActivityRef().getModel();
            activities.add(activity);
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityService.cancelActivityPackageExecution(activityPackageExecution);

        activityPackage = Datastore.get(ActivityPackageMeta.get(), activityPackage.getId());
        assertEquals(0, activityPackage.getExecutionCount());

        for (Activity activity : activities) {
            activity = Datastore.get(ActivityMeta.get(), activity.getId());
            assertEquals(0, activity.getSubscriptionCount());
        }

        for (ActivityPackageSubscription subscription : subscriptions) {
            assertNull(Datastore.getOrNull(subscription.getId()));
        }
        assertNull(Datastore.getOrNull(activityPackageExecution.getId()));
    }

    @Test
    public void testActivityWithCreateDateInPastThrows() {

    }

//    @Test
//    public void testActivityPackageWithValidUntilBeforeValidFromThrows() throws Exception {
//        thrown.expect(FieldValueException.class);
//        thrown.expectMessage("ActivityPackage.validUntil");
//        activityPackage10Organization.setItemCount(2);
//        DateTime datetime = new DateTime();
//        activityPackage10Organization.setValidFrom(datetime.plusDays(2).toDate());
//        activityPackage10Organization.setValidUntil(datetime.plusDays(1).toDate());
//        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
//    }

//    @Test
//    public void testActivityPackageWithValidFromInPastThrows() throws Exception {
//        thrown.expect(FieldValueException.class);
//        thrown.expectMessage("ActivityPackage.validFrom");
//        activityPackage10Organization.setItemCount(2);
//        activityPackage10Organization.setValidFrom(new Date(20));
//        activityService.addActivityPackage(activityPackage10Organization, Arrays.asList(activity1Organization1, activity2Organization1));
//    }
}
