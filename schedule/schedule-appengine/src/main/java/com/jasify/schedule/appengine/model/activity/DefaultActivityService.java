package com.jasify.schedule.appengine.model.activity;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.meta.activity.*;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.consistency.ConsistencyGuard;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.CompositeCriterion;
import org.slim3.datastore.Datastore;

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

    private final ActivityPackageActivityMeta activityPackageActivityMeta;

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ActivityPackageActivityDao activityPackageActivityDao = new ActivityPackageActivityDao();
    private final ActivityPackageExecutionDao activityPackageExecutionDao = new ActivityPackageExecutionDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();

    private DefaultActivityService() {
        activityPackageActivityMeta = ActivityPackageActivityMeta.get();
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

                    subscription.getActivityRef().setKey(activity.getId());
                    subscription.getUserRef().setKey(user.getId());

                    activity.setSubscriptionCount(activity.getSubscriptionCount() + 1);
                    activity.getSubscriptionListRef().getModelList().add(subscription);

                    activityDao.save(activity);
                    subscriptionDao.save(subscription, user.getId());

                    // TODO Add this user to the organizations client list
                    tx.commit();
                    return subscription;
                }
            });
        } catch (ModelException e) {
            log.error("Failed to subscribe User={} to Activity={}", user.getId(), activity.getId()); // TODO: Replace with event recorder
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
        UserServiceFactory.getUserService().getUser(userId);

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

                    execution.getActivityPackageRef().setKey(activityPackageId);
                    activityPackageExecutionDao.save(execution, userId);
                    activityPackageDao.save(activityPackage);

                    Key organizationId = Preconditions.checkNotNull(activityPackage.getOrganizationRef().getKey());

                    List<Key> packageActivityKeys = Lists.transform(Datastore
                            .query(tx, activityPackageActivityMeta, organizationId)
                            .filter(activityPackageActivityMeta.activityPackageRef.equal(activityPackageId))
                            .asList(), APA_TO_ACTIVITY_KEY_FUNCTION);

                    for (Key activityId : activityIds) {
                        Preconditions.checkArgument(packageActivityKeys.contains(activityId));
                    }

                    List<Activity> activities = activityDao.get(activityIds);
                    for (Activity activity : activities) {
                        int subscriptionCount = activity.getSubscriptionCount();
                        if (activity.getMaxSubscriptions() > 0 && subscriptionCount >= activity.getMaxSubscriptions()) {
                            throw new OperationException("Activity[" + activity.getId() + "] fully subscribed");
                        }

                        activity.setSubscriptionCount(subscriptionCount + 1);

                        ActivityPackageSubscription subscription = new ActivityPackageSubscription();
                        subscription.getActivityPackageExecutionRef().setKey(execution.getId());
                        subscription.getActivityRef().setKey(activity.getId());
                        subscriptionDao.save(subscription, userId);
                        activityDao.save(activity);
                    }

                    // TODO Add this user to the organizations client list
                    tx.commit();
                    return execution;
                }
            });
        } catch (EntityNotFoundException | OperationException e) {
            log.error("Failed to subscribe User={} to ActivityPackage={}", userId, activityPackageId); // TODO: Replace with event recorder
            throw e;
        } catch (ModelException e) {
            log.error("Failed to subscribe User={} to ActivityPackage={}", userId, activityPackageId); // TODO: Replace with event recorder
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
                    activityPackageDao.save(activityPackage);

                    Key userId = execution.getUserRef().getKey();
                    List<Key> subscriptionIds = Datastore.query(tx, ActivityPackageSubscriptionMeta.get(), userId)
                            .filter(ActivityPackageSubscriptionMeta.get().activityPackageExecutionRef.equal(activityPackageExecution.getId()))
                            .asKeyList();

                    for (Key subscriptionId : subscriptionIds) {
                        cancelSubscription(tx, subscriptionId);
                    }

                    ConsistencyGuard.beforeDelete(ActivityPackageExecution.class, execution.getId());
                    activityPackageExecutionDao.delete(execution.getId());
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

    private void cancelSubscription(Transaction tx, Key subscriptionId) throws ModelException {
        if (subscriptionId == null) throw new EntityNotFoundException("Subscription id=NULL");
        Subscription dbSubscription = subscriptionDao.get(subscriptionId);
        Activity dbActivity = activityDao.get(dbSubscription.getActivityRef().getKey());
        dbActivity.setSubscriptionCount(dbActivity.getSubscriptionCount() - 1);
        activityDao.save(dbActivity);
        ConsistencyGuard.beforeDelete(Subscription.class, subscriptionId);
        subscriptionDao.delete(subscriptionId);
    }


    @Override
    public Key addActivityPackage(final ActivityPackage activityPackage, List<Activity> activities) throws FieldValueException {
        final Key organizationId = activityPackage.getOrganizationRef().getKey();

        if (activityPackage.getItemCount() <= 0) {
            throw new FieldValueException("ActivityPackage.itemCount");
        }
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

        final List<ActivityPackageActivity> models = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityPackageActivity junction = new ActivityPackageActivity(activityPackage, activity);
            junction.setId(Datastore.allocateId(organizationId, activityPackageActivityMeta));
            models.add(junction);
        }
        TransactionOperator.executeNoEx(new ModelOperation<Void>() {
            @Override
            public Void execute(Transaction tx) throws ModelException {
                activityPackageDao.save(activityPackage);
                activityPackageActivityDao.save(models);
                tx.commit();
                return null;
            }
        });

        return activityPackage.getId();
    }

    @Override
    public ActivityPackage updateActivityPackage(final ActivityPackage activityPackage, final List<Activity> activities) throws FieldValueException, EntityNotFoundException {
        if (activityPackage.getItemCount() <= 0) throw new FieldValueException("ActivityPackage.itemCount");
        if (activities.isEmpty() || activities.size() == 1 || activities.size() < activityPackage.getItemCount()) {
            throw new FieldValueException("ActivityPackage.activities.size");
        }
        try {
            return TransactionOperator.execute(new ModelOperation<ActivityPackage>() {
                @Override
                public ActivityPackage execute(Transaction tx) throws ModelException {
                    Key activityPackageId = activityPackage.getId();
                    ActivityPackage dbActivityPackage = activityPackageDao.get(activityPackageId);
                    List<Key> newKeys = Lists.transform(activities, ACTIVITY_TO_KEY_FUNCTION);

                    List<ActivityPackageActivity> activityPackageActivities = activityPackageActivityDao.getByActivityPackageId(activityPackageId);
                    Set<Key> existingKeys = new HashSet<>();
                    for (ActivityPackageActivity activityPackageActivity : activityPackageActivities) {
                        existingKeys.add(activityPackageActivity.getActivityRef().getKey());
                    }

                    Key organizationId = dbActivityPackage.getOrganizationRef().getKey();

                    BeanUtil.copyPropertiesExcluding(dbActivityPackage, activityPackage,
                            "id", "created", "modified", "executionCount", "organizationRef", "activityPackageActivityListRef");

                    Set<Key> toAdd = new HashSet<>();
                    for (Key newKey : newKeys) {
                        if (existingKeys.contains(newKey)) continue;
                        toAdd.add(newKey);
                    }

                    for (Key activityId : toAdd) {
                        ActivityPackageActivity activityPackageActivity = new ActivityPackageActivity();
                        activityPackageActivity.getActivityRef().setKey(activityId);
                        activityPackageActivity.getActivityPackageRef().setKey(activityPackageId);
                        activityPackageActivityDao.save(activityPackageActivity, dbActivityPackage.getId());
                    }
                    activityPackageDao.save(dbActivityPackage);

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

    private static class Singleton {
        private static final ActivityService INSTANCE = new DefaultActivityService();
    }
}
