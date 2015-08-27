package com.jasify.schedule.appengine.util;

import java.util.TimeZone;

/**
 * Centralize everything that should be dynamic (TimeZone, etc)
 *
 * @author krico
 * @since 25/08/15.
 */
public final class InternationalizationUtil {

    public static final String ZURICH_TIME_ZONE_ID = "Europe/Zurich";
    public static final TimeZone ZURICH_TIME_ZONE = TimeZone.getTimeZone(ZURICH_TIME_ZONE_ID);

    private InternationalizationUtil() {
    }

    //TODO: one day this will know what the user's time zone is
    public static TimeZone getUserTimeZone() {
        return ZURICH_TIME_ZONE;
    }

    //TODO: one day this will know where an activity is, and give the proper time zone
    public static TimeZone getLocationTimeZone() {
        return ZURICH_TIME_ZONE;
    }
}
