package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 11/05/15.
 */
public class JasActivityPackageSubscription {
    private List<Key> activityIds = new ArrayList<>();

    public List<Key> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Key> activityIds) {
        this.activityIds = activityIds;
    }
}
