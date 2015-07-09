package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author szarmawa
 * @since 07/06/15.
 */
public class ActivityDao extends BaseCachingDao<Activity> {

    public ActivityDao() {
        super(ActivityMeta.get());
    }

    public List<Activity> getByActivityTypeId(Key activityTypeId) {
        ActivityMeta meta = getMeta();
        return query(new ByActivityTypeQuery(meta, activityTypeId));
    }

    public List<Activity> getByOrganizationId(Key organizationId) {
        ActivityMeta meta = getMeta();
        return query(new ByOrganizationQuery(meta, organizationId));
    }

    @Nonnull
    public Key save(@Nonnull Activity entity, @Nonnull Key activityTypeId) throws ModelException {
        preSave(entity, activityTypeId);
        return super.save(entity);
    }

    @Nonnull
    public List<Key> save(@Nonnull List<Activity> entities, @Nonnull Key activityTypeId) throws ModelException {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        for (Activity entity : entities) {
            preSave(entity, activityTypeId);
        }

        return super.save(entities);
    }

    private void preSave(Activity entity, Key activityTypeId) throws EntityNotFoundException, FieldValueException {
        if (entity.getStart() == null) throw new FieldValueException("Activity.start");
        if (entity.getStart().getTime() < System.currentTimeMillis()) throw new FieldValueException("Activity.start");
        if (entity.getFinish() == null) throw new FieldValueException("Activity.finish");
        if (entity.getFinish().getTime() < entity.getStart().getTime())
            throw new FieldValueException("Activity.finish");
        if (entity.getPrice() != null && entity.getPrice() < 0) throw new FieldValueException("Activity.price");
        if (entity.getMaxSubscriptions() < 0) throw new FieldValueException("Activity.maxSubscriptions");

        ActivityType activityType = entity.getActivityTypeRef().getModel();

        String name = StringUtils.trimToNull(entity.getName());
        if (StringUtils.isBlank(name)) {
            entity.setName(activityType.getName());
        }

        if (entity.getId() == null) {
            // New Activity
            Key organizationId = activityTypeId.getParent();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
    }

    private static class ByActivityTypeQuery extends BaseDaoQuery<Activity, ActivityMeta> {
        public ByActivityTypeQuery(ActivityMeta meta, Key activityTypeId) {
            super(meta, new Serializable[]{activityTypeId});
        }

        @Override
        public List<Key> execute() {
            Key activityTypeId = parameters.get(0);
            return Datastore.query(meta)
                    .filter(meta.activityTypeRef.equal(activityTypeId)).asKeyList();
        }
    }

    private static class ByOrganizationQuery extends BaseDaoQuery<Activity, ActivityMeta> {
        public ByOrganizationQuery(ActivityMeta meta, Key organizationId) {
            super(meta, new Serializable[]{organizationId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId).asKeyList();
        }
    }
}
