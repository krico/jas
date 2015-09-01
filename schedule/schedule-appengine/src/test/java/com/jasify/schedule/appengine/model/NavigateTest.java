package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.*;

public class NavigateTest {
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


}