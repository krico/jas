package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author szarmawa
 * @since 07/06/15.
 */
public class ActivityTypeDao extends BaseCachingDao<ActivityType> {

    public ActivityTypeDao() {
        super(ActivityTypeMeta.get());
    }

    public boolean exists(String name, Key organizationId) {
        ActivityTypeMeta meta = getMeta();
        return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId)
                .filter(meta.lcName.equal(StringUtils.lowerCase(name)))
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

    public List<ActivityType> getByOrganization(Key organizationId) {
        ActivityTypeMeta meta = getMeta();
        return query(new ByOrganizationQuery(meta, organizationId));
    }

    public List<Key> getKeysByOrganization(Key organizationId) {
        ActivityTypeMeta meta = getMeta();
        return queryKeys(new ByOrganizationQuery(meta, organizationId));
    }

    @Nonnull
    public Key save(@Nonnull ActivityType entity, @Nonnull Key organizationId) throws ModelException {
        String name = StringUtils.trimToNull(entity.getName());

        if (StringUtils.isBlank(name)) {
            throw new FieldValueException("ActivityType.name");
        }
        // If the trim changed anything
        entity.setName(name);

        if (entity.getId() == null) {
            // New ActivityType
            if (exists(name, organizationId)) {
                throw new UniqueConstraintException("ActivityType.name=" + name + ", Organization.id=" + organizationId);
            }
            entity.getOrganizationRef().setKey(organizationId);
            return save(entity);
        }

        // Update ActivityType
        ActivityType dbActivityType = get(entity.getId());

        if (!StringUtils.equalsIgnoreCase(dbActivityType.getName(), entity.getName())) {
            if (exists(entity.getName(), organizationId)) {
                throw new UniqueConstraintException("ActivityType.name=" + entity.getName() + ", Organization.id=" + organizationId);
            }
        }

        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull ActivityType entity) throws ModelException {
        Preconditions.checkNotNull(entity.getOrganizationRef().getKey(), "ActivityType must have organizationRef");
        if(entity.getId() == null) {
            Key organizationId = entity.getOrganizationRef().getKey();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<ActivityType> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (ActivityType entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    private static class ByOrganizationQuery extends BaseDaoQuery<ActivityType, ActivityTypeMeta> {
        public ByOrganizationQuery(ActivityTypeMeta meta, Key activityId) {
            super(meta, new Serializable[]{activityId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId).asKeyList();
        }
    }
}