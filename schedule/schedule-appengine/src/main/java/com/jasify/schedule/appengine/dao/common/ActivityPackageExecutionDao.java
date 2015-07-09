package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
            entity.getUserRef().setKey(userId);
        }
        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull ActivityPackageExecution entity) throws ModelException {
        Preconditions.checkNotNull(entity.getUserRef().getKey(), "ActivityPackageExecution must have userRef");
        if(entity.getId() == null) {
            Key userId = entity.getUserRef().getKey();
            entity.setId(Datastore.allocateId(userId, getMeta()));
        }
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<ActivityPackageExecution> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (ActivityPackageExecution entity : entities) {
            result.add(save(entity));
        }
        return result;
    }
}
