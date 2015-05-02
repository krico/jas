package com.jasify.schedule.appengine.spi.dm;

import com.google.api.client.util.Lists;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;

import java.util.List;

/**
 * @author krico
 * @since 02/05/15.
 */
public class JasAddActivityPackageRequest {
    private ActivityPackage activityPackage;
    private List<Activity> activities = Lists.newArrayList();

    public ActivityPackage getActivityPackage() {
        return activityPackage;
    }

    public void setActivityPackage(ActivityPackage activityPackage) {
        this.activityPackage = activityPackage;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
