package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;

import java.util.List;

/**
 * @author wszarmach
 * @since 01/07/15.
 */
@SuppressWarnings("unused")
public class ActivityTypeConsistency implements EntityConsistency<ActivityType> {
    @BeforeDelete(entityClass = ActivityType.class)
    public void ensureActivityTypeHasNoActivities(Key id) throws InconsistentModelStateException {
        List<Activity> activities = new ActivityDao().getByActivityTypeId(id);
        if (!activities.isEmpty()) {
            throw new InconsistentModelStateException("Cannot delete activity type with activities! " +
                    "id=" + id + " (" + activities.size() + " activities).");
        }
    }
}
