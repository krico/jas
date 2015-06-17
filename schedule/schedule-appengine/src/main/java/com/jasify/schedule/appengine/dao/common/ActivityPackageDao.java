package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityPackageDao extends BaseCachingDao<ActivityPackage> {
    public ActivityPackageDao() {
        super(ActivityPackageMeta.get());
    }

    public List<ActivityPackage> getByOrganization(final Key organizationId) {
        ActivityPackageMeta meta = getMeta();
        return query(new BaseDaoQuery<ActivityPackage, ActivityPackageMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta)
                        .filter(meta.organizationRef.equal(organizationId))
                        .sort(meta.created.desc).asKeyList();
            }
        });
    }
}