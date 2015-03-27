package com.jasify.schedule.appengine.mail;

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

/**
 * @author wszarmach
 * @since 14/02/15.
 */
public class MailParserTest {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

    User user;
    ActivityType activityType;
    Activity activity;
    Organization organization;
    Subscription subscription;

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        this.user = new User();
        this.user.setName("Fred");
        this.user.setRealName("James BlaBla");
        this.user.setId(Datastore.allocateId(User.class));
        this.organization = new Organization("Super Squash Courts");
        this.activityType = new ActivityType("Squash");
        this.activity = new Activity(this.activityType);
        this.activity.setPrice(20.0);
        this.activity.setStart(new DateTime(2015, 3, 31, 12, 0).toDate());
        this.activity.setFinish(new DateTime(2015, 3, 31, 13, 0).toDate());
        this.subscription = new Subscription();
        this.subscription.getActivityRef().setModel(activity);
        this.subscription.getUserRef().setModel(user);
        this.subscription.setId(Datastore.allocateId(Subscription.class));
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
        this.activity.setPrice(20.0000001);
        assert(MailParser.createJasifySubscriptionEmail(subscription, organization).getText().contains("20.00"));

        this.activity.setPrice(1.234444);
        assert(MailParser.createJasifySubscriptionEmail(subscription, organization).getText().contains("1.23"));

        this.activity.setPrice(0.12345);
        assert(MailParser.createJasifySubscriptionEmail(subscription, organization).getText().contains("0.12"));
    }

    @Test
    public void testJasifyUserSignUpEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail(user);
        String text = mailParser.getText();

        assert(text.contains("Name    : " + user.getRealName()));
        assert(text.contains("Username: " + user.getName()));
    }

    @Test
    public void testJasifyUserSignUpEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail(user);
        String html = mailParser.getHtml();

        assert(html.contains(user.getRealName()));
        assert(html.contains(user.getName()));
    }

    @Test
    public void testJasifySubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail(subscription, organization);
        String text = mailParser.getText();

        assert(text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(text.contains("Publisher     : " + organization.getName()));
        assert(text.contains("Subscriber    : " + user.getRealName()));
        assert(text.contains("Activity      : " + activity.getName()));
        assert(text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
        assert(text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(text.contains("Price         : " + activity.getPrice()));
    }

    @Test
    public void testJasifySubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail(subscription, organization);
        String html = mailParser.getHtml();

        assert(html.contains(": " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(html.contains(": " + organization.getName()));
        assert(html.contains(": " + user.getRealName()));
        assert(html.contains(": " + activity.getName()));
        assert(html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
        assert(html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(html.contains(": " + activity.getPrice()));
    }

    @Test
    public void testSubscriberPasswordRecoveryAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberPasswordRecoveryEmail("PasswordRecoveryUrl");
        String text = mailParser.getText();

        assert(text.contains("Click on the link below to reset your password: PasswordRecoveryUrl"));
    }

    @Test
    public void testSubscriberPasswordRecoveryAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberPasswordRecoveryEmail("PasswordRecoveryUrl");
        String html = mailParser.getHtml();

        assert(html.contains("PasswordRecoveryUrl"));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscription, organization);
        String text = mailParser.getText();

        assert(text.contains("Dear " + user.getRealName()));
        assert(text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(text.contains("Purchased From: " + organization.getName()));
        assert(text.contains("Activity      : " + activity.getName()));
        assert(text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
        assert(text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(text.contains("Price         : " + activity.getPrice()));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail(subscription, organization);
        String html = mailParser.getHtml();

        assert(html.contains("Order of Squash"));
        assert(html.contains("Dear " + user.getRealName()));
        assert(html.contains(": " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(html.contains(": " + organization.getName()));
        assert(html.contains(": " + activity.getName()));
        assert(html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
        assert(html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(html.contains(": " + activity.getPrice()));
    }

    @Test
    public void testPublisherSubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription, organization);
        String text = mailParser.getText();

        assert(text.contains("Dear " + organization.getName()));
        assert(text.contains("Order        #: " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(text.contains("Subscriber    : " + user.getRealName()));
        assert(text.contains("Activity      : " + activity.getName()));
        assert(text.contains("Start         : " + new DateTime(activity.getStart()).toString(dtf)));
        assert(text.contains("Finish        : " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(text.contains("Price         : " + activity.getPrice()));
    }

    @Test
    public void testPublisherSubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail(subscription, organization);
        String html = mailParser.getHtml();

        assert(html.contains("Order of Squash"));
        assert(html.contains("Dear " + organization.getName()));
        assert(html.contains(": " + KeyUtil.toHumanReadableString(subscription.getId())));
        assert(html.contains(": " + user.getRealName()));
        assert(html.contains(": " + activity.getName()));
        assert(html.contains(": " + new DateTime(activity.getStart()).toString(dtf)));
        assert(html.contains(": " + new DateTime(activity.getFinish()).toString(dtf)));
        assert(html.contains(": " + activity.getPrice()));
    }

    @Test
    public void testCreateSubscriberEmailSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberEmailSignUpConfirmationEmail("Sam BlaBla", "daBoss", "MagicUrl");
        String text = mailParser.getText();

        assert(text.contains("Dear Sam BlaBla"));
        assert(text.contains("Your username is: daBoss"));
        assert(text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberEmailSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberEmailSignUpConfirmationEmail("Sam BlaBla", "daBoss", "MagicUrl");
        String html = mailParser.getHtml();

        assert(html.contains("Dear Sam BlaBla"));
        assert(html.contains("daBoss"));
        assert(html.contains("MagicUrl"));
    }

    @Test
    public void testCreateSubscriberFacebookSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberFacebookSignUpConfirmationEmail("John BlaBla", "MagicUrl");
        String text = mailParser.getText();

        assert(text.contains("Dear John BlaBla"));
        assert(text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberFacebookSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberFacebookSignUpConfirmationEmail("John BlaBla", "MagicUrl");
        String html = mailParser.getHtml();

        assert(html.contains("Dear John BlaBla"));
        assert(html.contains("MagicUrl"));
    }

    @Test
    public void testCreateSubscriberGoogleSignUpConfirmationEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createSubscriberGoogleSignUpConfirmationEmail("Fred BlaBla", "MagicUrl");
        String text = mailParser.getText();

        assert(text.contains("Dear Fred BlaBla"));
        assert(text.contains(">> Sign In at: MagicUrl <<"));
    }

    @Test
    public void testCreateSubscriberGoogleSignUpConfirmationEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberGoogleSignUpConfirmationEmail("Fred BlaBla", "MagicUrl");
        String html = mailParser.getHtml();

        assert(html.contains("Dear Fred BlaBla"));
        assert(html.contains("MagicUrl"));
    }
}
