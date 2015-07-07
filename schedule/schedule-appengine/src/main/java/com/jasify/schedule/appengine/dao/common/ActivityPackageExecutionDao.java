package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;

/**
 * @author szarmawa
 * @since 21/06/15.
 */
public class ActivityPackageExecutionDao extends BaseCachingDao<ActivityPackageExecution> {
    public ActivityPackageExecutionDao() {
        super(ActivityPackageExecutionMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull ActivityPackageExecution entity, @Nonnull Key userId) throws ModelException {
        if (entity.getId() == null) {
            entity.setId(Datastore.allocateId(userId, getMeta()));
            entity.getUserRef().setKey(userId);
        }

        return super.save(entity);
    }
}
