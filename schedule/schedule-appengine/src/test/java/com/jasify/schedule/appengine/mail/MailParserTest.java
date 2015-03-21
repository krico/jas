package com.jasify.schedule.appengine.mail;

import org.junit.Test;

/**
 * @author wszarmach
 * @since 14/02/15.
 */
public class MailParserTest {

    @Test(expected = NullPointerException.class)
    public void testNullSubstitution() throws Exception {
        MailParser.createSubscriberPasswordRecoveryEmail(null);
    }

    @Test
    public void testDecimalFormatting() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0000001, 1.234444, 0.12345, "Credit Card");
        String text = mailParser.getText();
        assert(text.contains("20.00"));
        assert(text.contains("1.23"));
        assert(text.contains("0.12"));
        assert(text.contains("21.35"));
    }

    @Test
    public void testJasifyUserSignUpEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail("James Smith", "Fred");
        String text = mailParser.getText();
        assert(text.contains("Name    : James Smith"));
        assert(text.contains("Username: Fred"));
    }

    @Test
    public void testJasifyUserSignUpEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifyUserSignUpEmail("James Smith", "Fred");
        String html = mailParser.getHtml();
        assert(html.contains("James Smith"));
        assert(html.contains("Fred"));
    }

    @Test
    public void testJasifySubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0, 1.23, 0.12, "Credit Card");
        String text = mailParser.getText();
        assert(text.contains("Super Squash Courts"));
        assert(text.contains("James BlaBla"));
        assert(text.contains("ABC123"));
        assert(text.contains("20.00"));
        assert(text.contains("1.23"));
        assert(text.contains("0.12"));
        assert(text.contains("21.35"));
        assert(text.contains("Credit Card"));
    }

    @Test
    public void testJasifySubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createJasifySubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0, 1.23, 0.12, "Credit Card");
        String html = mailParser.getHtml();
        assert(html.contains("Super Squash Courts"));
        assert(html.contains("James BlaBla"));
        assert(html.contains("ABC123"));
        assert(html.contains("20.00"));
        assert(html.contains("1.23"));
        assert(html.contains("0.12"));
        assert(html.contains("21.35"));
        assert(html.contains("Credit Card"));
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
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0);
        String text = mailParser.getText();
        assert(text.contains("Thank you for your order, Squash!"));
        assert(text.contains("Dear James BlaBla"));
        assert(text.contains("Purchased From: Super Squash Courts"));
        assert(text.contains("Order        #: ABC123"));
        assert(text.contains("Price         : 20.00"));
    }

    @Test
    public void testSubscriberSubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createSubscriberSubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0);
        String html = mailParser.getHtml();
        assert(html.contains("Order of Squash"));
        assert(html.contains("Thank you for your order, Squash!"));
        assert(html.contains("Dear James BlaBla"));
        assert(html.contains("Purchased From: Super Squash Courts"));
        assert(html.contains("ABC123"));
        assert(html.contains("20.00"));
    }

    @Test
    public void testPublisherSubscriptionEmailAsText() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0);
        String text = mailParser.getText();
        assert(text.contains("Dear Super Squash Courts"));
        assert(text.contains("Subscriber    : James BlaBla"));
        assert(text.contains("Activity      : Squash"));
        assert(text.contains("Order        #: ABC123"));
        assert(text.contains("Price         : 20.00"));
    }

    @Test
    public void testPublisherSubscriptionEmailAsHtml() throws Exception {
        MailParser mailParser = MailParser.createPublisherSubscriptionEmail("Squash", "James BlaBla", "Super Squash Courts", "ABC123", 20.0);
        String html = mailParser.getHtml();
        assert(html.contains("Order of Squash"));
        assert(html.contains("Dear Super Squash Courts"));
        assert(html.contains("James BlaBla"));
        assert(html.contains("ABC123"));
        assert(html.contains("20.00"));
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
