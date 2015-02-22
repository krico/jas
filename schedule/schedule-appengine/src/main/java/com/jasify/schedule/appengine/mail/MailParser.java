package com.jasify.schedule.appengine.mail;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by wszarmach on 14/02/15.
 * Temporary class to create html emails
 */
public class MailParser {

    private enum SubstituteKey {
        ActivityName("%ActivityName%"),
        ActivityPrice("%ActivityPrice%"),
        JasifyUrl("%JasifyUrl%"),
        OrderNumber("%OrderNumber%"),
        PaymentMethod("%PaymentMethod%"),
        PasswordUrl("%PasswordUrl%"),
        PublisherName("%PublisherName%"),
        SubscriberName("%SubscriberName%"),
        Tax("%Tax%"),
        TotalPrice("%TotalPrice%"),
        UserName("%UserName%");

        private final String key;

        SubstituteKey(String key) {
            this.key = key;
        }
    }

    public static MailParser createUserSignUpEmail(String subscriberName, String userName) throws Exception {
        MailParser mailParser = new MailParser("/jasify/UserSignUp");
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.UserName, userName);
        return mailParser;
    }

    public static MailParser createBookingEmail(String activityName, String subscriberName, String publisherName, String orderNumber, BigDecimal activityPrice, BigDecimal tax, String paymentMethod) throws Exception {
        MailParser mailParser = new MailParser("/jasify/Booking");
        mailParser.substitute(SubstituteKey.ActivityName, activityName);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.PublisherName, publisherName);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activityPrice));
        mailParser.substitute(SubstituteKey.Tax, formatPrice(tax));
        mailParser.substitute(SubstituteKey.PaymentMethod, paymentMethod);
        BigDecimal totalPrice = activityPrice.add(tax);
        mailParser.substitute(SubstituteKey.TotalPrice, formatPrice(totalPrice));
        mailParser.substitute(SubstituteKey.PaymentMethod, paymentMethod);
        return mailParser;
    }

    public static MailParser createSubscriberPasswordRecoveryEmail(String passwordUrl) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/PasswordRecovery");
        mailParser.substitute(SubstituteKey.PasswordUrl, passwordUrl);
        return mailParser;
    }

    public static MailParser createSubscriberBookingConfirmationEmail(String activityName, String subscriberName, String publisherName, String orderNumber, BigDecimal activityPrice, BigDecimal tax, String paymentMethod) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/BookingConfirmation");
        mailParser.substitute(SubstituteKey.ActivityName, activityName);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.PublisherName, publisherName);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activityPrice));
        mailParser.substitute(SubstituteKey.Tax, formatPrice(tax));
        mailParser.substitute(SubstituteKey.PaymentMethod, paymentMethod);
        BigDecimal totalPrice = activityPrice.add(tax);
        mailParser.substitute(SubstituteKey.TotalPrice, formatPrice(totalPrice));
        mailParser.substitute(SubstituteKey.PaymentMethod, paymentMethod);
        return mailParser;
    }

    public static MailParser createSubscriberEmailSignUpConfirmationEmail(String subscriberName, String userName, String jasifyUrl) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/EmailSignUpConfirmation");
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.UserName, userName);
        mailParser.substitute(SubstituteKey.JasifyUrl, jasifyUrl);
        return mailParser;
    }

    public static MailParser createSubscriberFacebookSignUpConfirmationEmail(String subscriberName, String jasifyUrl) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/FacebookSignUpConfirmation");
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.JasifyUrl, jasifyUrl);
        return mailParser;
    }


    public static MailParser createSubscriberGoogleSignUpConfirmationEmail(String subscriberName, String jasifyUrl) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/GoogleSignUpConfirmation");
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.JasifyUrl, jasifyUrl);
        return mailParser;
    }

    private static String formatPrice(BigDecimal price) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingUsed(false);
        price = price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return decimalFormat.format(price);
    }

    /*
    Rather than two StringBuilders maybe it would be better to convert from html to text
    while maintaining new lines and tabs
     */
    private final StringBuilder htmlBuilder;
    private final StringBuilder textBuilder;

//    public MailParser(StringBuilder htmlBuilder) {
//        Preconditions.checkNotNull(htmlBuilder);
//        Preconditions.checkArgument(htmlBuilder.length() > 0);
//        this.htmlBuilder = htmlBuilder;
//    }

    private MailParser(String resource) throws IOException {
        htmlBuilder = createStringBuilder(resource + ".html");
        textBuilder = createStringBuilder(resource + ".txt");
    }

    private StringBuilder createStringBuilder(String resource) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(resource);
        byte[] buffer = new byte[1024];
        int length;
        StringBuilder builder = new StringBuilder();
        while ((length = inputStream.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, length));
        }
        inputStream.close();
        return builder;
    }

    private void substitute(SubstituteKey substituteKey, String value) {
        Preconditions.checkNotNull(value);
        int indexOfTarget;

        while ((indexOfTarget = htmlBuilder.indexOf(substituteKey.key)) > 0) {
            htmlBuilder.replace(indexOfTarget, indexOfTarget + substituteKey.key.length(), value);
        }

        while ((indexOfTarget = textBuilder.indexOf(substituteKey.key)) > 0) {
            textBuilder.replace(indexOfTarget, indexOfTarget + substituteKey.key.length(), value);
        }
    }

    public String getHtml() {
        return htmlBuilder.toString();
    }

    public String getText() {
        return textBuilder.toString();
    }
}
