package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wszarmach
 * @since 03/07/15.
 */
@SuppressWarnings("unused")
public class ActivityPackageConsistency implements EntityConsistency<ActivityPackage> {
    private static final Logger log = LoggerFactory.getLogger(ActivityPackageConsistency.class);

    @BeforeDelete(entityClass = ActivityPackage.class)
    public void ensureActivityTypeHasNoActivities(Key id) throws InconsistentModelStateException {
        try {
            ActivityPackage activityPackage = new ActivityPackageDao().get(id);
            if (activityPackage.getExecutionCount() != 0) {
                throw new InconsistentModelStateException("Cannot delete activity package with executions! " +
                        "id=" + id + " (" + activityPackage.getExecutionCount() + " executions).");
            }
        } catch (EntityNotFoundException e) {
            log.error("Activity package not found", e);
            throw new InconsistentModelStateException(e.getMessage());
        }
    }
}