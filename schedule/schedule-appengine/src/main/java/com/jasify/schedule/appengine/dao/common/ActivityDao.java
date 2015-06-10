package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author szarmawa
 * @since 07/06/15.
 */
public class ActivityDao extends BaseCachingDao<Activity> {
    public ActivityDao() {
        super(ActivityMeta.get());
    }

    public List<Activity> getBy(final ActivityType activityType) {
        ActivityMeta meta = getMeta();
        return query(new BaseDaoQuery<Activity, ActivityMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta)
                        .filter(meta.activityTypeRef.equal(activityType.getId())).asKeyList();
            }
        });
    }

    public List<Activity> getBy(final Organization organization) {
        ActivityMeta meta = getMeta();
        return query(new BaseDaoQuery<Activity, ActivityMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta, organization.getId()).asKeyList();
            }
        });
    }
}
