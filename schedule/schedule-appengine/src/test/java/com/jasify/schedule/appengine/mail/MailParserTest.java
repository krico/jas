package com.jasify.schedule.appengine.mail;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTimeZone;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wszarmach
 * @since 14/02/15.
 */
public class MailParserTest {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").withZone(DateTimeZone.forID("Europe/Zurich"));

    User user;
    ActivityType activityType;
    Activity activity1;
    Activity activity2;
    Organization organization;
    Subscription subscription1;
    Subscription subscription2;
    ActivityPackageExecution activityPackageExecution1;
    ActivityPackageExecution activityPackageExecution2;

    private Activity createActivity(ActivityType activityType, double price, DateTime start, DateTime finish) {
        Activity activity = new Activity(activityType);
        activity.setPrice(price);
        activity.setStart(start.toDate());
        activity.setFinish(finish.toDate());
        Datastore.put(activity);
        return activity;
    }

    private Subscription createSubscription(User user, Activity activity) {
        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(user);
        Datastore.put(subscription);
        return subscription;
    }

    private ActivityPackage createActivityPackage(ActivityType activityType) {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setModel(activityType.getOrganizationRef().getModel());
        activityPackage.setPrice(random(20.0, 25.0));
        activityPackage.setItemCount(2);
        activityPackage.setName("Squash Bundle");
        activityPackage.setCurrency("CHF");
        Datastore.put(activityPackage);
        return activityPackage;
    }

    private ActivityPackageSubscription createActivityPackageSubscription(User user, Activity activity) {
        ActivityPackageSubscription activityPackageSubscription = new ActivityPackageSubscription();
        activityPackageSubscription.getUserRef().setModel(user);
        activityPackageSubscription.getActivityRef().setModel(activity);
        Datastore.put(activityPackageSubscription);
        return activityPackageSubscription;
    }

    private ActivityPackageExecution createActivityPackageExecution(User user, ActivityType activityType) {
        ActivityPackage activityPackage = createActivityPackage(activityType);

        ActivityPackageExecution activityPackageExecution = new ActivityPackageExecution();
        activityPackageExecution.getActivityPackageRef().setModel(activityPackage);
        activityPackageExecution.getUserRef().setModel(user);
        activityPackageExecution.setId(Datastore.allocateId(ActivityPackageExecution.class));

        List<Activity> activities = new ArrayList<>();
        activities.add(createActivity(activityType, random(10.0, 20.0), new DateTime(2015, 4, 15, 13, 0), new DateTime(2015, 4, 15, 14, 0)));
        activities.add(createActivity(activityType, random(10.0, 20.0), new DateTime(2015, 4, 16, 13, 0), new DateTime(2015, 4, 16, 14, 0)));

        for (Activity activity : activities) {
            ActivityPackageSubscription activityPackageSubscription = createActivityPackageSubscription(user, activity);
            activityPackageSubscription.getActivityPackageExecutionRef().setModel(activityPackageExecution);
            activityPackageExecution.getSubscriptionListRef().getModelList().add(activityPackageSubscription);
        }
        Datastore.put(activityPackageExecution);
        return activityPackageExecution;
    }

