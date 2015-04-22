package com.jasify.schedule.appengine.model.activity;

import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 19/04/15.
 */
@Model
public class ActivityPackageSubscription extends Subscription {
    private ModelRef<ActivityPackageExecution> activityPackageExecutionRef = new ModelRef<>(ActivityPackageExecution.class);

    public ModelRef<ActivityPackageExecution> getActivityPackageExecutionRef() {
        return activityPackageExecutionRef;
    }
}
