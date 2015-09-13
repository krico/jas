package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.workflow.ActivityPackagePaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.*;

import static junit.framework.TestCase.*;

public class NavigateTest {
    private static final Comparator<HasId> sortById = new Comparator<HasId>() {
        @Override
        public int compare(HasId o1, HasId o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    private static <T extends HasId> void assertEqualsList(List<T> expectedOrig, List<T> actualOrig) {
        assertNotNull(actualOrig);
        List<T> expected = new ArrayList<>(expectedOrig);
        List<T> actual = new ArrayList<>(actualOrig);
        assertEquals(expected.size(), actual.size());
        Collections.sort(expected, sortById);
        Collections.sort(actual, sortById);

        for (int i = 0; i < expected.size(); ++i) {
            assertEquals("obj[" + i + "]\n\t" + expected + "\n\t" + actual, expected.get(i).getId(), actual.get(i).getId());
        }
    }

    @Before
    public void setupDatastore() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testActivityFromSubscriptionNull() {
        assertNull(Navigate.activity((Subscription) null));
    }

    @Test
    public void testActivityFromSubscriptionNoRef() {
        Subscription subscription = new Subscription();
        Datastore.put(subscription);
        assertNull(Navigate.activity(subscription));
    }

    @Test
    public void testActivityFromSubscriptionNonExistent() {
        Activity activity = new Activity();
        Subscription subscription = new Subscription();
        subscription.getActivityRef().setKey(Datastore.allocateId(Activity.class));
        Datastore.put(subscription);
        assertNull(Navigate.activity(subscription));
    }

    @Test
    public void testActivityFromSubscription() {
        Activity activity = new Activity();
        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);
        Datastore.put(subscription, activity);
        Activity navigated = Navigate.activity(subscription);
        assertNotNull(navigated);
        assertEquals(activity.getId(), navigated.getId());
    }

    @Test
    public void testActivityTypeFromActivityNull() {
        assertNull(Navigate.activityType((Activity) null));
    }

    @Test
    public void testActivityTypeFromActivityNoRef() {
        Activity activity = new Activity();
        Datastore.put(activity);
        assertNull(Navigate.activityType(activity));
    }

    @Test
    public void testActivityTypeFromActivityNonExistent() {
        Activity activity = new Activity();
        activity.getActivityTypeRef().setKey(Datastore.allocateId(ActivityType.class));
        Datastore.put(activity);
        assertNull(Navigate.activityType(activity));
    }

    @Test
    public void testActivityTypeFromActivity() {
        Activity activity = new Activity();
        ActivityType activityType = new ActivityType();
        activity.getActivityTypeRef().setModel(activityType);
        Datastore.put(activity, activityType);
        ActivityType navigated = Navigate.activityType(activity);
        assertNotNull(navigated);
        assertEquals(activityType.getId(), navigated.getId());
    }

    @Test
    public void testOrganizationFromActivityTypeNull() {
        assertNull(Navigate.organization((ActivityType) null));
    }

    @Test
    public void testOrganizationFromActivityTypeNoRef() {
        ActivityType activityType = new ActivityType();
        Datastore.put(activityType);
        assertNull(Navigate.organization(activityType));
    }

    @Test
    public void testOrganizationFromActivityTypeNonExistent() {
        ActivityType activityType = new ActivityType();
        activityType.getOrganizationRef().setKey(Datastore.allocateId(Organization.class));
        Datastore.put(activityType);
        assertNull(Navigate.organization(activityType));
    }

    @Test
    public void testOrganizationFromActivityType() {
        ActivityType activityType = new ActivityType();
        Organization organization = new Organization();
        activityType.getOrganizationRef().setModel(organization);
        Datastore.put(activityType, organization);
        Organization navigated = Navigate.organization(activityType);
        assertNotNull(navigated);
        assertEquals(organization.getId(), navigated.getId());
    }

    @Test
    public void testOrganizationFromSubscriptionNull() {
        assertNull(Navigate.organization((Subscription) null));
    }

    @Test
    public void testOrganizationFromSubscription() {
        Organization organization = new Organization();

        ActivityType activityType = new ActivityType();
        activityType.getOrganizationRef().setModel(organization);

        Activity activity = new Activity();
        activity.getActivityTypeRef().setModel(activityType);

        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);

        Datastore.put(subscription, activity, activityType, organization);

        subscription.getActivityRef().getModel().getActivityTypeRef().getModel().getOrganizationRef().getModel();
        Organization navigated = Navigate.organization(subscription);
        assertNotNull(navigated);
        assertEquals(organization.getId(), navigated.getId());
    }

