package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityPackageActivityDao;
import com.jasify.schedule.appengine.dao.common.SubscriptionDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author wszarmach
 * @since 03/07/15.
 */
@SuppressWarnings("unused")
public class ActivityConsistency implements EntityConsistency<Activity> {
    private static final Logger log = LoggerFactory.getLogger(ActivityConsistency.class);

    @BeforeDelete(entityClass = Activity.class)
    public void ensureActivityHasNoSubscriptions(Key id) throws InconsistentModelStateException {
        List<Subscription> subscriptions = new SubscriptionDao().getByActivity(id);
        if (!subscriptions.isEmpty()) {
            throw new InconsistentModelStateException("Activity has subscriptions");
        }
        try {
            List<ActivityPackageActivity> activityPackageActivities = new ActivityPackageActivityDao().getByActivityId(id);
            if (!activityPackageActivities.isEmpty()) {
                throw new InconsistentModelStateException("Activity is linked to Activity Package");
            }
        } catch (EntityNotFoundException e) {
            log.error("Activity not found", e);
            throw new InconsistentModelStateException(e.getMessage());
        }
    }
}
