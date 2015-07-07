package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityPackageActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageExecutionDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageSubscriptionDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author wszarmach
 * @since 07/07/15.
 */
@SuppressWarnings("unused")
public class ActivityPackageActivityConsistency implements EntityConsistency<ActivityPackageActivity> {
    private static final Logger log = LoggerFactory.getLogger(ActivityPackageActivityConsistency.class);

    private final ActivityPackageActivityDao activityPackageActivityDao = new ActivityPackageActivityDao();
    private final ActivityPackageExecutionDao activityPackageExecutionDao = new ActivityPackageExecutionDao();
    private final ActivityPackageSubscriptionDao activityPackageSubscriptionDao = new ActivityPackageSubscriptionDao();

    @BeforeDelete(entityClass = ActivityPackageActivity.class)
    public void ensureActivityPackageHas(Key id) throws InconsistentModelStateException {
        try {
            ActivityPackageActivity activityPackageActivity = activityPackageActivityDao.get(id);
            Key activityKey = activityPackageActivity.getActivityRef().getKey();
            Key activityPackageKey = activityPackageActivity.getActivityPackageRef().getKey();
            // Find all subscriptions
            List<ActivityPackageSubscription> activityPackageSubscriptions = activityPackageSubscriptionDao.getByActivityId(activityKey);
            int matches = 0;
            for (ActivityPackageSubscription activityPackageSubscription : activityPackageSubscriptions) {
                // Now count how many of those subscriptions are for this specific activity package
                ActivityPackageExecution activityPackageExecution = activityPackageExecutionDao.get(activityPackageSubscription.getActivityPackageExecutionRef().getKey());
                if (activityPackageExecution.getActivityPackageRef().getKey().equals(activityPackageKey)) {
                    matches++;
                }
            }

            if (matches > 0) {
                throw new InconsistentModelStateException("Cannot delete activity package activity with subscriptions! " +
                        "id=" + id + " (" + matches + " subscriptions).");
            }
        } catch (EntityNotFoundException e) {
            log.error("Activity package not found", e);
            throw new InconsistentModelStateException(e.getMessage());
        }
    }
}