    private double round(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double random(double min, double max) {
        double diff = max - min;
        return round(min + Math.random() * diff, 2);
    }

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        this.user = TestHelper.createUser(true);

        this.organization = TestHelper.createOrganization(true);
        this.activityType = TestHelper.createActivityType(organization, true);

        this.activity1 = createActivity(this.activityType, random(10.0, 20.0), new DateTime(2015, 3, 30, 12, 0, DateTimeZone.UTC), new DateTime(2015, 3, 30, 12, 45, DateTimeZone.UTC));
        this.subscription1 = createSubscription(this.user, this.activity1);

        this.activity2 = createActivity(this.activityType, random(10.0, 20.0), new DateTime(2015, 3, 15, 12, 0, DateTimeZone.UTC), new DateTime(2015, 3, 15, 14, 0, DateTimeZone.UTC));
        this.subscription2 = createSubscription(this.user, this.activity2);

        this.activityPackageExecution1 = createActivityPackageExecution(this.user, this.activityType);
        this.activityPackageExecution2 = createActivityPackageExecution(this.user, this.activityType);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testDecimalFormatting() throws Exception {
        this.activity1.setPrice(20.0000001);
        Datastore.put(this.activity1);
        MailParser mailParser1 = MailParser.createPublisherSubscriptionEmail(Arrays.asList(subscription1), Collections.<ActivityPackageExecution>emptyList());
        assert (mailParser1.getText().contains("20.00"));

        this.activity1.setPrice(1.234444);
        Datastore.put(this.activity1);
        MailParser mailParser2 = MailParser.createPublisherSubscriptionEmail(Arrays.asList(subscription1), Collections.<ActivityPackageExecution>emptyList());
        assert (mailParser2.getText().contains("1.23"));

        this.activity1.setPrice(0.12345);
        Datastore.put(this.activity1);
        MailParser mailParser3 = MailParser.createPublisherSubscriptionEmail(Arrays.asList(subscription1), Collections.<ActivityPackageExecution>emptyList());
        assert (mailParser3.getText().contains("0.12"));
    }

    @Test
    public void testUserNameFallback() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(Arrays.asList(subscription1), Collections.<ActivityPackageExecution>emptyList());
        User user = subscription1.getUserRef().getModel();
        String text = mailParser.getText();
        assert (text.contains("Subscriber    : " + user.getRealName()));
        assert (!text.contains("Subscriber    : " + user.getName()));
        user.setRealName(null);
        Datastore.put(user);
        mailParser = MailParser.createPublisherSubscriptionEmail(Arrays.asList(subscription1), Collections.<ActivityPackageExecution>emptyList());
        text = mailParser.getText();
        assert (!text.contains("Subscriber    : " + user.getRealName()));
        assert (text.contains("Subscriber    : " + user.getName()));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsText() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscriptions, activityPackageExecutions);
        String text = mailParser.getText();

        double totalPrice = 0;
        assert (text.contains("Dear " + user.getRealName()));

        for (Subscription subscription : subscriptions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            ActivityType activityType = activity.getActivityTypeRef().getModel();
            Organization organization = activityType.getOrganizationRef().getModel();
            assert (text.contains("Purchased From: " + organization.getName()));
            assert (text.contains("Activity      : " + activity.getName()));
            assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
            assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (text.contains("Price         : " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            Organization organization = activityPackage.getOrganizationRef().getModel();
            assert (text.contains("Purchased From: " + organization.getName()));
            assert (text.contains("Package       : " + activityPackage.getName()));
            assert (text.contains("Price         : " + activityPackage.getPrice()));
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
                Activity activity = activityPackageSubscription.getActivityRef().getModel();
                assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
                assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            }
            totalPrice += activityPackage.getPrice();
        }

        assert (text.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsHtml() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);

        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscriptions, activityPackageExecutions);
        String html = mailParser.getHtml();

        assert (html.contains("Dear " + user.getRealName()));
        double totalPrice = 0;