    @Test
    public void testUsersFromOrganizationNull() {
        assertNull(Navigate.users((Organization) null));
    }

    @Test
    public void testUsersFromOrganization() {
        Organization organization = new Organization();
        User user1 = new User();
        User user2 = new User();
        OrganizationMember om1 = new OrganizationMember();
        OrganizationMember om2 = new OrganizationMember();

        om1.getOrganizationRef().setModel(organization);
        om1.getUserRef().setModel(user1);

        om2.getOrganizationRef().setModel(organization);
        om2.getUserRef().setModel(user2);

        Datastore.put(organization, user1, user2, om1, om2);

        List<User> users = Navigate.users(organization);
        assertNotNull(users);
        assertEquals(2, users.size());
        Set<Key> userIds = new HashSet<>();
        for (User user : users) {
            userIds.add(user.getId());
        }
        assertTrue(userIds.contains(user1.getId()));
        assertTrue(userIds.contains(user2.getId()));
    }

    @Test
    public void testOrganizationFromActivityPackageNull() {
        assertNull(Navigate.organization((ActivityPackage) null));
    }

    @Test
    public void testOrganizationFromActivityPackage() {
        ActivityPackage activityPackage = new ActivityPackage();
        Organization organization = new Organization();
        activityPackage.getOrganizationRef().setModel(organization);
        Datastore.put(activityPackage, organization);
        Organization navigated = Navigate.organization(activityPackage);
        assertNotNull(navigated);
        assertEquals(organization.getId(), navigated.getId());
    }

    @Test
    public void testActivityPackageFromActivityPackageExecutionNull() {
        assertNull(Navigate.activityPackage((ActivityPackageExecution) null));
    }

    @Test
    public void testActivityPackageFromActivityPackageExecution() {
        ActivityPackageExecution execution = new ActivityPackageExecution();
        ActivityPackage activityPackage = new ActivityPackage();
        execution.getActivityPackageRef().setModel(activityPackage);
        Datastore.put(execution, activityPackage);

        ActivityPackage navigated = Navigate.activityPackage(execution);

        assertNotNull(navigated);
        assertEquals(activityPackage.getId(), navigated.getId());
    }

    @Test
    public void testOrganizationFromActivityPackageExecutionNull() {
        assertNull(Navigate.organization((ActivityPackageExecution) null));
    }

    @Test
    public void testOrganizationFromActivityPackageExecution() {
        ActivityPackageExecution execution = new ActivityPackageExecution();
        ActivityPackage activityPackage = new ActivityPackage();
        Organization organization = new Organization();
        activityPackage.getOrganizationRef().setModel(organization);
        execution.getActivityPackageRef().setModel(activityPackage);

        Datastore.put(execution, activityPackage, organization);

        Organization navigated = Navigate.organization(execution);
        assertNotNull(navigated);
        assertEquals(organization.getId(), navigated.getId());
    }

    @Test
    public void testAttachmentFromInvoicePaymentNul() {
        assertNull(Navigate.attachment((InvoicePayment) null));
    }

    @Test
    public void testAttachmentFromInvoicePayment() {
        InvoicePayment payment = new InvoicePayment();
        Attachment attachment = new Attachment();
        payment.getAttachmentRef().setModel(attachment);
        Datastore.put(payment, attachment);

        Attachment navigated = Navigate.attachment(payment);
        assertNotNull(navigated);
        assertEquals(attachment.getId(), navigated.getId());
    }

