package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageSubscriptionMeta;
import com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author wszarmach
 * @since 07/07/15.
 */
public class ActivityPackageSubscriptionDao extends BaseCachingDao<ActivityPackageSubscription> {
    public ActivityPackageSubscriptionDao() {
        super(ActivityPackageSubscriptionMeta.get());
    }

    public List<ActivityPackageSubscription> getByActivityId(Key activityId) {
        ActivityPackageSubscriptionMeta meta = getMeta();
        return query(new ByActivityPackageAndActivityQuery(meta, activityId));
    }

    public List<ActivityPackageSubscription> getByActivityPackageExecutionId(Key activityPackageExecutionId) {
        return query(new ByActivityPackageExecutionQuery(this.<ActivityPackageSubscriptionMeta>getMeta(), activityPackageExecutionId));
    }

    private static class ByActivityPackageExecutionQuery extends BaseDaoQuery<ActivityPackageSubscription, ActivityPackageSubscriptionMeta> {
        public ByActivityPackageExecutionQuery(ActivityPackageSubscriptionMeta meta, Key activityPackageExecutionId) {
            super(meta, new Serializable[]{activityPackageExecutionId});
        }

        @Override
        public List<Key> execute() {
            Key activityPackageExecutionId = parameters.get(0);
            return Datastore.query(meta)
                    .filter(meta.activityPackageExecutionRef.equal(activityPackageExecutionId))
                    .asKeyList();
        }
    }

    private static class ByActivityPackageAndActivityQuery extends BaseDaoQuery<ActivityPackageSubscription, ActivityPackageSubscriptionMeta> {
        public ByActivityPackageAndActivityQuery(ActivityPackageSubscriptionMeta meta, Key activityId) {
            super(meta, new Serializable[]{activityId});
        }

        @Override
        public List<Key> execute() {
            Key activityId = parameters.get(0);
            return Datastore.query(meta)
                    .filter(meta.activityRef.equal(activityId))
                    .asKeyList();
        }
    }
}
