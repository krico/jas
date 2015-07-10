package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityPackageDao extends BaseCachingDao<ActivityPackage> {

    public ActivityPackageDao() {
        super(ActivityPackageMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull ActivityPackage entity, @Nonnull Key organizationId) throws ModelException {
        if (entity.getId() == null) {
            entity.getOrganizationRef().setKey(organizationId);
        }
        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull ActivityPackage entity) throws ModelException {
        Preconditions.checkNotNull(entity.getOrganizationRef().getKey(), "ActivityPackage must have organizationRef");
        if(entity.getId() == null) {
            Key organizationId = entity.getOrganizationRef().getKey();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<ActivityPackage> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (ActivityPackage entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    public List<ActivityPackage> getByOrganization(Key organizationId) {
        ActivityPackageMeta meta = getMeta();
        return query(new ByOrganizationQuery(meta, organizationId));
    }

    private static class ByOrganizationQuery extends BaseDaoQuery<ActivityPackage, ActivityPackageMeta> {
        public ByOrganizationQuery(ActivityPackageMeta meta, Key organizationId) {
            super(meta, new Serializable[]{organizationId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            return Datastore.query(
                    Datastore.getCurrentTransaction(), meta, organizationId)
                    .sort(meta.created.desc).asKeyList();
        }
    }
}