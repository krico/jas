package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.model.activity.Activity;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

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

    public static String toString(Activity activity) {
        String name = StringUtils.trimToEmpty(activity.getName());
        if (StringUtils.isBlank(name)) {
            name = "Activity: " + KeyUtil.toHumanReadableString(activity.getId());
        }
        return name + START_FORMAT.get().format(activity.getStart()) + FINISH_FORMAT.get().format(activity.getFinish());
    }
}
