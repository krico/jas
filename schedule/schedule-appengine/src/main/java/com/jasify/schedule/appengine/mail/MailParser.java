package com.jasify.schedule.appengine.mail;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * @author wszarmach
 * @since 14/02/15.
 * Temporary class to create html/text emails
 */
public class MailParser {

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").withZone(DateTimeZone.forID("Europe/Zurich"));

    private static final OrganizationDao organizationDao = new OrganizationDao();
    private static final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private static final ActivityDao activityDao = new ActivityDao();
    private static final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private static final UserDao userDao = new UserDao();

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

    public static MailParser createNewVersionEmail(String appEngineVersion, String version, String timestamp, String branch, String number, String jasifyUrl) throws Exception {
        MailParser mailParser = new MailParser("/jasify/NewVersion");
        mailParser.substitute(SubstituteKey.Version, version);
        mailParser.substitute(SubstituteKey.AppEngineVersion, appEngineVersion);
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

    private static String formatDate(Date date) {
        return new DateTime(date).toString(DTF);
    }

    private static String getOrganisationName(Activity activity) throws EntityNotFoundException {
        ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
        Organization organization = organizationDao.get(activityType.getOrganizationRef().getKey());
        return organization.getName();
    }

    private static String getOrganisationName(ActivityPackage activityPackage) throws EntityNotFoundException {
        Organization organization = organizationDao.get(activityPackage.getOrganizationRef().getKey());
        return organization.getName();
    }

    private static String getOrganizationNameFromSubscriptions(Collection<Subscription> subscriptions) throws EntityNotFoundException {
        for (Subscription subscription : subscriptions) {
            Activity activity = activityDao.get(subscription.getActivityRef().getKey());
            String name = getOrganisationName(activity);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    private static String getOrganizationNameFromActivityPackageExecutions(Collection<ActivityPackageExecution> executions) throws EntityNotFoundException {
        for (ActivityPackageExecution execution : executions) {
            ActivityPackage activityPackage = activityPackageDao.get(execution.getActivityPackageRef().getKey());
            String name = getOrganisationName(activityPackage);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    private static MailParser createSubscriptionActivityDetails(MultiSubscription multiSubscription, Subscription subscription) throws IOException, EntityNotFoundException {
        Activity activity = activityDao.get(subscription.getActivityRef().getKey());
        String orderNumber = KeyUtil.toHumanReadableString(subscription.getId());
        MailParser mailParser = new MailParser(multiSubscription.activityDetails);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.PublisherName, getOrganisationName(activity));
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName());
        mailParser.substitute(SubstituteKey.ActivityStart, formatDate(activity.getStart()));
        mailParser.substitute(SubstituteKey.ActivityFinish, formatDate(activity.getFinish()));
        mailParser.substitute(SubstituteKey.ActivityPrice, formatPrice(activity.getPrice()));
        return mailParser;
    }

    private static MailParser createSubscriptionActivityPackageDetails(MultiSubscription multiSubscription, ActivityPackageExecution activityPackageExecution) throws IOException, EntityNotFoundException {
        ActivityPackage activityPackage = activityPackageDao.get(activityPackageExecution.getActivityPackageRef().getKey());
        String orderNumber = KeyUtil.toHumanReadableString(activityPackageExecution.getId());
        MailParser mailParser = new MailParser(multiSubscription.activityPackageDetails);
        mailParser.substitute(SubstituteKey.OrderNumber, orderNumber);
        mailParser.substitute(SubstituteKey.PublisherName, getOrganisationName(activityPackage));
        mailParser.substitute(SubstituteKey.ActivityPackageName, activityPackage.getName());
        mailParser.substitute(SubstituteKey.ActivityPackagePrice, formatPrice(activityPackage.getPrice()));

        Collection<MailParser> activityPackageActivityDetails = new ArrayList<>();
        for (ActivityPackageSubscription activityPackageSubscription : activityPackageExecution.getSubscriptionListRef().getModelList()) {
            // TODO
            activityPackageActivityDetails.add(MailParser.createSubscriptionActivityPackageActivityDetails(multiSubscription, activityPackageSubscription));
        }

        mailParser.substitute(SubstituteKey.ActivityPackageActivityDetails, activityPackageActivityDetails);

        return mailParser;
    }

    private static MailParser createSubscriptionActivityPackageActivityDetails(MultiSubscription multiSubscription, ActivityPackageSubscription activityPackageSubscription) throws IOException, EntityNotFoundException {
        Activity activity = activityDao.get(activityPackageSubscription.getActivityRef().getKey());
        MailParser mailParser = new MailParser(multiSubscription.activityPackageActivityDetails);
        mailParser.substitute(SubstituteKey.ActivityName, activity.getName());
        mailParser.substitute(SubstituteKey.ActivityStart, formatDate(activity.getStart()));
        mailParser.substitute(SubstituteKey.ActivityFinish, formatDate(activity.getFinish()));
        return mailParser;
    }

    private static String getSubscriberName(User user) {
        if (user.getRealName() != null) {
            return user.getRealName();
        }
        return user.getName();
    }

    private static MailParser createSubscriptionEmail(MultiSubscription multiSubscription, Collection<Subscription> subscriptions, Collection<ActivityPackageExecution> activityPackageExecutions) throws Exception {
        double totalPrice = 0;
        String subscriberName = null;
        Collection<MailParser> activityDetails = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            Activity activity = activityDao.get(subscription.getActivityRef().getKey());
            Double subscriptionPrice = activity.getPrice();
            if (subscriptionPrice != null) {
                totalPrice += subscriptionPrice;
            }
            activityDetails.add(MailParser.createSubscriptionActivityDetails(multiSubscription, subscription));
            if (subscriberName == null) {
                User user = userDao.get(subscription.getUserRef().getKey());
                subscriberName = getSubscriberName(user);
            }
        }

        for (ActivityPackageExecution activityPackageExecution : activityPackageExecutions) {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageExecution.getActivityPackageRef().getKey());
            Double activityPackagePrice = activityPackage.getPrice();
            if (activityPackagePrice != null) {
                totalPrice += activityPackagePrice;
            }
            activityDetails.add(MailParser.createSubscriptionActivityPackageDetails(multiSubscription, activityPackageExecution));
            if (subscriberName == null) {
                User user = userDao.get(activityPackageExecution.getUserRef().getKey());
                subscriberName = getSubscriberName(user);
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

    public static MailParser createPublisherSubscriptionEmail(Collection<Subscription> subscriptions, Collection<ActivityPackageExecution> executions) throws Exception {
        String organizationName = getOrganizationNameFromSubscriptions(subscriptions);
        if (organizationName == null) {
            organizationName = getOrganizationNameFromActivityPackageExecutions(executions);
        }
        if (organizationName == null) {
            throw new Exception("Could not find organization name");
        }
        MailParser mailParser = createSubscriptionEmail(MultiSubscription.Publisher, subscriptions, executions);
        mailParser.substitute(SubstituteKey.PublisherName, organizationName);
        return mailParser;
    }

    public static MailParser createSubscriberSubscriptionEmail(Collection<Subscription> subscriptions, Collection<ActivityPackageExecution> executions) throws Exception {
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
        Version("%Version%"),
        AppEngineVersion("%AppEngineVersion%");

        private final String key;

        SubstituteKey(String key) {
            this.key = key;
        }
    }

    private enum MultiSubscription {
        Publisher("/publisher/Subscription", "/publisher/SubscriptionActivityDetails", "/publisher/SubscriptionActivityPackageDetails", "/common/SubscriptionActivityPackageActivityDetails"),
        Subscriber("/subscriber/Subscription", "/subscriber/SubscriptionActivityDetails", "/subscriber/SubscriptionActivityPackageDetails", "/common/SubscriptionActivityPackageActivityDetails");

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
