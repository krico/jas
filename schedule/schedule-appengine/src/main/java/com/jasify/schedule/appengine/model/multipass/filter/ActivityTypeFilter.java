package com.jasify.schedule.appengine.model.multipass.filter;

import com.google.appengine.api.datastore.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class ActivityTypeFilter implements Serializable {
    private List<Key> activityTypeIds = new ArrayList<>();

    public List<Key> getActivityTypeIds() { return activityTypeIds; }

    public void setActivityTypeIds(List<Key> activityTypeIds) { this.activityTypeIds = activityTypeIds; }
}
