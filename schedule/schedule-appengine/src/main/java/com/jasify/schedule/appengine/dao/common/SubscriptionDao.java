package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
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
            entity.getUserRef().setKey(userId);
        }
        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull Subscription entity) throws ModelException {
        Preconditions.checkNotNull(entity.getUserRef().getKey(), "Subscription must have userRef");
        if (entity.getId() == null) {
            Key userId = entity.getUserRef().getKey();
            entity.setId(Datastore.allocateId(userId, getMeta()));
        }

        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<Subscription> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (Subscription entity : entities) {
            result.add(save(entity));
        }
        return result;
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
