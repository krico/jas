package com.jasify.schedule.appengine.spi.dm;

import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 21/06/15.
 */
public class JasNewShoppingCartRequest {
    private List<Key> activityIds = new ArrayList<>();
    private List<ActivityPackageSubscription> activityPackageSubscriptions = new ArrayList<>();

    public List<Key> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Key> activityIds) {
        this.activityIds = activityIds;
    }

    public List<ActivityPackageSubscription> getActivityPackageSubscriptions() {
        return activityPackageSubscriptions;
    }

    public void setActivityPackageSubscriptions(List<ActivityPackageSubscription> activityPackageSubscriptions) {
        this.activityPackageSubscriptions = activityPackageSubscriptions;
    }

    public static class ActivityPackageSubscription extends JasActivityPackageSubscription {
        private Key activityPackageId;

        public Key getActivityPackageId() {
            return activityPackageId;
        }

        public void setActivityPackageId(Key activityPackageId) {
            this.activityPackageId = activityPackageId;
        }
    }
}
