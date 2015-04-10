package com.jasify.schedule.appengine.util;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.OrganizationAccount;
import com.jasify.schedule.appengine.model.balance.UserAccount;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * @author krico
 * @since 06/04/15.
 */
public final class FormatUtil {
    private static final ThreadLocal<SimpleDateFormat> START_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" [EEE, d MMM HH:mm");
        }
    };

    private static final ThreadLocal<SimpleDateFormat> FINISH_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(" - HH:mm]");
        }
    };

    private FormatUtil() {
    }

    public static String toString(Subscription subscription) {
        Key key = subscription.getActivityRef().getKey();
        if (key == null) {
            return toShortString(subscription.getId());
        }
        return toString(subscription.getActivityRef().getModel());
    }

    public static String toString(Activity activity) {
        String name = StringUtils.trimToEmpty(activity.getName());
        if (StringUtils.isBlank(name)) {
            name = "Activity: " + KeyUtil.toHumanReadableString(activity.getId());
        }
        if (activity.getStart() == null || activity.getFinish() == null) return name;
        return name + START_FORMAT.get().format(activity.getStart()) + FINISH_FORMAT.get().format(activity.getFinish());
    }

    public static String toString(UserAccount account) {
        if (account.getUserRef().getKey() == null) {
            return toShortString(account.getId());
        }
        User user = account.getUserRef().getModel();
        return toShortString(account.getId()) + " - " + StringUtils.defaultIfBlank(user.getRealName(), user.getName());
    }

    public static String toString(OrganizationAccount account) {
        if (account.getOrganizationRef().getKey() == null) {
            return toShortString(account.getId());
        }
        Organization organization = account.getOrganizationRef().getModel();
        return toShortString(account.getId()) + " - " + organization.getName();
    }

    public static String toString(Account account) {
        if (account instanceof UserAccount) {
            return toString((UserAccount) account);
        } else if (account instanceof OrganizationAccount) {
            return toString((OrganizationAccount) account);
        }
        return toShortString(account.getId()) + " - account";
    }

    private static String toShortString(Key id) {
        if (id == null) return "NULL";
        if (id.getName() == null) return Objects.toString(id.getId());
        return id.getName();
    }

}