        for (Subscription subscription : subscriptions) {
            assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            ActivityType activityType = activity.getActivityTypeRef().getModel();
            Organization organization = activityType.getOrganizationRef().getModel();
            assert (html.contains(": " + organization.getName()));
            assert (html.contains(": " + activity.getName()));
            assert (html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
            assert (html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (html.contains(": " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (html.contains(": " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            Organization organization = activityPackage.getOrganizationRef().getModel();
            assert (html.contains(": " + organization.getName()));
            assert (html.contains(": " + activityPackage.getName()));
            assert (html.contains(": " + activityPackage.getPrice()));
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
                Activity activity = activityPackageSubscription.getActivityRef().getModel();
                assert (html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
                assert (html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
            }
            totalPrice += activityPackage.getPrice();
        }

        assert (html.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test
    public void testPublisherSubscriptionEmailAsText() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscriptions, activityPackageExecutions);
        String text = mailParser.getText();

        double totalPrice = 0;
        assert (text.contains("Dear " + organization.getName()));
        assert (text.contains("Subscriber    : " + user.getDisplayName()));

        for (Subscription subscription : subscriptions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            assert (text.contains("Activity      : " + activity.getName()));
            assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
            assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (text.contains("Price         : " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            assert (text.contains("Package       : " + activityPackage.getName()));
            assert (text.contains("Price         : " + activityPackage.getPrice()));
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
                Activity activity = activityPackageSubscription.getActivityRef().getModel();
                assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
                assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            }
            totalPrice += activityPackage.getPrice();
        }

        assert (text.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test(expected = Exception.class)
    public void testPublisherSubscriptionEmailThrowsIfNoOrganisationNameFound() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);
        organization.setName(null);
        Datastore.put(organization);
        MailParser.createPublisherSubscriptionEmail(subscriptions, activityPackageExecutions);
    }

    @Test
    public void testPublisherSubscriptionEmailForSubscriptionOnly() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscriptions, Collections.<ActivityPackageExecution>emptyList());
        String text = mailParser.getText();

        double totalPrice = 0;
        assert (text.contains("Dear " + organization.getName()));
        assert (text.contains("Subscriber    : " + user.getDisplayName()));

        for (Subscription subscription : subscriptions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            assert (text.contains("Activity      : " + activity.getName()));
            assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
            assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (text.contains("Price         : " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        assert (text.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test
    public void testPublisherSubscriptionEmailForActivityPackageOnly() throws Exception {
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(Collections.<Subscription>emptyList(), activityPackageExecutions);
        String text = mailParser.getText();

        double totalPrice = 0;
        assert (text.contains("Dear " + organization.getName()));
        assert (text.contains("Subscriber    : " + user.getDisplayName()));

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            assert (text.contains("Package       : " + activityPackage.getName()));
            assert (text.contains("Price         : " + activityPackage.getPrice()));
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
                Activity activity = activityPackageSubscription.getActivityRef().getModel();
                assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
                assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            }
            totalPrice += activityPackage.getPrice();
        }

        assert (text.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test
    public void testPublisherSubscriptionEmailAsHtml() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1, activityPackageExecution2);

        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscriptions, activityPackageExecutions);
        String html = mailParser.getHtml();

        assert (html.contains("Dear " + organization.getName()));
        assert (html.contains(": " + user.getDisplayName()));
        double totalPrice = 0;

        for (Subscription subscription : subscriptions) {
            assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            assert (html.contains(": " + activity.getName()));
            assert (html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
            assert (html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (html.contains(": " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (html.contains(": " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            assert (html.contains(": " + activityPackage.getName()));
            assert (html.contains(": " + activityPackage.getPrice()));
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
                Activity activity = activityPackageSubscription.getActivityRef().getModel();
                assert (html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
                assert (html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
            }
            totalPrice += activityPackage.getPrice();
        }

        assert (html.contains(": " + MailParser.formatPrice(totalPrice)));
    }

    @Test
    public void testCreateSubscriberEmailSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberEmailSignUpConfirmationEmail("Sam BlaBla", "daBoss", "MagicUrl");
        String text = mailParser.getText();

        assert (text.contains("Dear Sam BlaBla"));
        assert (text.contains("Your username is: daBoss"));
        assert (text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberEmailSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberEmailSignUpConfirmationEmail("Sam BlaBla", "daBoss", "MagicUrl");
        String html = mailParser.getHtml();

        assert (html.contains("Dear Sam BlaBla"));
        assert (html.contains("daBoss"));
        assert (html.contains("MagicUrl"));
    }

    @Test
    public void testCreateSubscriberFacebookSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberFacebookSignUpConfirmationEmail("John BlaBla", "MagicUrl");
        String text = mailParser.getText();

        assert (text.contains("Dear John BlaBla"));
        assert (text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberFacebookSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberFacebookSignUpConfirmationEmail("John BlaBla", "MagicUrl");
        String html = mailParser.getHtml();

        assert (html.contains("Dear John BlaBla"));
        assert (html.contains("MagicUrl"));
    }

    @Test
    public void testCreateSubscriberGoogleSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberGoogleSignUpConfirmationEmail("Fred BlaBla", "MagicUrl");
        String text = mailParser.getText();

        assert (text.contains("Dear Fred BlaBla"));
        assert (text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberGoogleSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberGoogleSignUpConfirmationEmail("Fred BlaBla", "MagicUrl");
        String html = mailParser.getHtml();

        assert (html.contains("Dear Fred BlaBla"));
        assert (html.contains("MagicUrl"));
    }
}
