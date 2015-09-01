package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Model;

/**
 * @author wszarmach
 * @since 26/08/15.
 */
@Model
public class SubscriptionHistory extends History {
    private Key subscriptionId;

    public SubscriptionHistory() {
    }

    public SubscriptionHistory(HistoryTypeEnum type) {
        super(type);
    }

    public Key getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(Key subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
