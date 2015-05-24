package com.jasify.schedule.appengine.mail;

import com.google.appengine.repackaged.org.joda.time.DateTime;
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

import static junit.framework.TestCase.*;

import java.util.*;

/**
 * @author wszarmach
 * @since 14/02/15.
 */
public class MailParserTest {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

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
        return activity;
    }

    private Subscription createSubscription(User user, Activity activity) {
        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(user);
        subscription.setId(Datastore.allocateId(Subscription.class));
        return subscription;
    }

    private ActivityPackage createActivityPackage(ActivityType activityType, List<Activity> activities) {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setModel(activityType.getOrganizationRef().getModel());
        activityPackage.setPrice(random(20.0, 25.0));
        activityPackage.setItemCount(2);
        activityPackage.setName("Squash Bundle");
        activityPackage.setCurrency("CHF");
        for (Activity activity : activities) {
            activityPackage.getActivities().add(activity);
        }
        return activityPackage;
    }

    private ActivityPackageSubscription createActivityPackageSubscription(User user, Activity activity) {
        ActivityPackageSubscription activityPackageSubscription = new ActivityPackageSubscription();
        activityPackageSubscription.getUserRef().setModel(user);
        activityPackageSubscription.getActivityRef().setModel(activity);
        return activityPackageSubscription;
    }

    private ActivityPackageExecution createActivityPackageExecution(User user, ActivityType activityType) {
        List<Activity> activities = new ArrayList<>();
        activities.add(createActivity(activityType, random(10.0, 20.0), new DateTime(2015, 4, 15, 13, 0), new DateTime(2015, 4, 15, 14, 0)));
        activities.add(createActivity(activityType, random(10.0, 20.0), new DateTime(2015, 4, 16, 13, 0), new DateTime(2015, 4, 16, 14, 0)));
        ActivityPackage activityPackage = createActivityPackage(activityType, activities);

        ActivityPackageExecution activityPackageExecution = new ActivityPackageExecution();
        activityPackageExecution.getActivityPackageRef().setModel(activityPackage);
        activityPackageExecution.getUserRef().setModel(user);
        activityPackageExecution.setId(Datastore.allocateId(ActivityPackageExecution.class));

        for (Activity activity :  activities) {
            ActivityPackageSubscription activityPackageSubscription = createActivityPackageSubscription(user, activity);
            activityPackageSubscription.getActivityPackageExecutionRef().setModel(activityPackageExecution);
            activityPackageExecution.getSubscriptionListRef().getModelList().add(activityPackageSubscription);
        }
        return activityPackageExecution;
    }

    private double round(double value, int places) {
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double random(double min, double max)
    {
        double diff = max - min;
        return round(min + Math.random( ) * diff, 2);
    }

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        this.user = new User();
        this.user.setName("Fred");
        this.user.setRealName("James BlaBla");
        this.user.setId(Datastore.allocateId(User.class));

        this.organization = new Organization("Super Squash Courts");

        this.activityType = new ActivityType("Squash");
        this.activityType.getOrganizationRef().setModel(this.organization);

        this.activity1 = createActivity(this.activityType, random(10.0, 20.0), new DateTime(2015, 3, 30, 12, 0), new DateTime(2015, 3, 30, 12, 45));
        this.subscription1 = createSubscription(this.user, this.activity1);

        this.activity2 = createActivity(this.activityType, random(10.0, 20.0), new DateTime(2015, 3, 15, 12, 0), new DateTime(2015, 3, 15, 14, 0));
        this.subscription2 = createSubscription(this.user, this.activity2);

        this.activityPackageExecution1 = createActivityPackageExecution(this.user, this.activityType);
        this.activityPackageExecution2 = createActivityPackageExecution(this.user, this.activityType);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test(expected = NullPointerException.class)
    public void testNullSubstitution() throws Exception {
        MailParser.createSubscriberPasswordRecoveryEmail(null);
    }

    @Test
    public void testDecimalFormatting() throws Exception {
        this.activity1.setPrice(20.0000001);
        assert (MailParser.createPublisherSubscriptionEmail(subscription1).getText().contains("20.00"));

        this.activity1.setPrice(1.234444);
        assert (MailParser.createPublisherSubscriptionEmail(subscription1).getText().contains("1.23"));

        this.activity1.setPrice(0.12345);
        assert (MailParser.createPublisherSubscriptionEmail(subscription1).getText().contains("0.12"));
    }

    @Test
    public void testUserNameFallback() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription1);
        User user = subscription1.getUserRef().getModel();
        String text = mailParser.getText();
        assert (text.contains("Subscriber    : " + user.getRealName()));
        assert (!text.contains("Subscriber    : " + user.getName()));
        user.setRealName(null);
        mailParser = MailParser.createPublisherSubscriptionEmail(subscription1);
        text = mailParser.getText();
        assert (!text.contains("Subscriber    : " + user.getRealName()));
        assert (text.contains("Subscriber    : " + user.getName()));
    }

    @Test
    public void testJasifyUserSignUpEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail(user);
        String text = mailParser.getText();

        assert (text.contains("Name    : " + user.getRealName()));
        assert (text.contains("Username: " + user.getName()));
    }

    @Test
    public void testJasifyUserSignUpEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail(user);
        String html = mailParser.getHtml();

        assert (html.contains(user.getRealName()));
        assert (html.contains(user.getName()));
    }

    @Test
    public void testJasifySubscriptionEmailAsText() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1,activityPackageExecution2);
        MailParser mailParser = MailParser.createJasifySubscriptionEmail(subscriptions, activityPackageExecutions);
        String text = mailParser.getText();

        double totalPrice = 0;
        assert (text.contains("A new subscription has been created for " + user.getRealName()));

        for (Subscription subscription : subscriptions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
            Activity activity = subscription.getActivityRef().getModel();
            ActivityType activityType = activity.getActivityTypeRef().getModel();
            Organization organization = activityType.getOrganizationRef().getModel();
            assert (text.contains("Publisher     : " + organization.getName()));
            assert (text.contains("Activity      : " + activity.getName()));
            assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
            assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
            assert (text.contains("Price         : " + activity.getPrice()));
            totalPrice += activity.getPrice();
        }

        for(ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(activityPackageExecution.getId())));
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            Organization organization = activityPackage.getOrganizationRef().getModel();
            assert (text.contains("Publisher     : " + organization.getName()));
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
    public void testJasifySubscriptionEmailAsHtml() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1,activityPackageExecution2);

        MailParser mailParser = MailParser.createJasifySubscriptionEmail(subscriptions, activityPackageExecutions);
        String html = mailParser.getHtml();

        double totalPrice = 0;
        assert (html.contains("A new subscription has been created for " + user.getRealName()));

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

        for(ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
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
    public void testSubscriberPasswordRecoveryAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberPasswordRecoveryEmail("PasswordRecoveryUrl");
        String text = mailParser.getText();

        assert (text.contains("Click on the link below to reset your password: PasswordRecoveryUrl"));
    }

    @Test
    public void testSubscriberPasswordRecoveryAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberPasswordRecoveryEmail("PasswordRecoveryUrl");
        String html = mailParser.getHtml();

        assert (html.contains("PasswordRecoveryUrl"));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsText() throws Exception {
        List<Subscription> subscriptions = Arrays.asList(subscription1, subscription2);
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1,activityPackageExecution2);
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

        for(ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
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
        List<ActivityPackageExecution> activityPackageExecutions = Arrays.asList(activityPackageExecution1,activityPackageExecution2);

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

        for(ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
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
    public void testPublisherActivitySubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription1);
        String text = mailParser.getText();

        assert (text.contains("Dear " + organization.getName()));
        assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (text.contains("Subscriber    : " + user.getRealName()));
        assert (text.contains("Activity      : " + activity1.getName()));
        assert (text.contains("Start         : " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (text.contains("Finish        : " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (text.contains("Price         : " + activity1.getPrice()));
    }

    @Test
    public void testPublisherActivitySubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription1);
        String html = mailParser.getHtml();

        assert (html.contains("Order of Squash"));
        assert (html.contains("Dear " + organization.getName()));
        assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (html.contains(": " + user.getRealName()));
        assert (html.contains(": " + activity1.getName()));
        assert (html.contains(": " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (html.contains(": " + activity1.getPrice()));
    }

    @Test
    public void testPublisherActivityPackageSubscriptionEmailAsText() throws Exception {
        ActivityPackageSubscription activityPackageSubscription = activityPackageExecution1.getSubscriptionListRef().getModelList().get(0);
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(activityPackageSubscription);
        String text = mailParser.getText();
        User user = activityPackageExecution1.getUserRef().getModel();
        Activity activity = activityPackageSubscription.getActivityRef().getModel();
        ActivityPackage activityPackage = activityPackageExecution1.getActivityPackageRef().getModel();

        assert (text.contains("Dear " + organization.getName()));
        assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(activityPackageExecution1.getId())));
        assert (text.contains("Subscriber    : " + user.getRealName()));
        assert (text.contains("Activity      : " + activity.getName() + " [" + activityPackage.getName() + "]"));
        assert (text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
        assert (text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
        assert (text.contains("Price         : " + activityPackage.getPrice()));
    }

    @Test
    public void testPublisherActivityPackageSubscriptionEmailAsHtml() throws Exception {
        ActivityPackageSubscription activityPackageSubscription = activityPackageExecution1.getSubscriptionListRef().getModelList().get(0);
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(activityPackageSubscription);
        String html = mailParser.getHtml();
        User user = activityPackageExecution1.getUserRef().getModel();
        Activity activity = activityPackageSubscription.getActivityRef().getModel();
        ActivityPackage activityPackage = activityPackageExecution1.getActivityPackageRef().getModel();

        assert (html.contains("Order of Squash"));
        assert (html.contains("Dear " + organization.getName()));
        assert (html.contains(": " + KeyUtil.toHumanReadableString(activityPackageExecution1.getId())));
        assert (html.contains(": " + user.getRealName()));
        assert (html.contains(": " + activity.getName() + " [" + activityPackage.getName() + "]"));
        assert (html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
        assert (html.contains(": " + activityPackage.getPrice()));
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

    @Test
    public  void testNewVersionEmail() throws Exception {
        MailParser mailParser = MailParser.createNewVersionEmail("Beta", "222", "ABC", "#2", "https://URL");
        String text = mailParser.getText();

        assert (text.contains("Version    : Beta"));
        assert (text.contains("Timestamp  : 222"));
        assert (text.contains("Branch     : ABC"));
        assert (text.contains("Number     : #2"));
        assert (text.contains("Try it out at: https://URL"));
    }
}
