package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author szarmawa
 * @since 07/06/15.
 */
public class ActivityTypeDao extends BaseCachingDao<ActivityType> {

    public ActivityTypeDao() {
        super(ActivityTypeMeta.get());
    }

    public boolean exists(String lcName, Organization organization) {
        ActivityTypeMeta meta = getMeta();
        return Datastore.query(Datastore.getCurrentTransaction(), meta, organization.getId())
                .filter(meta.lcName.equal(StringUtils.lowerCase(lcName)))
                .count() > 0;
    }

    public List<ActivityType> getAll() {
        ActivityTypeMeta meta = getMeta();
        return query(new BaseDaoQuery<ActivityType, ActivityTypeMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta).asKeyList();
            }
        });
    }

    public List<ActivityType> getByOrganization(final Key organizationId) {
        ActivityTypeMeta meta = getMeta();
        return query(new BaseDaoQuery<ActivityType, ActivityTypeMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId).asKeyList();
            }
        });
    }
}