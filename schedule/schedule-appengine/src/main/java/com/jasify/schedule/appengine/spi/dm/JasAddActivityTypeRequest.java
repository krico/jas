package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.activity.ActivityType;

/**
 * @author krico
 * @since 15/01/15.
 */
public class JasAddActivityTypeRequest {
    private Key organizationId;
    private ActivityType activityType;

    public Key getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Key organizationId) {
        this.organizationId = organizationId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
}
