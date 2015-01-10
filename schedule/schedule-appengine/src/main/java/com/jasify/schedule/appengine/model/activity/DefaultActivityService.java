package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author krico
 * @since 09/01/15.
 */
class DefaultActivityService implements ActivityService {
    private final ActivityTypeMeta activityTypeMeta;
    private final ActivityMeta activityMeta;
    private final OrganizationMeta organizationMeta;

    private DefaultActivityService() {
        activityTypeMeta = ActivityTypeMeta.get();
        activityMeta = ActivityMeta.get();
        organizationMeta = OrganizationMeta.get();
    }

    static ActivityService instance() {
        return Singleton.INSTANCE;
    }

    private Organization getOrganization(Key id) throws EntityNotFoundException {
        try {
            return Datastore.get(organizationMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Organization id=" + id);
        }
    }

    @Nonnull
    @Override
    public Key addActivityType(Organization organization, ActivityType activityType) throws EntityNotFoundException, UniqueConstraintException, FieldValueException {
//        Organization dbOrganization = getOrganization(organization.getId());
        return null;
    }

    @Nonnull
    @Override
    public ActivityType getActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException {
        return null;
    }

    @Nonnull
    @Override
    public ActivityType getActivityType(Organization organization, String name) throws EntityNotFoundException {
        return null;
    }

    @Nonnull
    @Override
    public List<ActivityType> getActivityTypes(Organization organization) throws EntityNotFoundException {
        return null;
    }

    @Nonnull
    @Override
    public ActivityType updateActivityType(ActivityType activityType) throws EntityNotFoundException, FieldValueException, UniqueConstraintException {
        return null;
    }

    @Override
    public void removeActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException {

    }

    @Nonnull
    @Override
    public Key addActivity(Activity activity) throws EntityNotFoundException, FieldValueException {
        return null;
    }

    @Nonnull
    @Override
    public Activity getActivity(Key id) throws EntityNotFoundException, IllegalArgumentException {
        return null;
    }

    @Nonnull
    @Override
    public List<Activity> getActivities(Organization organization) throws EntityNotFoundException {
        return null;
    }

    @Nonnull
    @Override
    public List<Activity> getActivities(ActivityType activityType) throws EntityNotFoundException {
        return null;
    }

    @Nonnull
    @Override
    public Activity updateActivity(Activity activity) throws EntityNotFoundException, FieldValueException {
        return null;
    }

    @Override
    public void removeActivity(Key id) throws EntityNotFoundException, IllegalArgumentException {

    }

    private static class Singleton {
        private static final ActivityService INSTANCE = new DefaultActivityService();
    }
}
