package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.OrganizationAccount;
import com.jasify.schedule.appengine.model.balance.UserAccount;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author krico
 * @since 06/04/15.
 */
public final class FormatUtil {
    public static final FormatUtil INSTANCE = new FormatUtil();
    private static final Logger log = LoggerFactory.getLogger(FormatUtil.class);
    private static final ThreadLocal<SimpleDateFormat> START_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat(" [EEE, d MMM HH:mm");
            sdf.setTimeZone(InternationalizationUtil.ZURICH_TIME_ZONE);
            return sdf;
        }
    };

    private static final ThreadLocal<SimpleDateFormat> FINISH_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat sdf = new SimpleDateFormat(" - HH:mm]");
            sdf.setTimeZone(InternationalizationUtil.ZURICH_TIME_ZONE);
            return sdf;
        }
    };

    private FormatUtil() {
    }

    public static String toString(Subscription subscription) {
        try {
            if (subscription.getActivityRef().getKey() != null) {
                ActivityDao activityDao = new ActivityDao();
                Activity activity = activityDao.get(subscription.getActivityRef().getKey());
                StringBuilder builder = new StringBuilder().append(activity);

                if (subscription.getUserRef().getKey() != null) {
                    UserDao userDao = new UserDao();
                    User user = userDao.get(subscription.getUserRef().getKey());
                    builder.append(" (").append(toString(user)).append(')');
                }

                return builder.toString();
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to find entity", e);
        }
        return toShortString(subscription.getId());
    }

    public static String toString(ActivityPackageExecution activityPackageExecution) {
        try {
            if (activityPackageExecution.getActivityPackageRef().getKey() != null) {
                ActivityPackageDao activityPackageDao = new ActivityPackageDao();
                ActivityPackage activityPackage = activityPackageDao.get(activityPackageExecution.getActivityPackageRef().getKey());
                StringBuilder builder = new StringBuilder().append(toString(activityPackage));
                if (activityPackageExecution.getUserRef().getKey() != null) {
                    UserDao userDao = new UserDao();
                    User user = userDao.get(activityPackageExecution.getUserRef().getKey());
                    builder.append(" (").append(toString(user)).append(')');
                }
                return builder.toString();
            }
        } catch (EntityNotFoundException e) {
            log.error("Failed to find entity", e);
        }
        return toShortString(activityPackageExecution.getId());
    }

    public static String toString(Activity activity) {
        String name = StringUtils.trimToEmpty(activity.getName());
        if (StringUtils.isBlank(name)) {
            name = "Activity: " + KeyUtil.toHumanReadableString(activity.getId());
        }
        if (activity.getStart() == null || activity.getFinish() == null) return name;
        return name + START_FORMAT.get().format(activity.getStart()) + FINISH_FORMAT.get().format(activity.getFinish());
    }

    public static String toString(ActivityPackage activityPackage) {
        String name = StringUtils.trimToEmpty(activityPackage.getName());
        if (StringUtils.isBlank(name)) {
            name = "Activity: " + KeyUtil.toHumanReadableString(activityPackage.getId());
        }
        return name + " (" + activityPackage.getItemCount() + " allowed subscriptions)";
    }

    public static String toString(UserAccount account) {
        if (account.getUserRef().getKey() == null) {
            return toShortString(account.getId());
        }
        return toString(account.getUserRef().getModel()) + " (" + toShortString(account.getId()) + ")";
    }

    public static String toString(User user) {
        return StringUtils.defaultIfBlank(user.getRealName(), user.getName());
    }

    public static String toString(OrganizationAccount account) {
        if (account.getOrganizationRef().getKey() == null) {
            return toShortString(account.getId());
        }
        Organization organization = account.getOrganizationRef().getModel();
        return organization.getName() + " (" + toShortString(account.getId()) + ")";
    }

    public static String toString(Account account) {
        if (account instanceof UserAccount) {
            return toString((UserAccount) account);
        } else if (account instanceof OrganizationAccount) {
            return toString((OrganizationAccount) account);
        }
        return toShortString(account.getId());
    }

    private static String toShortString(Key id) {
        if (id == null) return "NULL";
        if (id.getName() == null) return Objects.toString(id.getId());
        return id.getName();
    }

    public static String toTransactionFeeString(Subscription subscription) {
        return "Transaction Fee " + toString(subscription);
    }

    public static String toTransactionFeeString(ActivityPackageExecution activityPackageExecution) {
        return "Transaction Fee " + toString(activityPackageExecution);
    }
}
