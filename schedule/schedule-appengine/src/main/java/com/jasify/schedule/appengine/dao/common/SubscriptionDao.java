package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author szarmawa
 * @since 21/06/15.
 */
public class SubscriptionDao extends BaseCachingDao<Subscription> {
    public SubscriptionDao() {
        super(SubscriptionMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull Subscription entity, @Nonnull Key userId) throws ModelException {
        if (entity.getId() == null) {
            entity.setId(Datastore.allocateId(userId, getMeta()));
            entity.getUserRef().setKey(userId);
        }
        return super.save(entity);
    }

    public List<Subscription> getByActivity(Key activityId) {
        SubscriptionMeta meta = getMeta();
        return query(new ByActivityQuery(meta, activityId));
    }

    private static class ByActivityQuery extends BaseDaoQuery<Subscription, SubscriptionMeta> {
        public ByActivityQuery(SubscriptionMeta meta, Key activityId) {
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
