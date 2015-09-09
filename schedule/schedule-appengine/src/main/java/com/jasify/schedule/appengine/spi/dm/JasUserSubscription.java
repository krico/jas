package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.spi.transform.JasActivityTransformer;
import com.jasify.schedule.appengine.spi.transform.JasSubscriptionTransformer;

/**
 * @author wszarmach
 * @since 03/09/15.
 */
public class JasUserSubscription implements JasEndpointEntity {
    private JasActivity activity;
    private JasSubscription subscription;

    private boolean isPaid;


    public JasUserSubscription() {

    }

    public JasUserSubscription(Subscription subscription) throws EntityNotFoundException {
        JasActivityTransformer activityTransformer = new JasActivityTransformer();
        JasSubscriptionTransformer subscriptionTransformer = new JasSubscriptionTransformer();
        setSubscription(subscriptionTransformer.transformTo(subscription));
        Activity activity = new ActivityDao().get(subscription.getActivityRef().getKey());
        setActivity(activityTransformer.transformTo(activity));
    }

    public JasActivity getActivity() {
        return activity;
    }

    public void setActivity(JasActivity activity) {
        this.activity = activity;
    }

    public JasSubscription getSubscription() {
        return subscription;
    }

    public void setSubscription(JasSubscription subscription) {
        this.subscription = subscription;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
}
