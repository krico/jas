package com.jasify.schedule.appengine.mail;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author wszarmach
 * @since 14/02/15.
 * Temporary class to create html/text emails
 */
public class MailParser {

    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
    /*
    Rather than two StringBuilders maybe it would be better to convert from html to text
    while maintaining new lines and tabs
     */
    private final StringBuilder htmlBuilder;
    private final StringBuilder textBuilder;

    private MailParser(String resource) throws IOException {
        htmlBuilder = createStringBuilder(resource + ".html");
        textBuilder = createStringBuilder(resource + ".txt");
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

    public static MailParser createJasifyUserSignUpEmail(User user) throws Exception {
        MailParser mailParser = new MailParser("/jasify/UserSignUp");
        mailParser.substitute(SubstituteKey.SubscriberName, getSubscriberName(user));
        mailParser.substitute(SubstituteKey.UserName, user.getName());
        return mailParser;
    }

    private static String getTimeZone(Activity activity) {
        // TODO: Temporary method as initial activities did not have TimeZone
        if (activity.getTimeZone() == null)
            return "Europe/Zurich";
        return activity.getTimeZone();
    }
    private static MailParser createSubscriptionActivityDetails(MultiSubscription multiSubscription, Subscription subscription) throws IOException {
        Activity activity = subscription.getActivityRef().getModel();
        ActivityType activityType = activity.getActivityTypeRef().getModel();
        Organization organization = activityType.getOrganizationRef().getModel();
        String orderNumber = KeyUtil.toHumanReadableString(subscription.getId());
        DateTime start = new DateTime(activity.getStart(), DateTimeZone.forID(getTimeZone(activity)));
        DateTime finish = new DateTime(activity.getFinish(), DateTimeZone.forID(getTimeZone(activity)));
        MailParser mailParser = new MailParser(multiSubscription.activityDetails);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.PublisherName, organization.getName());
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName());
        mailParser.substitute(SubstituteKey.ActivityStart, start.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityFinish, finish.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activity.getPrice()));
        return mailParser;
    }

    private static MailParser createSubscriptionActivityPackageDetails(MultiSubscription multiSubscription, ActivityPackageExecution activityPackageExecution) throws IOException {
        ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
        Organization organization = activityPackage.getOrganizationRef().getModel();
        String orderNumber = KeyUtil.toHumanReadableString(activityPackageExecution.getId());
        MailParser mailParser = new MailParser(multiSubscription.activityPackageDetails);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.PublisherName, organization.getName());
        mailParser.substitute(SubstituteKey.ActivityPackageName, activityPackage.getName());
        mailParser.substitute(SubstituteKey.ActivityPackagePrice, formatPrice(activityPackage.getPrice()));

        List<MailParser> activityPackageActivityDetails = new ArrayList<>();
        for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
            activityPackageActivityDetails.add(MailParser.createSubscriptionActivityPackageActivityDetails(multiSubscription, activityPackageSubscription));
        }

        mailParser.substitute(SubstituteKey.ActivityPackageActivityDetails, activityPackageActivityDetails);

        return mailParser;
    }

    private static MailParser createSubscriptionActivityPackageActivityDetails(MultiSubscription multiSubscription, ActivityPackageSubscription activityPackageSubscription) throws IOException {
        Activity activity = activityPackageSubscription.getActivityRef().getModel();
        DateTime start = new DateTime(activity.getStart(), DateTimeZone.forID(getTimeZone(activity)));
        DateTime finish = new DateTime(activity.getFinish(), DateTimeZone.forID(getTimeZone(activity)));
        MailParser mailParser = new MailParser(multiSubscription.activityPackageActivityDetails);
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName());
        mailParser.substitute(SubstituteKey.ActivityStart, start.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityFinish, finish.toString(dtf));
        return mailParser;
    }

    private static String getSubscriberName(User user) {
        if (user.getRealName() != null) {
            return user.getRealName();
        }
        return user.getName();
    }

    private static MailParser createSubscriptionEmail(MultiSubscription multiSubscription, List<Subscription> subscriptions, List<ActivityPackageExecution> activityPackageExecutions) throws Exception {
        double totalPrice = 0;
        String subscriberName = null;
        List<MailParser> activityDetails = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            Double subscriptionPrice = subscription.getActivityRef().getModel().getPrice();
            if (subscriptionPrice != null) {
                totalPrice += subscriptionPrice;
            }
            activityDetails.add(MailParser.createSubscriptionActivityDetails(multiSubscription, subscription));
            if (subscriberName == null) {
                subscriberName = getSubscriberName(subscription.getUserRef().getModel());
            }
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();
            Double activityPackagePrice = activityPackage.getPrice();
            if (activityPackagePrice != null) {
                totalPrice += activityPackagePrice;
            }
            activityDetails.add(MailParser.createSubscriptionActivityPackageDetails(multiSubscription, activityPackageExecution));
            if (subscriberName == null) {
                subscriberName = getSubscriberName(activityPackageExecution.getUserRef().getModel());
            }
        }

        double fees = 0.0;
        double tax = 0.0;
        totalPrice = totalPrice + fees + tax;

        MailParser mailParser = new MailParser(multiSubscription.subscription);
        mailParser.substitute(SubstituteKey.SubscriberName, subscriberName);
        mailParser.substitute(SubstituteKey.ActivityDetails, activityDetails);
        mailParser.substitute(SubstituteKey.Fees, formatPrice(fees));
        mailParser.substitute(SubstituteKey.Tax, formatPrice(tax));
        mailParser.substitute(SubstituteKey.TotalPrice, formatPrice(totalPrice));
        mailParser.substitute(SubstituteKey.PaymentMethod, "Unknown");
        return mailParser;
    }

    public static MailParser createJasifySubscriptionEmail(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) throws Exception {
        return createSubscriptionEmail(MultiSubscription.Jasify, subscriptions, executions);
    }

    public static MailParser createPublisherSubscriptionEmail(Subscription subscription) throws Exception {
        Activity activity = subscription.getActivityRef().getModel();
        ActivityType activityType = activity.getActivityTypeRef().getModel();
        Organization organization = activityType.getOrganizationRef().getModel();

        String orderNumber = KeyUtil.toHumanReadableString(subscription.getId());
        User user = subscription.getUserRef().getModel();
        DateTime start = new DateTime(activity.getStart(), DateTimeZone.forID(getTimeZone(activity)));
        DateTime finish = new DateTime(activity.getFinish(), DateTimeZone.forID(getTimeZone(activity)));
        MailParser mailParser = new MailParser("/publisher/Subscription");
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.SubscriberName, getSubscriberName(user));
        mailParser.substitute(SubstituteKey.PublisherName, organization.getName());
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName());
        mailParser.substitute(SubstituteKey.ActivityStart, start.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityFinish, finish.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activity.getPrice()));
        return mailParser;
    }

    public static MailParser createPublisherSubscriptionEmail(ActivityPackageSubscription activityPackageSubscription) throws Exception {
        Activity activity = activityPackageSubscription.getActivityRef().getModel();
        ActivityType activityType = activity.getActivityTypeRef().getModel();
        Organization organization = activityType.getOrganizationRef().getModel();
        ActivityPackageExecution activityPackageExecution = activityPackageSubscription.getActivityPackageExecutionRef().getModel();
        ActivityPackage activityPackage = activityPackageExecution.getActivityPackageRef().getModel();

        String orderNumber = KeyUtil.toHumanReadableString(activityPackageExecution.getId());
        User user = activityPackageSubscription.getUserRef().getModel();
        DateTime start = new DateTime(activity.getStart(), DateTimeZone.forID(getTimeZone(activity)));
        DateTime finish = new DateTime(activity.getFinish(), DateTimeZone.forID(getTimeZone(activity)));
        MailParser mailParser = new MailParser("/publisher/Subscription");
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.SubscriberName, getSubscriberName(user));
        mailParser.substitute(SubstituteKey.PublisherName, organization.getName());
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName() + " [" + activityPackage.getName() + "]");
        mailParser.substitute(SubstituteKey.ActivityStart, start.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityFinish, finish.toString(dtf));
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activityPackage.getPrice()));
        return mailParser;
    }

    public static MailParser createSubscriberSubscriptionEmail(List<Subscription> subscriptions, List<ActivityPackageExecution> executions) throws Exception {
        return createSubscriptionEmail(MultiSubscription.Subscriber, subscriptions, executions);
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

    public static String formatPrice(Double input) {
        BigDecimal output = BigDecimal.ZERO;
        if (input != null) {
            output = BigDecimal.valueOf(input);
        }
        output = output.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setGroupingUsed(false);
        return decimalFormat.format(output);
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

    private void substitute(StringBuilder stringBuilder, SubstituteKey substituteKey, String value) {
        Preconditions.checkNotNull(value);
        int indexOfTarget;

        while ((indexOfTarget = stringBuilder.indexOf(substituteKey.key)) > 0) {
            stringBuilder.replace(indexOfTarget, indexOfTarget + substituteKey.key.length(), value);
        }
    }

    private void substitute(SubstituteKey substituteKey, String value) {
        substitute(htmlBuilder, substituteKey, value);
        substitute(textBuilder, substituteKey, value);
    }

    private void substitute(SubstituteKey substituteKey, Collection<MailParser> mailParsers) {
        StringBuilder tempHtmlBuilder = new StringBuilder();
        StringBuilder tempTextBuilder = new StringBuilder();
        for (MailParser mailParser : mailParsers) {
            tempHtmlBuilder.append(mailParser.htmlBuilder);
            tempTextBuilder.append(mailParser.textBuilder);
        }
        substitute(htmlBuilder, substituteKey, tempHtmlBuilder.toString());
        substitute(textBuilder, substituteKey, tempTextBuilder.toString());
    }

    public String getHtml() {
        return htmlBuilder.toString();
    }

    public String getText() {
        return textBuilder.toString();
    }

    private enum SubstituteKey {
        ActivityDetails("%ActivityDetails%"),
        ActivityFinish("%ActivityFinish%"),
        ActivityName("%ActivityName%"),
        ActivityPackageActivityDetails("%ActivityPackageActivityDetails%"),
        ActivityPackageName("%ActivityPackageName%"),
        ActivityPackagePrice("%ActivityPackagePrice%"),
        ActivityPrice("%ActivityPrice%"),
        ActivityStart("%ActivityStart%"),
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

    private enum MultiSubscription {
        Jasify("/jasify/Subscription", "/jasify/SubscriptionActivityDetails", "/jasify/SubscriptionActivityPackageDetails", "/jasify/SubscriptionActivityPackageActivityDetails"),
        Subscriber("/subscriber/Subscription", "/subscriber/SubscriptionActivityDetails", "/subscriber/SubscriptionActivityPackageDetails", "/subscriber/SubscriptionActivityPackageActivityDetails");

        String subscription;
        String activityDetails;
        String activityPackageDetails;
        String activityPackageActivityDetails;

        MultiSubscription(String subscription, String activityDetails, String activityPackageDetails, String activityPackageActivityDetails) {
            this.subscription = subscription;
            this.activityDetails = activityDetails;
            this.activityPackageDetails = activityPackageDetails;
            this.activityPackageActivityDetails = activityPackageActivityDetails;
        }
    }
}
