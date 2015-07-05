package com.jasify.schedule.appengine.model.activity;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageExecutionDao;
import com.jasify.schedule.appengine.dao.common.SubscriptionDao;
import com.jasify.schedule.appengine.meta.activity.*;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.CompositeCriterion;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final ActivityMeta activityMeta;
    private final SubscriptionMeta subscriptionMeta;
    private final ActivityPackageMeta activityPackageMeta;
    private final ActivityPackageActivityMeta activityPackageActivityMeta;
    private final ActivityPackageExecutionMeta activityPackageExecutionMeta;

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ActivityPackageExecutionDao activityPackageExecutionDao = new ActivityPackageExecutionDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();

    private DefaultActivityService() {
        activityMeta = ActivityMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
        activityPackageMeta = ActivityPackageMeta.get();
        activityPackageActivityMeta = ActivityPackageActivityMeta.get();
        activityPackageExecutionMeta = ActivityPackageExecutionMeta.get();
    }

    static ActivityService instance() {
        return Singleton.INSTANCE;
    }

    @Nonnull
    @Override
    public Subscription subscribe(final User user, final Activity activity) throws OperationException {
        if (activity.getMaxSubscriptions() > 0 && activity.getSubscriptionCount() >= activity.getMaxSubscriptions()) {
            throw new OperationException("Activity fully subscribed");
        }

        try {
            return TransactionOperator.execute(new ModelOperation<Subscription>() {
                @Override
                public Subscription execute(Transaction tx) throws ModelException {
                    Subscription subscription = new Subscription();

                    subscription.setId(Datastore.allocateId(user.getId(), subscriptionMeta));
                    subscription.getActivityRef().setKey(activity.getId());
                    subscription.getUserRef().setKey(user.getId());

                    activity.setSubscriptionCount(activity.getSubscriptionCount() + 1);
                    activity.getSubscriptionListRef().getModelList().add(subscription);

                    Datastore.put(tx, activity);
                    Datastore.put(tx, subscription);

                    // TODO Add this user to the organizations client list
                    tx.commit();
                    return subscription;
                }
            });
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public ActivityPackageExecution subscribe(User user, ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, OperationException, IllegalArgumentException {
        return subscribe(user.getId(), activityPackage.getId(), Lists.transform(activities, ACTIVITY_TO_KEY_FUNCTION));
    }

    @Override
    public ActivityPackageExecution subscribe(final Key userId, final Key activityPackageId, final List<Key> activityIds) throws EntityNotFoundException, OperationException, IllegalArgumentException {
        // TODO: This method needs cleanup
        Preconditions.checkState(!activityIds.isEmpty(), "Need at least 1 activity to subscribe");
        if (userId == null) throw new EntityNotFoundException("User id=NULL");
        final User user = UserServiceFactory.getUserService().getUser(userId);

        try {
            //I will implement this method with transactions, so we can use it as a reference for the other subscribe
            return TransactionOperator.execute(new ModelOperation<ActivityPackageExecution>() {
                @Override
                public ActivityPackageExecution execute(Transaction tx) throws ModelException {
                    ActivityPackageExecution execution = new ActivityPackageExecution();

                    ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
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

                        ActivityPackageSubscription subscription = new ActivityPackageSubscription();
                        subscription.setId(Datastore.allocateId(userId, subscriptionMeta));
                        subscription.getActivityPackageExecutionRef().setKey(execution.getId());
                        subscription.getActivityRef().setKey(activity.getId());
                        subscription.getUserRef().setKey(userId);

                        Datastore.put(tx, activity, subscription);
                    }

                    // TODO Add this user to the organizations client list
                    tx.commit();
                    return execution;
                }
            });
        } catch (EntityNotFoundException | OperationException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void cancelActivityPackageExecution(final ActivityPackageExecution activityPackageExecution) throws EntityNotFoundException {
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {

                    ActivityPackageExecution execution = activityPackageExecutionDao.get(activityPackageExecution.getId());
                    ActivityPackage activityPackage = activityPackageDao.get(execution.getActivityPackageRef().getKey());
                    activityPackage.setExecutionCount(activityPackage.getExecutionCount() - 1);
                    Datastore.put(tx, activityPackage);

                    Key userId = execution.getUserRef().getKey();
                    List<Key> subscriptionIds = Datastore.query(tx, ActivityPackageSubscriptionMeta.get(), userId)
                            .filter(ActivityPackageSubscriptionMeta.get().activityPackageExecutionRef.equal(activityPackageExecution.getId()))
                            .asKeyList();

                    for (Key subscriptionId : subscriptionIds) {
                        cancelSubscription(tx, subscriptionId);
                    }

                    Datastore.delete(tx, execution.getId());
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
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
    public void cancelSubscription(final Key subscriptionId) throws EntityNotFoundException {
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    cancelSubscription(tx, subscriptionId);
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    private void cancelSubscription(Transaction tx, Key subscriptionId) throws EntityNotFoundException {
        if (subscriptionId == null) throw new EntityNotFoundException("Subscription id=NULL");
        Subscription dbSubscription = subscriptionDao.get(subscriptionId);
        Activity dbActivity = activityDao.get(dbSubscription.getActivityRef().getKey());
        dbActivity.setSubscriptionCount(dbActivity.getSubscriptionCount() - 1);
        Datastore.put(tx, dbActivity);
        Datastore.delete(tx, subscriptionId);
    }

    @Nonnull
    // TODO   @Override
    public List<Subscription> getSubscriptions(Activity activity) {
        // This assumes that you have the latest version of activity
        return activity.getSubscriptionListRef().getModelList();
    }

    @Override
    public Key addActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws FieldValueException {
        Key organizationId = activityPackage.getOrganizationRef().getKey();

        if (activityPackage.getItemCount() <= 0) throw new FieldValueException("ActivityPackage.itemCount");
        if (activities.isEmpty() || activities.size() == 1 || activities.size() < activityPackage.getItemCount()) {
            throw new FieldValueException("ActivityPackage.activities.size");
        }
        HashSet<Key> activityIds = new HashSet<>();
        for (Activity activity : activities) {
            if (!activityIds.add(activity.getId())) {
                throw new FieldValueException("ActivityPackage.activities has duplicates");
            }
        }
//        if (activityPackage.getValidUntil().getTime() < activityPackage.getValidFrom().getTime()) throw new FieldValueException("ActivityPackage.validUntil");

        activityPackage.setId(Datastore.allocateId(organizationId, activityPackageMeta));
        final List<Object> models = new ArrayList<>();
        models.add(activityPackage);
        for (Activity activity : activities) {
            ActivityPackageActivity junction = new ActivityPackageActivity(activityPackage, activity);
            junction.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
            models.add(junction);
        }
        TransactionOperator.executeNoEx(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                Datastore.put(tx, models);
                tx.commit();
                return null;
            }
        });

        return activityPackage.getId();
    }

    @Override
    public ActivityPackage updateActivityPackage(ActivityPackage activityPackage) throws EntityNotFoundException, FieldValueException {
        ActivityPackage dbActivityPackage = activityPackageDao.get(activityPackage.getId());

        copyProperties(activityPackage, dbActivityPackage);

        Datastore.put(dbActivityPackage);

        return dbActivityPackage;
    }

    private void copyProperties(ActivityPackage source, ActivityPackage destination) {
        BeanUtil.copyPropertiesExcluding(destination, source,
                "id", "created", "modified", "executionCount", "organizationRef", "activityPackageActivityListRef");
    }

    @Override
    public ActivityPackage updateActivityPackage(final ActivityPackage activityPackage, final List<Activity> activities) throws EntityNotFoundException {
        try {
            return TransactionOperator.execute(new ModelOperation<ActivityPackage>() {
                @Override
                public ActivityPackage execute(Transaction tx) throws ModelException {

                    Key activityPackageId = activityPackage.getId();
                    ActivityPackage dbActivityPackage = activityPackageDao.get(activityPackageId);
                    List<Key> newKeys = Lists.transform(activities, ACTIVITY_TO_KEY_FUNCTION);
                    Set<Key> existingKeys = dbActivityPackage.getActivityKeys();

                    Key organizationId = dbActivityPackage.getOrganizationRef().getKey();

                    copyProperties(activityPackage, dbActivityPackage);

                    List<Object> models = new ArrayList<>();
                    models.add(dbActivityPackage);

                    Set<Key> toAdd = new HashSet<>();
                    for (Key newKey : newKeys) {
                        if (existingKeys.contains(newKey)) continue;
                        toAdd.add(newKey);
                    }

                    for (Key activityId : toAdd) {
                        ActivityPackageActivity activityPackageActivity = new ActivityPackageActivity();
                        activityPackageActivity.getActivityRef().setKey(activityId);
                        activityPackageActivity.getActivityPackageRef().setKey(activityPackageId);
                        activityPackageActivity.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
                        models.add(activityPackageActivity);
                    }
                    Datastore.put(tx, models);

                    Set<Key> toRemove = new HashSet<>();
                    for (Key existingKey : existingKeys) {
                        if (newKeys.contains(existingKey)) continue;
                        toRemove.add(existingKey);
                    }

                    Set<Key> junctionsToRemove = new HashSet<>();
                    for (Key activityId : toRemove) {
                        List<Key> keyList = Datastore.query(tx, activityPackageActivityMeta, organizationId)
                                .filter(new CompositeCriterion(activityPackageActivityMeta,
                                        Query.CompositeFilterOperator.AND,
                                        activityPackageActivityMeta.activityPackageRef.equal(activityPackageId),
                                        activityPackageActivityMeta.activityRef.equal(activityId)))
                                .asKeyList();
                        if (keyList.isEmpty())
                            throw new OperationException("Failed to find relation that should exist");
                        junctionsToRemove.addAll(keyList);
                    }
                    Datastore.delete(tx, junctionsToRemove);

                    tx.commit();

                    dbActivityPackage.getActivityPackageActivityListRef().clear();

                    return dbActivityPackage;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void addActivityToActivityPackage(final ActivityPackage activityPackage, final Activity activity) throws EntityNotFoundException {
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    Key organizationId = activityPackageDao.get(activityPackage.getId()).getOrganizationRef().getKey();

                    ActivityPackageActivity activityPackageActivity = Datastore
                            .query(tx, activityPackageActivityMeta, organizationId)
                            .filter(new CompositeCriterion(activityPackageActivityMeta,
                                    Query.CompositeFilterOperator.AND,
                                    activityPackageActivityMeta.activityPackageRef.equal(activityPackage.getId()),
                                    activityPackageActivityMeta.activityRef.equal(activity.getId())))
                            .asSingle();

                    if (activityPackageActivity == null) {
                        activityPackageActivity = new ActivityPackageActivity();
                        activityPackageActivity.getActivityRef().setKey(activity.getId());
                        activityPackageActivity.getActivityPackageRef().setKey(activityPackage.getId());
                        activityPackageActivity.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
                        Datastore.put(tx, activityPackageActivity);
                    } else {
                        log.warn("Tried to add already existent ap: {} / a: {}", activityPackage.getId(), activity.getId());
                    }

                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void removeActivityFromActivityPackage(final ActivityPackage activityPackage, final Activity activity) throws EntityNotFoundException {
        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    Key organizationId = activityPackageDao.get(activityPackage.getId()).getOrganizationRef().getKey();

                    ActivityPackageActivity activityPackageActivity = Datastore
                            .query(tx, activityPackageActivityMeta, organizationId)
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
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    private static class Singleton {
        private static final ActivityService INSTANCE = new DefaultActivityService();
    }
}
