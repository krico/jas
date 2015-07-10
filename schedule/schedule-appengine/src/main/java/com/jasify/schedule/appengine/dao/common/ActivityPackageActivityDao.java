package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityPackageActivityDao extends BaseCachingDao<ActivityPackageActivity> {
    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();

    public ActivityPackageActivityDao() {
        super(ActivityPackageActivityMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull ActivityPackageActivity entity, @Nonnull Key activityPackageId) throws ModelException {
        if (entity.getId() == null) {
            entity.getActivityPackageRef().setKey(activityPackageId);
        }
        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull ActivityPackageActivity entity) throws ModelException {
        Preconditions.checkNotNull(entity.getActivityPackageRef().getKey(), "ActivityPackageActivity must have activityPackageRef");
        if(entity.getId() == null) {
            ActivityPackage activityPackage = activityPackageDao.get(entity.getActivityPackageRef().getKey());
            Key organizationId = activityPackage.getOrganizationRef().getKey();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<ActivityPackageActivity> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (ActivityPackageActivity entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    public List<ActivityPackageActivity> getByActivityId(Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        Activity activity = activityDao.get(activityId);
        if (activity.getActivityTypeRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key activityTypeId = activity.getActivityTypeRef().getKey();
        ActivityType activityType = activityTypeDao.get(activityTypeId);
        if (activityType.getOrganizationRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key organizationId = activityType.getOrganizationRef().getKey();

        return query(new ByOrganizationAndActivityQuery(meta, organizationId, activityId));
    }

    public Key getKeyByActivityPackageIdAndActivityId(Key activityPackageId, Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
        Key organizationId = activityPackage.getOrganizationRef().getKey();

        List<Key> result = queryKeys(new ByActivityPackageAndActivityQuery(meta, organizationId, activityPackageId, activityId));

        // There can be only one
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new PreparedQuery.TooManyResultsException();
        }

        return result.get(0);
    }

    public ActivityPackageActivity getByActivityPackageIdAndActivityId(Key activityPackageId, Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
        Key organizationId = activityPackage.getOrganizationRef().getKey();

        List<ActivityPackageActivity> result = query(new ByActivityPackageAndActivityQuery(meta, organizationId, activityPackageId, activityId));

        // There can be only one
        if (result.isEmpty()) {
            return null;
        } else if (result.size() > 1) {
            throw new PreparedQuery.TooManyResultsException();
        }

        return result.get(0);
    }

    public List<Key> getKeysByActivityPackageId(Key activityPackageId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
        Key organizationId = activityPackage.getOrganizationRef().getKey();

        return queryKeys(new ByOrganizationAndActivityPackageQuery(meta, organizationId, activityPackageId));
    }

    public List<ActivityPackageActivity> getByActivityPackageId(Key activityPackageId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
        Key organizationId = activityPackage.getOrganizationRef().getKey();

        return query(new ByOrganizationAndActivityPackageQuery(meta, organizationId, activityPackageId));
    }

    private static class ByOrganizationAndActivityQuery extends BaseDaoQuery<ActivityPackageActivity, ActivityPackageActivityMeta> {
        public ByOrganizationAndActivityQuery(ActivityPackageActivityMeta meta, Key organizationId, Key activityId) {
            super(meta, new Serializable[]{organizationId, activityId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            Key activityId = parameters.get(1);
            return Datastore.query(
                    Datastore.getCurrentTransaction(), meta, organizationId)
                    .filter(meta.activityRef.equal(activityId))
                    .asKeyList();
        }
    }

    private static class ByActivityPackageAndActivityQuery extends BaseDaoQuery<ActivityPackageActivity, ActivityPackageActivityMeta> {
        public ByActivityPackageAndActivityQuery(ActivityPackageActivityMeta meta, Key organizationId, Key activityPackageId, Key activityId) {
            super(meta, new Serializable[]{organizationId, activityPackageId, activityId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            Key activityPackageId = parameters.get(1);
            Key activityId = parameters.get(2);
            return Datastore
                    .query(Datastore.getCurrentTransaction(), meta, organizationId)
                    .filter(meta.activityPackageRef.equal(activityPackageId),
                            meta.activityRef.equal(activityId))
                    .asKeyList();
        }
    }

    private static class ByOrganizationAndActivityPackageQuery extends BaseDaoQuery<ActivityPackageActivity, ActivityPackageActivityMeta> {
        public ByOrganizationAndActivityPackageQuery(ActivityPackageActivityMeta meta, Key organizationId, Key activityPackageId) {
            super(meta, new Serializable[]{organizationId, activityPackageId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            Key activityPackageId = parameters.get(1);
            return Datastore
                    .query(Datastore.getCurrentTransaction(), meta, organizationId)
                    .filter(meta.activityPackageRef.equal(activityPackageId))
                    .asKeyList();
        }
    }
}