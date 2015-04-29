package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.meta.activity.*;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType;
import com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.CompositeCriterion;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author krico
 * @since 09/01/15.
 */
class DefaultActivityService implements ActivityService {
    private static final Logger log = LoggerFactory.getLogger(DefaultActivityService.class);
    private static final Function<Activity, Key> ACTIVITY_TO_KEY_FUNCTION = new Function<Activity, Key>() {
        @Nullable
        @Override
        public Key apply(Activity activity) {
            return activity.getId();
        }
    };

    private static final Function<ActivityPackageActivity, Key> APA_TO_ACTIVITY_KEY_FUNCTION = new Function<ActivityPackageActivity, Key>() {
        @Nullable
        @Override
        public Key apply(ActivityPackageActivity activity) {
            return activity.getActivityRef().getKey();
        }
    };

    private final ActivityTypeMeta activityTypeMeta;
    private final ActivityMeta activityMeta;
    private final RepeatDetailsMeta repeatDetailsMeta;
    private final OrganizationMeta organizationMeta;
    private final UserMeta userMeta;
    private final SubscriptionMeta subscriptionMeta;
    private final ActivityPackageMeta activityPackageMeta;
    private final ActivityPackageActivityMeta activityPackageActivityMeta;
    private final ActivityPackageExecutionMeta activityPackageExecutionMeta;

    private DefaultActivityService() {
        activityTypeMeta = ActivityTypeMeta.get();
        activityMeta = ActivityMeta.get();
        repeatDetailsMeta = RepeatDetailsMeta.get();
        organizationMeta = OrganizationMeta.get();
        userMeta = UserMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
        activityPackageMeta = ActivityPackageMeta.get();
        activityPackageActivityMeta = ActivityPackageActivityMeta.get();
        activityPackageExecutionMeta = ActivityPackageExecutionMeta.get();
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

    private boolean isActivityTypeNameUnique(Transaction tx, Key organizationId, String name) {
        return Datastore.query(tx, activityTypeMeta, organizationId)
                .filter(activityTypeMeta.lcName.equal(StringUtils.lowerCase(name)))
                .asKeyList()
                .isEmpty();
    }

    private void validateActivity(Activity activity) throws FieldValueException {
        if (activity.getStart() == null) throw new FieldValueException("Activity.start");
        if (activity.getStart().getTime() < System.currentTimeMillis()) throw new FieldValueException("Activity.start");
        if (activity.getFinish() == null) throw new FieldValueException("Activity.finish");
        if (activity.getFinish().getTime() < activity.getStart().getTime())
            throw new FieldValueException("Activity.finish");
        if (activity.getPrice() != null && activity.getPrice() < 0) throw new FieldValueException("Activity.price");
        if (activity.getMaxSubscriptions() < 0) throw new FieldValueException("Activity.maxSubscriptions");
    }

    private void validateRepeatDetails(RepeatDetails repeatDetails) throws FieldValueException {
        if (repeatDetails.getRepeatType() == null) throw new FieldValueException("RepeatDetails.repeatType");
        if (repeatDetails.getRepeatType() != RepeatType.No) {
            if (repeatDetails.getRepeatEvery() <= 0) throw new FieldValueException("RepeatDetails.repeatEvery");
            if (repeatDetails.getRepeatUntilType() == null)
                throw new FieldValueException("RepeatDetails.repeatUntilType");
            if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Count && repeatDetails.getUntilCount() <= 0)
                throw new FieldValueException("RepeatDetails.untilCount");
            if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Date) {
                if (repeatDetails.getUntilDate() == null)
                    throw new FieldValueException("RepeatDetails.untilDate");
                if (repeatDetails.getUntilDate().getTime() < System.currentTimeMillis())
                    throw new FieldValueException("RepeatDetails.untilDate");
            }
        }
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

