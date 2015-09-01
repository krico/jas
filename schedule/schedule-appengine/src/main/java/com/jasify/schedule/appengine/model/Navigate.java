package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
}
