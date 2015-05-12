package com.jasify.schedule.appengine.mail;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.Collections;

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

        this.activity1 = createActivity(this.activityType, 20.0, new DateTime(2015, 3, 30, 12, 0), new DateTime(2015, 3, 30, 12, 45));
        this.subscription1 = createSubscription(this.user, this.activity1);

        this.activity2 = createActivity(this.activityType, 19.75, new DateTime(2015, 3, 15, 12, 0), new DateTime(2015, 3, 15, 14, 0));
        this.subscription2 = createSubscription(this.user, this.activity2);
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
        MailParser mailParser = MailParser.createJasifySubscriptionEmail(Arrays.asList(subscription1, subscription2), Collections.<ActivityPackageExecution>emptyList());
        String text = mailParser.getText();

        assert (text.contains("A new subscription has been created for " + user.getRealName()));

        assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (text.contains("Publisher     : " + organization.getName()));
        assert (text.contains("Activity      : " + activity1.getName()));
        assert (text.contains("Start         : " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (text.contains("Finish        : " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (text.contains("Price         : " + activity1.getPrice()));

        assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription2.getId())));
        assert (text.contains("Publisher     : " + organization.getName()));
        assert (text.contains("Activity      : " + activity2.getName()));
        assert (text.contains("Start         : " + new DateTime(activity2.getStart()).toString(dtf)));
        assert (text.contains("Finish        : " + new DateTime(activity2.getFinish()).toString(dtf)));
        assert (text.contains("Price         : " + activity2.getPrice()));

        assert (text.contains("Total         : " + MailParser.formatPrice(activity1.getPrice() + activity2.getPrice())));
    }

    @Test
    public void testJasifySubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail(Arrays.asList(subscription1, subscription2), Collections.<ActivityPackageExecution>emptyList());
        String html = mailParser.getHtml();

        assert (html.contains("A new subscription has been created for " + user.getRealName()));

        assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (html.contains(": " + organization.getName()));
        assert (html.contains(": " + activity1.getName()));
        assert (html.contains(": " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (html.contains(": " + activity1.getPrice()));

        assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription2.getId())));
        assert (html.contains(": " + organization.getName()));
        assert (html.contains(": " + activity2.getName()));
        assert (html.contains(": " + new DateTime(activity2.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity2.getFinish()).toString(dtf)));
        assert (html.contains(": " + activity2.getPrice()));

        assert (html.contains(": " + MailParser.formatPrice(activity1.getPrice() + activity2.getPrice())));
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
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(Arrays.asList(new Subscription[]{subscription1, subscription2}), Collections.<ActivityPackageExecution>emptyList());
        String text = mailParser.getText();

        assert (text.contains("Dear " + user.getRealName()));
        assert (text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (text.contains("Purchased From: " + organization.getName()));
        assert (text.contains("Activity      : " + activity1.getName()));
        assert (text.contains("Start         : " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (text.contains("Finish        : " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (text.contains("Price         : " + activity1.getPrice()));

        assert (text.contains(": " + KeyUtil.toHumanReadableString(subscription2.getId())));
        assert (text.contains(": " + organization.getName()));
        assert (text.contains(": " + activity2.getName()));
        assert (text.contains(": " + new DateTime(activity2.getStart()).toString(dtf)));
        assert (text.contains(": " + new DateTime(activity2.getFinish()).toString(dtf)));
        assert (text.contains(": " + activity2.getPrice()));

        assert (text.contains(": " + MailParser.formatPrice(activity1.getPrice() + activity2.getPrice())));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(Arrays.asList(new Subscription[]{subscription1, subscription2}), Collections.<ActivityPackageExecution>emptyList());
        String html = mailParser.getHtml();

        assert (html.contains("Dear " + user.getRealName()));
        assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription1.getId())));
        assert (html.contains(": " + organization.getName()));
        assert (html.contains(": " + activity1.getName()));
        assert (html.contains(": " + new DateTime(activity1.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity1.getFinish()).toString(dtf)));
        assert (html.contains(": " + activity1.getPrice()));

        assert (html.contains(": " + KeyUtil.toHumanReadableString(subscription2.getId())));
        assert (html.contains(": " + organization.getName()));
        assert (html.contains(": " + activity2.getName()));
        assert (html.contains(": " + new DateTime(activity2.getStart()).toString(dtf)));
        assert (html.contains(": " + new DateTime(activity2.getFinish()).toString(dtf)));
        assert (html.contains(": " + activity2.getPrice()));

        assert (html.contains(": " + MailParser.formatPrice(activity1.getPrice() + activity2.getPrice())));
    }

    @Test
    public void testPublisherSubscriptionEmailAsText() throws Exception {
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
    public void testPublisherSubscriptionEmailAsHtml() throws Exception {
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
