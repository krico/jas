package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityTypeMeta;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
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
    private final UserMeta userMeta;
    private final SubscriptionMeta subscriptionMeta;

    private DefaultActivityService() {
        activityTypeMeta = ActivityTypeMeta.get();
        activityMeta = ActivityMeta.get();
        organizationMeta = OrganizationMeta.get();
        userMeta = UserMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
    }

    static ActivityService instance() {
        return Singleton.INSTANCE;
    }


    private Organization getOrganization(Key id) throws EntityNotFoundException {
        if (id == null) throw new EntityNotFoundException("Organization.id=NULL");

        try {
            return Datastore.get(organizationMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Organization id=" + id);
        }
    }

    private User getUser(Key id) throws EntityNotFoundException {
        if (id == null) throw new EntityNotFoundException("User id=NULL");

        try {
            return Datastore.get(userMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("User id=" + id);
        }
    }

    private Subscription getSubscription(Key id) throws EntityNotFoundException {
        if (id == null) throw new EntityNotFoundException("Subscription id=NULL");

        try {
            return Datastore.get(subscriptionMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Subscription id=" + id);
        }
    }

    private boolean isActivityTypeNameUnique(Transaction tx, Key organizationId, String name) {
        return Datastore.query(tx, activityTypeMeta, organizationId)
                .filter(activityTypeMeta.lcName.equal(StringUtils.lowerCase(name)))
                .asKeyList()
                .isEmpty();
    }

    @Nonnull
    @Override
    public Key addActivityType(Organization organization, ActivityType activityType) throws EntityNotFoundException, UniqueConstraintException, FieldValueException {
        String name = StringUtils.trimToNull(activityType.getName());
        if (name == null) {
            throw new FieldValueException("ActivityType.name");
        }

        Organization dbOrganization = getOrganization(organization.getId());

        Key organizationId = dbOrganization.getId();
        Transaction tx = Datastore.beginTransaction();
        try {
            if (!isActivityTypeNameUnique(tx, organizationId, name)) {
                throw new UniqueConstraintException("ActivityType.name=" + name + ", Organization.id=" + organizationId);
            }
            activityType.setId(Datastore.allocateId(organizationId, activityTypeMeta));
            Key ret = Datastore.put(tx, activityType);
            tx.commit();
            return ret;
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
    }

    @Nonnull
    @Override
    public ActivityType getActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(activityTypeMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("ActivityType.id=" + id);
        }
    }

    @Nonnull
    @Override
    public ActivityType getActivityType(Organization organization, String name) throws EntityNotFoundException {
        name = StringUtils.trimToEmpty(name);
        getOrganization(organization.getId());

        ActivityType ret = Datastore.query(activityTypeMeta, organization.getId())
                .filter(activityTypeMeta.lcName.equal(StringUtils.lowerCase(name)))
                .asSingle();

        if (ret == null)
            throw new EntityNotFoundException("ActivityType.name=" + name + " Organization.id=" + organization.getId());

        return ret;
    }

    @Nonnull
    @Override
    public List<ActivityType> getActivityTypes(Organization organization) throws EntityNotFoundException {
        return getActivityTypes(organization.getId());
    }

    @Nonnull
    @Override
    public List<ActivityType> getActivityTypes(Key organizationId) throws EntityNotFoundException {
        Organization organization = getOrganization(organizationId);
        return Datastore.query(activityTypeMeta, organization.getId()).asList();
    }

    @Nonnull
    @Override
    public ActivityType updateActivityType(ActivityType activityType) throws EntityNotFoundException, FieldValueException, UniqueConstraintException {
        String name = StringUtils.trimToNull(activityType.getName());
        if (name == null) {
            throw new FieldValueException("ActivityType.name");
        }
        ActivityType dbActivityType = getActivityType(activityType.getId());
        Transaction tx = Datastore.beginTransaction();
        try {
            if (!StringUtils.equalsIgnoreCase(dbActivityType.getName(), name)) {
                dbActivityType.setName(name);
                if (!isActivityTypeNameUnique(tx, activityType.getId().getParent(), name)) {
                    throw new UniqueConstraintException("ActivityType.name=" + name);
                }
            }
            dbActivityType.setDescription(activityType.getDescription());
            Datastore.put(tx, dbActivityType);
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }

        return dbActivityType;
    }

    @Override
    public void removeActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException {
        ActivityType activityType = getActivityType(id);
        Datastore.delete(activityType.getId());
    }

    @Nonnull
    @Override
    public Key addActivity(Activity activity) throws EntityNotFoundException, FieldValueException {
        Key activityTypeId = activity.getActivityTypeRef().getKey();
        if (activityTypeId == null) throw new FieldValueException("Activity.activityType");

        getActivityType(activityTypeId);

        activity.setId(Datastore.allocateId(activityTypeId.getParent(), activityMeta));

        return Datastore.put(activity);
    }

    @Nonnull
    @Override
    public Activity getActivity(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(activityMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Activity.id=" + id);
        }
    }

    @Nonnull
    @Override
    public List<Activity> getActivities(Organization organization) throws EntityNotFoundException {
        getOrganization(organization.getId());
        return Datastore.query(activityMeta, organization.getId()).asList();
    }

    @Nonnull
    @Override
    public List<Activity> getActivities(ActivityType activityType) throws EntityNotFoundException {
        getActivityType(activityType.getId());
        return Datastore.query(activityMeta)
                .filter(activityMeta.activityTypeRef.equal(activityType.getId()))
                .asList();
    }

    @Nonnull
    @Override
    public Activity updateActivity(Activity activity) throws EntityNotFoundException, FieldValueException {
        Activity dbActivity = getActivity(activity.getId());
        BeanUtil.copyPropertiesExcluding(dbActivity, activity, "created", "modified", "id", "activityTypeRef");
        Datastore.put(dbActivity);
        return dbActivity;
    }

    @Override
    public void removeActivity(Key id) throws EntityNotFoundException, IllegalArgumentException {
        getActivity(id);
        Datastore.delete(id);
    }

    @Nonnull
    @Override
    public Subscription subscribe(User user, Activity activity) throws EntityNotFoundException, UniqueConstraintException {
        Subscription subscription = subscribe(user.getId(), activity.getId());
        activity.setSubscriptionCount(activity.getSubscriptionCount() + 1);
        return subscription;
    }

    @Nonnull
    @Override
    public Subscription subscribe(Key userId, Key activityId) throws EntityNotFoundException, UniqueConstraintException {
        User dbUser = getUser(userId);
        Activity dbActivity = getActivity(activityId);

        List<Subscription> existingSubscriptions = dbActivity.getSubscriptionListRef().getModelList();
        for (Subscription subscription : existingSubscriptions) {
            if (userId.equals(subscription.getUserRef().getKey())) {
                throw new UniqueConstraintException("User already subscribed");
            }
        }
        Subscription subscription = new Subscription();

        subscription.setId(Datastore.allocateId(dbUser.getId(), subscriptionMeta));
        subscription.getActivityRef().setKey(dbActivity.getId());
        subscription.getUserRef().setKey(dbUser.getId());

        //TODO: put this in a transaction
        dbActivity.setSubscriptionCount(dbActivity.getSubscriptionCount() + 1);

        Datastore.put(dbActivity);
        Datastore.put(subscription);

        return subscription;
    }

    @Override
    public void cancel(Subscription subscription) throws EntityNotFoundException {
        Subscription dbSubscription = getSubscription(subscription.getId());
        Activity dbActivity = getActivity(dbSubscription.getActivityRef().getKey());
        dbActivity.setSubscriptionCount(dbActivity.getSubscriptionCount() - 1);
        Datastore.put(dbActivity);
        Datastore.delete(dbSubscription.getId());
    }

    @Nonnull
    @Override
    public List<Subscription> getSubscriptions(Activity activity) throws EntityNotFoundException {
        return getSubscriptions(activity.getId());
    }

    @Nonnull
    @Override
    public List<Subscription> getSubscriptions(Key activityId) throws EntityNotFoundException, IllegalArgumentException {
        Activity dbActivity = getActivity(activityId);
        return dbActivity.getSubscriptionListRef().getModelList();
    }

    private static class Singleton {
        private static final ActivityService INSTANCE = new DefaultActivityService();
    }
}
