package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.model.activity.Subscription;
import org.slim3.datastore.Datastore;

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

    public List<Subscription> getByActivity(final Key activityId) {
        SubscriptionMeta meta = getMeta();
        return query(new BaseDaoQuery<Subscription, SubscriptionMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta)
                        .filter(meta.activityRef.equal(activityId))
                        .asKeyList();
            }
        });
    }
}
