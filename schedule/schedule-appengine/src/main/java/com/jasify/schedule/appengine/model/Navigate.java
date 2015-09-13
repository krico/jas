package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author krico
 * @since 01/09/15.
 */
public final class Navigate {
    private static final Logger log = LoggerFactory.getLogger(Navigate.class);

    private Navigate() {
    }

    public static Activity activity(Subscription subscription) {

        if (subscription == null)
            return null;

        Key activityId = subscription.getActivityRef().getKey();
        if (activityId == null) {
            log.warn("Subscription [{}] has no ActivityRef", subscription.getId());
            return null;
        }

        ActivityDao activityDao = new ActivityDao();
        Activity activity = activityDao.getOrNull(activityId);

        if (activity == null)
            log.warn("Subscription [{}] points to non-existent Activity [{}]", subscription.getId(), activityId);

        return activity;
    }

    public static ActivityType activityType(Activity activity) {

        if (activity == null)
            return null;

        Key activityTypeId = activity.getActivityTypeRef().getKey();
        if (activityTypeId == null) {
            log.warn("Activity [{}] has no ActivityTypeRef", activity.getId());
            return null;
        }
        ActivityTypeDao activityTypeDao = new ActivityTypeDao();
        ActivityType activityType = activityTypeDao.getOrNull(activityTypeId);

        if (activityType == null)
            log.warn("Activity [{}] points to non-existent ActivityType [{}]", activity.getId(), activityTypeId);

        return activityType;
    }

    public static Organization organization(ActivityType activityType) {

        if (activityType == null)
            return null;

        Key organizationId = activityType.getOrganizationRef().getKey();
        if (organizationId == null) {
            log.warn("ActivityType [{}] has no OrganizationRef", activityType.getId());
            return null;
        }

        OrganizationDao organizationDao = new OrganizationDao();
        Organization organization = organizationDao.getOrNull(organizationId);

        if (organization == null)
            log.warn("ActivityType [{}] points to non-existent Organization [{}]", activityType.getId(), organizationId);

        return organization;
    }

    public static Organization organization(ActivityPackage activityPackage) {

        if (activityPackage == null)
            return null;

        Key organizationId = activityPackage.getOrganizationRef().getKey();
        if (organizationId == null) {
            log.warn("ActivityPackage [{}] has no OrganizationRef", activityPackage.getId());
            return null;
        }

        OrganizationDao organizationDao = new OrganizationDao();
        Organization organization = organizationDao.getOrNull(organizationId);

        if (organization == null)
            log.warn("ActivityPackage [{}] points to non-existent Organization [{}]", activityPackage.getId(), organizationId);

        return organization;
    }

    public static ActivityPackage activityPackage(ActivityPackageExecution execution) {

        if (execution == null)
            return null;

        Key activityPackageId = execution.getActivityPackageRef().getKey();
        if (activityPackageId == null) {
            log.warn("ActivityPackageExecution [{}] has no ActivityPackageRef", execution.getId());
            return null;
        }

        ActivityPackageDao activityPackageDao = new ActivityPackageDao();
        ActivityPackage activityPackage = activityPackageDao.getOrNull(activityPackageId);

        if (activityPackage == null)
            log.warn("ActivityPackageExecution [{}] points to non-existent ActivityPackage [{}]", execution.getId(), activityPackageId);

        return activityPackage;
    }

    public static Attachment attachment(InvoicePayment payment) {
        if (payment == null) return null;
        Key attachmentId = payment.getAttachmentRef().getKey();
        if (attachmentId == null) {
            log.warn("InvoicePayment [{}] has no AttachmentRef", payment.getId());
            return null;
        }
        AttachmentDao attachmentDao = new AttachmentDao();
        Attachment attachment = attachmentDao.getOrNull(attachmentId);

        if (attachment == null)
            log.warn("InvoicePayment [{}] point to non-existent Attachment [{}]", payment.getId(), attachmentId);

        return attachment;
    }

    public static Organization organization(ActivityPackageExecution execution) {

        if (execution == null)
            return null;

        ActivityPackage activityPackage = Navigate.activityPackage(execution);

        return Navigate.organization(activityPackage);
    }

    public static Organization organization(Subscription subscription) {

        if (subscription == null)
            return null;

        Activity activity = Navigate.activity(subscription);
        ActivityType activityType = Navigate.activityType(activity);
        return Navigate.organization(activityType);
    }

    public static List<User> users(Organization organization) {

        if (organization == null)
            return null;

        OrganizationDao organizationDao = new OrganizationDao();
        try {
            return organizationDao.getUsersOfOrganization(organization.getId());
        } catch (EntityNotFoundException e) {
            log.error("Failed to get users of organization [{}]", organization.getId(), e);
            return null;
        }
    }

    public static User user(Payment payment) {
        if (payment == null)
            return null;

        Key userId = payment.getUserRef().getKey();
        if (userId == null) {
            log.warn("Payment [{}] has no UserRef", payment.getId());
            return null;
        }

        UserDao userDao = new UserDao();
        try {
            return userDao.get(userId);
        } catch (EntityNotFoundException e) {
            log.error("Failed to get user of payment [{}]", payment.getId(), e);
            return null;
        }
    }

    public static List<Organization> organizations(Payment payment) {
        Map<Key, Organization> organizationMap = new HashMap<>();
        List<Subscription> subscriptions = Navigate.subscriptions(payment);
        for (Subscription subscription : subscriptions) {
            Organization organization = Navigate.organization(subscription);
            if (organization != null) organizationMap.put(organization.getId(), organization);
        }
        List<ActivityPackageExecution> activityPackageExecutions = Navigate.activityPackageExecutions(payment);
        for (ActivityPackageExecution execution : activityPackageExecutions) {
            Organization organization = Navigate.organization(execution);
            if (organization != null) organizationMap.put(organization.getId(), organization);
        }
        return new ArrayList<>(organizationMap.values());
    }

    public static List<ActivityPackageExecution> activityPackageExecutions(Payment payment) {
        return payment.getActivityPackageExecutions(); //TODO: move logic here
    }

    public static List<Subscription> subscriptions(Payment payment) {
        return payment.getSubscriptions(); //TODO: move logic here
    }
}
