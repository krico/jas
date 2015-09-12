package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;

/**
 * @author wszarmach
 * @since 03/09/15.
 */
public class JasUserSubscription implements JasEndpointEntity {
    private Activity activity;
    private Subscription subscription;

    private boolean isPaid;


    public JasUserSubscription() {

    }

    public JasUserSubscription(Subscription subscription) throws EntityNotFoundException {
        setSubscription(subscription);
        Activity activity = new ActivityDao().get(subscription.getActivityRef().getKey());
        setActivity(activity);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
}
