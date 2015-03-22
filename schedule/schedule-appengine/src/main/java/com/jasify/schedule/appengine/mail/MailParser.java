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
        Branch("%Branch%"),
        Fees("%Fees%"),
        JasifyUrl("%JasifyUrl%"),
        Number("%Number%"),
        OrderNumber("%OrderNumber%"),
        PaymentMethod("%PaymentMethod%"),
        PasswordUrl("%PasswordUrl%"),
        PublisherName("%PublisherName%"),
        SubscriberName("%SubscriberName%"),
        Tax("%Tax%"),
        Timestamp("%Timestamp%"),
        TotalPrice("%TotalPrice%"),
        UserName("%UserName%"),
        Version("%Version%");

        private final String key;

        SubstituteKey(String key) {
            this.key = key;
        }
    }

    public static MailParser createNewVersionEmail(String version, String timestamp, String branch, String number, String jasifyUrl) throws Exception {
        MailParser mailParser = new MailParser("/jasify/NewVersion");
        mailParser.substitute(SubstituteKey.Version, version);
        mailParser.substitute(SubstituteKey.Timestamp, timestamp);
        mailParser.substitute(SubstituteKey.Branch, branch);
        mailParser.substitute(SubstituteKey.Number, number);
        mailParser.substitute(SubstituteKey.JasifyUrl, jasifyUrl);
        return mailParser;
    }

    public static MailParser createJasifyUserSignUpEmail(String subscriberName, String userName) throws Exception {
        MailParser mailParser = new MailParser("/jasify/UserSignUp");
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.UserName, userName);
        return mailParser;
    }

    public static MailParser createJasifySubscriptionEmail(String activityName, String subscriberName, String publisherName, String orderNumber, Double activityPrice, Double fees, Double tax, String paymentMethod) throws Exception {
        BigDecimal bdActivityPrice = convert(activityPrice);
        BigDecimal bdFees = convert(fees);
        BigDecimal bdTax = convert(tax);
        MailParser mailParser = new MailParser("/jasify/Subscription");
        mailParser.substitute(SubstituteKey.ActivityName, activityName);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.PublisherName, publisherName);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(bdActivityPrice));
        mailParser.substitute(SubstituteKey.Fees, formatPrice(bdFees));
        mailParser.substitute(SubstituteKey.Tax, formatPrice(bdTax));
        BigDecimal bdTotalPrice = convert(bdActivityPrice.add(bdFees).add(bdTax).doubleValue());
        mailParser.substitute(SubstituteKey.TotalPrice, formatPrice(bdTotalPrice));
        mailParser.substitute(SubstituteKey.PaymentMethod, paymentMethod);
        return mailParser;

    }

    public static MailParser createPublisherSubscriptionEmail(String activityName, String subscriberName, String publisherName, String orderNumber, Double activityPrice) throws Exception {
        BigDecimal bdActivityPrice = convert(activityPrice);
        MailParser mailParser = new MailParser("/publisher/Subscription");
        mailParser.substitute(SubstituteKey.ActivityName, activityName);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.PublisherName, publisherName);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(bdActivityPrice));
        return mailParser;
    }

    public static MailParser createSubscriberSubscriptionEmail(String activityName, String subscriberName, String publisherName, String orderNumber, Double activityPrice) throws Exception {
        BigDecimal bdActivityPrice = convert(activityPrice);
        MailParser mailParser = new MailParser("/subscriber/Subscription");
        mailParser.substitute(SubstituteKey.ActivityName, activityName);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.PublisherName, publisherName);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(bdActivityPrice));
        return mailParser;
    }

    public static MailParser createSubscriberPasswordRecoveryEmail(String passwordUrl) throws Exception {
        MailParser mailParser = new MailParser("/subscriber/PasswordRecovery");
        mailParser.substitute(SubstituteKey.PasswordUrl, passwordUrl);
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

    private static BigDecimal convert(Double input) {
        BigDecimal output = BigDecimal.ZERO;
        if (input != null) output = BigDecimal.valueOf(input);
        return output.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    private static String formatPrice(BigDecimal price) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingUsed(false);
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
