package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.slim3.datastore.CompositeCriterion;
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

    public ActivityPackageActivityDao() {
        super(ActivityPackageActivityMeta.get());
    }

    public List<ActivityPackageActivity> getBy(final Activity activity) {
        ActivityPackageActivityMeta meta = getMeta();
        try {
            ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
            final Key organisationKey = activityType.getOrganizationRef().getKey();
            return query(new BaseDaoQuery<ActivityPackageActivity, ActivityPackageActivityMeta>(meta, new Serializable[0]) {
                @Override
                public List<Key> execute() {
                    return Datastore.query(meta, organisationKey)
                            .filter(new CompositeCriterion(meta,
                                    Query.CompositeFilterOperator.AND,
                                    meta.activityRef.equal(activity.getId()))).asKeyList();
                }
            });
        } catch (EntityNotFoundException e) {
            // Is this possible?
            return Collections.emptyList();
        }
    }
}