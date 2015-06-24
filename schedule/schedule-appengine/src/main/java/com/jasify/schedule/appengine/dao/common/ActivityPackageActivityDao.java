package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityPackageActivityDao extends BaseCachingDao<ActivityPackageActivity> {
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final ActivityDao activityDao = new ActivityDao();

    public ActivityPackageActivityDao() {
        super(ActivityPackageActivityMeta.get());
    }

    public List<ActivityPackageActivity> getByActivityId(Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        Activity activity = activityDao.get(activityId);
        if (activity.getActivityTypeRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key activityTypeId = activity.getActivityTypeRef().getKey();
        ActivityType activityType = activityTypeDao.get(activityTypeId);
        if (activityType.getOrganizationRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key organizationId = activityType.getOrganizationRef().getKey();

        return query(new ByOrganizationAndActivityQuery(meta, organizationId, activityId));
    }

    private static class ByOrganizationAndActivityQuery extends BaseDaoQuery<ActivityPackageActivity, ActivityPackageActivityMeta> {
        public ByOrganizationAndActivityQuery(ActivityPackageActivityMeta meta, Key organizationId, Key activityId) {
            super(meta, new Serializable[]{organizationId, activityId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            Key activityId = parameters.get(1);
            return Datastore.query(
                    Datastore.getCurrentTransaction(), meta, organizationId)
                    .filter(meta.activityRef.equal(activityId))
                    .asKeyList();
        }
    }
}