    @Test
    public void testUserFromPayment() {
        Payment payment = new Payment();
        User user = new User();
        payment.getUserRef().setModel(user);
        Datastore.put(payment, user);
        User navigated = Navigate.user(payment);
        assertNotNull(navigated);
        assertEquals(user.getId(), navigated.getId());
    }

    @Test
    public void testOrganizationsFromPayment() {
        Payment payment = new Payment();
        List<Organization> expected = new ArrayList<>();
        Organization organization1 = new Organization();
        expected.add(organization1);
        Organization organization2 = new Organization();
        expected.add(organization2);

        Datastore.put(payment, organization1, organization2);

        ActivityPaymentWorkflow wfl1 = new ActivityPaymentWorkflow();
        wfl1.getPaymentRef().setModel(payment);
        wfl1.setSubscriptionId(Datastore.allocateId(Subscription.class));
        ActivityPackagePaymentWorkflow wfl2 = new ActivityPackagePaymentWorkflow();
        wfl2.getPaymentRef().setModel(payment);
        wfl2.setActivityPackageExecutionId(Datastore.allocateId(ActivityPackageExecution.class));
        Subscription s1 = new Subscription();
        s1.setId(wfl1.getSubscriptionId());
        ActivityPackageExecution e1 = new ActivityPackageExecution();
        e1.setId(wfl2.getActivityPackageExecutionId());

        ActivityType at1 = new ActivityType();
        at1.getOrganizationRef().setModel(organization1);
        Activity a1 = new Activity();
        a1.getActivityTypeRef().setModel(at1);
        s1.getActivityRef().setModel(a1);

        ActivityPackage ap1 = new ActivityPackage();
        ap1.getOrganizationRef().setModel(organization2);
        e1.getActivityPackageRef().setModel(ap1);
        Datastore.put(wfl1, wfl2, s1, e1, at1, a1, ap1);

        List<Organization> organizations = Navigate.organizations(payment);
        assertEqualsList(expected, organizations);
    }

    @Test
    public void testSubscriptionsFromPayment() {
        Payment payment = new Payment();
        ActivityPaymentWorkflow wfl1 = new ActivityPaymentWorkflow();
        wfl1.getPaymentRef().setModel(payment);
        ActivityPaymentWorkflow wfl2 = new ActivityPaymentWorkflow();
        wfl2.getPaymentRef().setModel(payment);
        List<Subscription> expected = new ArrayList<>();
        Subscription s1 = new Subscription();
        expected.add(s1);
        Subscription s2 = new Subscription();
        expected.add(s2);
        Datastore.put(s1, s2);
        wfl1.setSubscriptionId(s1.getId());
        wfl2.setSubscriptionId(s2.getId());

        Datastore.put(payment, wfl1, wfl2, s1, s2);

        List<Subscription> subscriptions = Navigate.subscriptions(payment);

        assertEqualsList(expected, subscriptions);
    }

    @Test
    public void testActivityPackageExecutionsFromPayment() {
        Payment payment = new Payment();
        ActivityPackagePaymentWorkflow wfl1 = new ActivityPackagePaymentWorkflow();
        wfl1.getPaymentRef().setModel(payment);
        ActivityPackagePaymentWorkflow wfl2 = new ActivityPackagePaymentWorkflow();
        wfl2.getPaymentRef().setModel(payment);
        List<ActivityPackageExecution> expected = new ArrayList<>();
        ActivityPackageExecution s1 = new ActivityPackageExecution();
        expected.add(s1);
        ActivityPackageExecution s2 = new ActivityPackageExecution();
        expected.add(s2);
        Datastore.put(s1, s2);
        wfl1.setActivityPackageExecutionId(s1.getId());
        wfl2.setActivityPackageExecutionId(s2.getId());
        Datastore.put(payment, wfl1, wfl2, s1, s2);

        List<ActivityPackageExecution> subscriptions = Navigate.activityPackageExecutions(payment);

        assertEqualsList(expected, subscriptions);
    }
}