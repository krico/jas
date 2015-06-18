package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
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

    public List<Activity> getByActivityTypeId(final Key activityTypeId) {
        ActivityMeta meta = getMeta();
        return query(new BaseDaoQuery<Activity, ActivityMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta)
                        .filter(meta.activityTypeRef.equal(activityTypeId)).asKeyList();
            }
        });
    }

    public List<Activity> getByOrganizationId(final Key organizationId) {
        ActivityMeta meta = getMeta();
        return query(new BaseDaoQuery<Activity, ActivityMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId).asKeyList();
            }
        });
    }
}
