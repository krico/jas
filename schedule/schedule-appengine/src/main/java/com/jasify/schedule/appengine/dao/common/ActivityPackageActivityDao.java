package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageActivity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import org.slim3.datastore.CompositeCriterion;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author szarmawa
 * @since 09/06/15.
 */
public class ActivityPackageActivityDao extends BaseCachingDao<ActivityPackageActivity> {

    public ActivityPackageActivityDao() {
        super(ActivityPackageActivityMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull ActivityPackageActivity entity, @Nonnull Key organizationId) throws ModelException {
        if (entity.getId() == null) {
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    public List<ActivityPackageActivity> getByActivityId(Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        Activity activity = new ActivityDao().get(activityId);
        if (activity.getActivityTypeRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key activityTypeId = activity.getActivityTypeRef().getKey();
        ActivityType activityType = new ActivityTypeDao().get(activityTypeId);
        if (activityType.getOrganizationRef().getKey() == null) {
            return Collections.emptyList();
        }

        Key organizationId = activityType.getOrganizationRef().getKey();

        return query(new ByOrganizationAndActivityQuery(meta, organizationId, activityId));
    }

    public Key getKeyByActivityPackageIdAndActivityId(Key activityPackageId, Key activityId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        ActivityPackage activityPackage = new ActivityPackageDao().get(activityPackageId);
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

        Key organizationId = activityPackageId.getParent();

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

        Key organizationId = activityPackageId.getParent();

        return queryKeys(new ByOrganizationAndActivityPackageQuery(meta, organizationId, activityPackageId));
    }

    public List<ActivityPackageActivity> getByActivityPackageId(Key activityPackageId) throws EntityNotFoundException {
        ActivityPackageActivityMeta meta = getMeta();

        Key organizationId = activityPackageId.getParent();

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
                    .filter(new CompositeCriterion(meta,
                            Query.CompositeFilterOperator.AND,
                            meta.activityPackageRef.equal(activityPackageId),
                            meta.activityRef.equal(activityId)))
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