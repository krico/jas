package com.jasify.schedule.appengine.model.history;

import com.jasify.schedule.appengine.model.activity.Subscription;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author wszarmach
 * @since 26/08/15.
 */
@Model
public class SubscriptionHistory extends History {

    private ModelRef<Subscription> subscriptionRef = new ModelRef<>(Subscription.class);

    public SubscriptionHistory() {
    }

    public SubscriptionHistory(HistoryTypeEnum type) {
        super(type);
    }

    public SubscriptionHistory(HistoryTypeEnum type, Subscription subscription) {
        super(type);
        getSubscriptionRef().setModel(subscription);
    }

    public ModelRef<Subscription> getSubscriptionRef() {
        return subscriptionRef;
    }
}