            activityType.getOrganizationRef().setKey(organizationId);

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
                if (!isActivityTypeNameUnique(tx, dbActivityType.getOrganizationRef().getKey(), name)) {
                    throw new UniqueConstraintException("ActivityType.name=" + name);
                }
            }
            dbActivityType.setDescription(activityType.getDescription());
            dbActivityType.setPrice(activityType.getPrice());
            dbActivityType.setCurrency(activityType.getCurrency());
            dbActivityType.setLocation(activityType.getLocation());
            dbActivityType.setMaxSubscriptions(activityType.getMaxSubscriptions());
            Datastore.put(tx, dbActivityType);
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }

        return dbActivityType;
    }

    @Override
    public void removeActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException, OperationException {
        ActivityType activityType = getActivityType(id);
        if (!getActivitiesByActivityTypeId(id).isEmpty())
            throw new OperationException("ActivityType has activities");
        Datastore.delete(activityType.getId());
    }

    @Nonnull
    @Override
    public List<Key> addActivity(Activity activity, RepeatDetails repeatDetails) throws EntityNotFoundException, FieldValueException {
        Key activityTypeId = activity.getActivityTypeRef().getKey();
        validateActivity(activity);
        if (activityTypeId == null) throw new FieldValueException("Activity.activityType");
        if (repeatDetails == null) {
            repeatDetails = new RepeatDetails();
        } else {
            validateRepeatDetails(repeatDetails);
        }

        ActivityType activityType = getActivityType(activityTypeId);

        switch (repeatDetails.getRepeatType()) {
            case Daily:
                return addActivityRepeatTypeDaily(activity, repeatDetails, activityType);
            case Weekly:
                return addActivityRepeatTypeWeekly(activity, repeatDetails, activityType);
            case No:
                activity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), activityMeta));
                return Arrays.asList(Datastore.put(activity));
            default: // Safety check in case someone adds a new RepeatType but forgets to update this method
                throw new FieldValueException("activity.repeatDetails.repeatType");
        }
    }

    private List<Key> addActivityRepeatTypeDaily(Activity activity, RepeatDetails repeatDetails, ActivityType activityType) throws EntityNotFoundException, FieldValueException {
        DateTime start = new DateTime(activity.getStart());
        DateTime finish = new DateTime(activity.getFinish());

        List<Key> result = null;
        Transaction tx = Datastore.beginTransaction();
        try {
            repeatDetails.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), repeatDetailsMeta));
            Datastore.put(tx, repeatDetails);
            activity.setRepeatDetailsRef(repeatDetails);
            List<Activity> activities = new ArrayList<>();
            while (activities.size() < MaximumRepeatCounter) {
                Activity newActivity = new Activity(activityType);
                BeanUtil.copyProperties(newActivity, activity);
                newActivity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), activityMeta));
                newActivity.setStart(start.toDate());
                newActivity.setFinish(finish.toDate());
                activities.add(newActivity);

                start = start.plusDays(repeatDetails.getRepeatEvery());
                finish = finish.plusDays(repeatDetails.getRepeatEvery());

                if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                    break;
                if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Date && finish.toDate().getTime() > repeatDetails.getUntilDate().getTime())
                    break;
            }

            if (!activities.isEmpty()) {
                result = Datastore.put(tx, activities);
                tx.commit();
            }
        } finally {
            if (tx.isActive()) tx.rollback();
        }

        return result;
    }

    private Set<Integer> getRepeatDays(RepeatDetails repeatDetails) {
        Set<Integer> result = new HashSet<>();
        if (repeatDetails.isMondayEnabled()) result.add(DateTimeConstants.MONDAY);
        if (repeatDetails.isTuesdayEnabled()) result.add(DateTimeConstants.TUESDAY);
        if (repeatDetails.isWednesdayEnabled()) result.add(DateTimeConstants.WEDNESDAY);
        if (repeatDetails.isThursdayEnabled()) result.add(DateTimeConstants.THURSDAY);
        if (repeatDetails.isFridayEnabled()) result.add(DateTimeConstants.FRIDAY);
        if (repeatDetails.isSaturdayEnabled()) result.add(DateTimeConstants.SATURDAY);
        if (repeatDetails.isSundayEnabled()) result.add(DateTimeConstants.SUNDAY);
        return result;
    }

    // TODO: Computer says this is too complex so should get broken up
    private List<Key> addActivityRepeatTypeWeekly(Activity activity, RepeatDetails repeatDetails, ActivityType activityType) throws EntityNotFoundException, FieldValueException {
        Set<Integer> repeatDays = getRepeatDays(repeatDetails);
        if (repeatDays.isEmpty()) throw new FieldValueException("RepeatDetails.repeatDays");

        int repeatEvery = 0;
        if (repeatDetails.getRepeatEvery() > 1) {
            repeatEvery = (repeatDetails.getRepeatEvery() - 1) * 7;
        }

        DateTime start = new DateTime(activity.getStart());
        DateTime finish = new DateTime(activity.getFinish());

        // Find the next chosen day
        for (int day = 0; day < 7; day++) {
            if (repeatDays.contains(start.getDayOfWeek())) {
                break;
            }
            start = start.plusDays(1);
            finish = finish.plusDays(1);
        }

        List<Key> result = null;
        Transaction tx = Datastore.beginTransaction();
        try {
            repeatDetails.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), repeatDetailsMeta));
            Datastore.put(tx, repeatDetails);
            activity.setRepeatDetailsRef(repeatDetails);

            List<Activity> activities = new ArrayList<>();
            boolean repeatCompleted = false;
            while (!repeatCompleted && activities.size() < MaximumRepeatCounter) {
                // Run through 7 days per week
                for (int day = 0; day < 7 && !repeatCompleted; day++) {
                    // Its one of the chosen days
                    if (repeatDays.contains(start.getDayOfWeek())) {
                        Activity newActivity = new Activity(activityType);
                        BeanUtil.copyProperties(newActivity, activity);
                        newActivity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), activityMeta));
                        newActivity.setStart(start.toDate());
                        newActivity.setFinish(finish.toDate());
                        activities.add(newActivity);
                    }
                    // Move to the next day
                    start = start.plusDays(1);
                    finish = finish.plusDays(1);

                    if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Count && activities.size() == repeatDetails.getUntilCount())
                        repeatCompleted = true;
                    if (repeatDetails.getRepeatUntilType() == RepeatUntilType.Date && finish.toDate().getTime() > repeatDetails.getUntilDate().getTime())
                        repeatCompleted = true;
                }
                // Jump to next period
                if (repeatEvery > 0) {
                    start = start.plusDays(repeatEvery);
                    finish = finish.plusDays(repeatEvery);
                }
            }

            if (!activities.isEmpty()) {
                result = Datastore.put(tx, activities);
                tx.commit();
            }
        } finally {
            if (tx.isActive()) tx.rollback();
        }
        return result;
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
        return getActivitiesByActivityTypeId(activityType.getId());
    }

    private List<Activity> getActivitiesByActivityTypeId(Key id) throws EntityNotFoundException {
        return Datastore.query(activityMeta)
                .filter(activityMeta.activityTypeRef.equal(id))
                .asList();
    }

    @Nonnull
    @Override
    public Activity updateActivity(Activity activity) throws EntityNotFoundException, FieldValueException {
        validateActivity(activity);
        Activity dbActivity = getActivity(activity.getId());
        BeanUtil.copyPropertiesExcluding(dbActivity, activity, "created", "modified", "id", "activityTypeRef");
        Datastore.put(dbActivity);
        return dbActivity;
    }

    @Override
    public void removeActivity(Key id) throws EntityNotFoundException, IllegalArgumentException, OperationException {
        Activity dbActivity = getActivity(id);
        if (!dbActivity.getSubscriptionListRef().getModelList().isEmpty())
            throw new OperationException("Activity has subscriptions");
        Datastore.delete(id);
    }

    @Nonnull
    @Override
    public Subscription subscribe(User user, Activity activity) throws EntityNotFoundException, UniqueConstraintException, OperationException {
        Subscription subscription = subscribe(user.getId(), activity.getId());
        activity.setSubscriptionCount(activity.getSubscriptionCount() + 1);
        return subscription;
    }

    @Nonnull
    @Override
    public Subscription subscribe(Key userId, Key activityId) throws EntityNotFoundException, UniqueConstraintException, OperationException {
        User dbUser = getUser(userId);
        Activity dbActivity = getActivity(activityId);

        List<Subscription> existingSubscriptions = dbActivity.getSubscriptionListRef().getModelList();
        for (Subscription subscription : existingSubscriptions) {
            if (userId.equals(subscription.getUserRef().getKey())) {
                throw new UniqueConstraintException("User already subscribed");
            }
        }

        if (dbActivity.getMaxSubscriptions() > 0 && dbActivity.getSubscriptionCount() >= dbActivity.getMaxSubscriptions()) {
            throw new OperationException("Activity fully subscribed");
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
    public ActivityPackageExecution subscribe(User user, ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, UniqueConstraintException, OperationException, IllegalArgumentException {
        return subscribe(user.getId(), activityPackage.getId(), Lists.transform(activities, ACTIVITY_TO_KEY_FUNCTION));
    }

    @Override
    public ActivityPackageExecution subscribe(Key userId, Key activityPackageId, List<Key> activityIds) throws EntityNotFoundException, UniqueConstraintException, OperationException, IllegalArgumentException {
        Preconditions.checkState(!activityIds.isEmpty(), "Need at least 1 activity to subscribe");
        User user = getUser(userId);
        List<ActivityPackageSubscription> subscriptions = new ArrayList<>(activityIds.size());

        ActivityPackageExecution execution = new ActivityPackageExecution();

        //I will implement this method with transactions, so we can use it as a reference for the other subscribe

        Transaction tx = Datastore.beginTransaction();
        try {
            ActivityPackage activityPackage = Datastore.get(tx, activityPackageMeta, activityPackageId);
            if (activityPackage.getItemCount() < activityIds.size()) {
                throw new OperationException("ActivityPackage[" + activityPackage.getId() + "] itemCount=" +
                        activityPackage.getItemCount() + ", activities.size=" + activityIds.size());
            }
            int executionCount = activityPackage.getExecutionCount();
            if (activityPackage.getMaxExecutions() > 0 && executionCount >= activityPackage.getMaxExecutions()) {
                throw new OperationException("ActivityPackage[" + activityPackage.getId() + "] has reached maxExecutions=" + executionCount);
            }
            activityPackage.setExecutionCount(executionCount + 1);

            execution.setId(Datastore.allocateId(userId, activityPackageExecutionMeta));
            execution.getUserRef().setKey(userId);
            execution.getActivityPackageRef().setKey(activityPackageId);

            Datastore.put(tx, activityPackage, execution);

            Key organizationId = Preconditions.checkNotNull(activityPackage.getOrganizationRef().getKey());

            List<Key> packageActivityKeys = Lists.transform(Datastore
                    .query(tx, activityPackageActivityMeta, organizationId)
                    .filter(activityPackageActivityMeta.activityPackageRef.equal(activityPackageId))
                    .asList(), APA_TO_ACTIVITY_KEY_FUNCTION);

            for (Key activityId : activityIds) {
                Preconditions.checkArgument(packageActivityKeys.contains(activityId));
            }

            List<Activity> activities = getActivities(tx, activityIds);
            for (Activity activity : activities) {
                int subscriptionCount = activity.getSubscriptionCount();
                if (activity.getMaxSubscriptions() > 0 && subscriptionCount >= activity.getMaxSubscriptions()) {
                    throw new OperationException("Activity[" + activity.getId() + "] fully subscribed");
                }
                activity.setSubscriptionCount(subscriptionCount + 1);

                if (isSubscribed(tx, user, activity)) {
                    throw new UniqueConstraintException("User already subscribed to activity: " + activity.getId());
                }

                ActivityPackageSubscription subscription = new ActivityPackageSubscription();
                subscription.setId(Datastore.allocateId(userId, subscriptionMeta));
                subscription.getActivityPackageExecutionRef().setKey(execution.getId());
                subscription.getActivityRef().setKey(activity.getId());
                subscription.getUserRef().setKey(userId);

                Datastore.put(tx, activity, subscription);
                subscriptions.add(subscription);
            }

            tx.commit();
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Not found", e);
        } finally {
            if (tx.isActive()) tx.rollback();
        }

        return execution;
    }

    private boolean isSubscribed(Transaction tx, User user, Activity activity) {
        return !Datastore.query(tx, subscriptionMeta, user.getId())
                .filter(subscriptionMeta.activityRef.equal(activity.getId()))
                .asKeyList().isEmpty();
    }

    public List<Activity> getActivities(Transaction tx, List<Key> activityIds) throws EntityNotFoundException {
        List<Activity> activities;
        try {
            activities = Datastore.get(tx, activityMeta, activityIds);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("ActivityIds: " + activityIds, e);
        }
        return activities;
    }

    @Override
    public void cancel(Subscription subscription) throws EntityNotFoundException {
        cancelSubscription(subscription.getId());
    }

    @Override
    public void cancelSubscription(Key subscriptionId) throws EntityNotFoundException {
        Subscription dbSubscription = getSubscription(subscriptionId);
        Activity dbActivity = getActivity(dbSubscription.getActivityRef().getKey());
        dbActivity.setSubscriptionCount(dbActivity.getSubscriptionCount() - 1);
        Datastore.put(dbActivity);
        Datastore.delete(dbSubscription.getId());
    }

    @Nonnull
    @Override
    public Subscription getSubscription(Key id) throws EntityNotFoundException {
        if (id == null) throw new EntityNotFoundException("Subscription id=NULL");
        try {
            return Datastore.get(subscriptionMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Subscription id=" + id);
        }
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

    @Override
    public Key addActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, FieldValueException {
        Key organizationId = Preconditions.checkNotNull(activityPackage.getOrganizationRef().getKey());
        activities = Preconditions.checkNotNull(activities);

        activityPackage.setId(Datastore.allocateId(organizationId, activityPackageMeta));
        List<Object> models = new ArrayList<>();
        models.add(activityPackage);
        for (Activity activity : activities) {
            ActivityPackageActivity junction = new ActivityPackageActivity(activityPackage, activity);
            junction.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
            models.add(junction);
        }

        Transaction tx = Datastore.beginTransaction();
        try {
            Datastore.put(tx, models);
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
        activityPackage.getActivityPackageActivityListRef().clear();
        return activityPackage.getId();
    }

    @Nonnull
    @Override
    public ActivityPackage getActivityPackage(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(activityPackageMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("ActivityPackage.id=" + id);
        }
    }

    @Override
    public void addActivityToActivityPackage(ActivityPackage activityPackage, Activity activity) throws EntityNotFoundException {
        Transaction tx = Datastore.beginTransaction();
        try {

            Key organizationId = activityPackage.getOrganizationRef().getKey();
            ActivityPackageActivity activityPackageActivity = Datastore
                    .query(tx, activityPackageActivityMeta, organizationId)
                    .filter(new CompositeCriterion(activityPackageActivityMeta,
                            Query.CompositeFilterOperator.AND,
                            activityPackageActivityMeta.activityPackageRef.equal(activityPackage.getId()),
                            activityPackageActivityMeta.activityRef.equal(activity.getId())))
                    .asSingle();

            if (activityPackageActivity == null) {
                activityPackageActivity = new ActivityPackageActivity(activityPackage, activity);
                activityPackageActivity.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
                Datastore.put(tx, activityPackageActivity);
            } else {
                log.warn("Tried to add already existent ap: {} / a: {}", activityPackage.getId(), activity.getId());
            }

            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
    }

    @Override
    public void removeActivityFromActivityPackage(ActivityPackage activityPackage, Activity activity) throws EntityNotFoundException {
        Transaction tx = Datastore.beginTransaction();
        try {

            ActivityPackageActivity activityPackageActivity = Datastore
                    .query(tx, activityPackageActivityMeta, activityPackage.getOrganizationRef().getKey())
                    .filter(new CompositeCriterion(activityPackageActivityMeta,
                            Query.CompositeFilterOperator.AND,
                            activityPackageActivityMeta.activityPackageRef.equal(activityPackage.getId()),
                            activityPackageActivityMeta.activityRef.equal(activity.getId())))
                    .asSingle();

            if (activityPackageActivity == null) {
                log.warn("Tried to remove non existent ap: {} / a: {}", activityPackage.getId(), activity.getId());
            } else {
                Datastore.delete(tx, activityPackageActivity.getId());
            }

            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
    }

    private static class Singleton {
        private static final ActivityService INSTANCE = new DefaultActivityService();
    }
}
