package com.jasify.schedule.appengine.model.activity;

/**
 * @author krico
 * @since 09/01/15.
 */
public class ActivityServiceFactory {
    private static ActivityService instance;

    protected ActivityServiceFactory() {
    }

    public static ActivityService getActivityService() {
        if (instance == null)
            return DefaultActivityService.instance();
        return instance;
    }

    protected static void setInstance(ActivityService instance) {
        ActivityServiceFactory.instance = instance;
    }
}
