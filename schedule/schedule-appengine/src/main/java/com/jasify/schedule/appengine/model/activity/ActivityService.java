package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author krico
 * @since 09/01/15.
 */
public interface ActivityService {
    /**
     * Maximum number of activities that can be added in one addActivity call
     */
    int MaximumRepeatCounter = 25;

    /**
     * Add an activityType to and organization
     *
     * @param organization to add it to
     * @param activityType to add to it
     * @return the key to the newly added activityType
     * @throws UniqueConstraintException if <code>organization</code> already contains an activityType with the name of the activityType being added.
     * @throws EntityNotFoundException   if the organization doesn't exist.
     * @throws FieldValueException       in case of invalid fields (e.g. empty name)
     */
    @Nonnull
    public Key addActivityType(Organization organization, ActivityType activityType) throws EntityNotFoundException, UniqueConstraintException, FieldValueException;

    /**
     * @param id to fetch
     * @return the activity type with that id
     * @throws EntityNotFoundException  if that activity type doesn't exist
     * @throws IllegalArgumentException if the id is not of an ActivityType
     */
    @Nonnull
    ActivityType getActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param organization in which to get it
     * @param name         of the activity type
     * @return the activity type with that name in that organization
     * @throws EntityNotFoundException if not found, or if the organization doesn't eist
     */
    @Nonnull
    ActivityType getActivityType(Organization organization, String name) throws EntityNotFoundException;

    /**
     * @param organization in which to search
     * @return the list of activity type in that organization
     * @throws EntityNotFoundException if that organization doesn't exist
     */
    @Nonnull
    List<ActivityType> getActivityTypes(Organization organization) throws EntityNotFoundException;

    List<ActivityType> getActivityTypes(Key organizationId) throws EntityNotFoundException;

    /**
     * @param activityType to be updated
     * @return the updated ActivityType
     * @throws EntityNotFoundException   if it didn't exist
     * @throws FieldValueException       if any of the fields is invalid
     * @throws UniqueConstraintException if you are changing the name to a name that already exists within the organization
     */
    @Nonnull
    ActivityType updateActivityType(ActivityType activityType) throws EntityNotFoundException, FieldValueException, UniqueConstraintException;

    /**
     * @param id of the activityType
     * @throws EntityNotFoundException  if the activityType doesn't exist
     * @throws IllegalArgumentException if the id is not of an ActivityType
     * @throws OperationException       if the activityType has activities
     */
    void removeActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException, OperationException;

    /**
     * @param activity      to add
     * @param repeatDetails information on adding repeat activities
     * @return list of ids of the added activities
     * @throws EntityNotFoundException if not found
     * @throws FieldValueException     if any fields are invalid
     */
    @Nonnull
    List<Key> addActivity(Activity activity, RepeatDetails repeatDetails) throws EntityNotFoundException, FieldValueException;

    /**
     * @param id to fetch
     * @return the activity
     * @throws EntityNotFoundException  not found
     * @throws IllegalArgumentException the id is not of an Activity
     */
    @Nonnull
    Activity getActivity(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param organization to search within
     * @return all activities in that organization
     * @throws EntityNotFoundException if the organization doesn't exist
     */
    @Nonnull
    List<Activity> getActivities(Organization organization) throws EntityNotFoundException;

    /**
     * Get all activities of a certain type
     *
     * @param activityType to search by
     * @return all activities of this type
     * @throws EntityNotFoundException if the type doesn't exist
     */
    @Nonnull
    List<Activity> getActivities(ActivityType activityType) throws EntityNotFoundException;

    /**
     * @param activity to updated
     * @return the updated activity
     * @throws EntityNotFoundException if it doesn't exist
     * @throws FieldValueException     if any of the fields are invalid
     */
    @Nonnull
    Activity updateActivity(Activity activity) throws EntityNotFoundException, FieldValueException;

    /**
     * @param id of the activity to remove
     * @throws EntityNotFoundException  if the activity doesn't exist
     * @throws IllegalArgumentException if the id is not of an Activity
     * @throws OperationException       if activity has subscritpions
     */
    void removeActivity(Key id) throws EntityNotFoundException, IllegalArgumentException, OperationException;

    /**
     * @param activityPackage to be created
     * @param activities      that are allowed in this package
     * @return the key to the new package
     * @throws EntityNotFoundException if not found
     * @throws FieldValueException     if fields are invalid
     */
    Key addActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, FieldValueException;

    /**
     * @param activityPackage with the data to be updated
     * @return the updated activityPackage
     * @throws EntityNotFoundException if it doesn't exist
     * @throws FieldValueException     if fields are invalid
     */
    ActivityPackage updateActivityPackage(ActivityPackage activityPackage) throws EntityNotFoundException, FieldValueException;

    /**
     * This method updates the activityPackage and the activities at once
     *
     * @param activityPackage to be updated
     * @param activities      new list of activities for this package
     * @return the updated activity package
     * @throws EntityNotFoundException
     * @throws FieldValueException
     */
    ActivityPackage updateActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, FieldValueException;

    void addActivityToActivityPackage(ActivityPackage activityPackage, Activity activity) throws EntityNotFoundException;

    void addActivityToActivityPackage(Key activityPackageId, Key activityId) throws EntityNotFoundException;

    void removeActivityFromActivityPackage(ActivityPackage activityPackage, Activity activity) throws EntityNotFoundException;

    void removeActivityFromActivityPackage(Key activityPackageId, Key activityId) throws EntityNotFoundException;


    /**
     * @param organizationId to fetch activity packages from
     * @return the list of activity packages for this organization
     */
    List<ActivityPackage> getActivityPackages(Key organizationId) throws EntityNotFoundException;

    /**
     * @param organization to fetch activity packages from
     * @return the list of activity packages for this organization
     */
    List<ActivityPackage> getActivityPackages(Organization organization) throws EntityNotFoundException;

    /**
     * @param id to fetch
     * @return the activityPackage
     * @throws EntityNotFoundException  not found
     * @throws IllegalArgumentException the id is not of an Activity
     */
    @Nonnull
    ActivityPackage getActivityPackage(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * Subscribe a user for an activity
     *
     * @param user     to subscribe
     * @param activity to subscribe to
     * @return a newly created Subscription for this user to this activity
     * @throws EntityNotFoundException   if any of the entities don't exist
     * @throws UniqueConstraintException user was already subscribed to this activity
     * @throws OperationException        if activity is fully subscribed
     */
    @Nonnull
    Subscription subscribe(User user, Activity activity) throws EntityNotFoundException, UniqueConstraintException, OperationException;


    /**
     * Subscribe a user for an activity
     *
     * @param userId     to subscribe
     * @param activityId to subscribe to
     * @return a newly created Subscription for this user to this activity
     * @throws EntityNotFoundException   if any of the entities don't exist
     * @throws IllegalArgumentException  if any key is invalid
     * @throws UniqueConstraintException user was already subscribed to this activity
     * @throws OperationException        if activity is fully subscribed
     */
    @Nonnull
    Subscription subscribe(Key userId, Key activityId) throws EntityNotFoundException, IllegalArgumentException, UniqueConstraintException, OperationException;

    /**
     * @param user            who is subscribing
     * @param activityPackage tha package used to aqcuire the activities
     * @param activities      activities to subscribe to
     * @return the execution of this subscription
     * @throws EntityNotFoundException   if any of the entities don't exist
     * @throws UniqueConstraintException user was already subscribed to any of the activities
     * @throws OperationException        if activity is fully subscribed
     *                                   if there are more activities then allowed by the package
     * @throws IllegalArgumentException  if any of the activities is not part of the package
     */
    ActivityPackageExecution subscribe(User user, ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, UniqueConstraintException, OperationException, IllegalArgumentException;

    /**
     * @param userId            who is subscribing
     * @param activityPackageId tha package used to aqcuire the activities
     * @param activityIds       activities to subscribe to
     * @return the execution of this subscription
     * @throws EntityNotFoundException   if any of the entities don't exist
     * @throws UniqueConstraintException user was already subscribed to any of the activities
     * @throws OperationException        if activity is fully subscribed
     *                                   if there are more activities then allowed by the package
     * @throws IllegalArgumentException  if any of the activities is not part of the package
     *                                   if any of the keys is not the type they should be
     */
    ActivityPackageExecution subscribe(Key userId, Key activityPackageId, List<Key> activityIds) throws EntityNotFoundException, UniqueConstraintException, OperationException, IllegalArgumentException;

    /**
     * List subscriptions for an activity
     *
     * @param activity to get the subscriptions from
     * @return list of subscriptions
     * @throws EntityNotFoundException if the activity doesn't exist
     */
    @Nonnull
    List<Subscription> getSubscriptions(Activity activity) throws EntityNotFoundException;

    /**
     * List subscriptions for an activity
     *
     * @param activityId to get the subscriptions from
     * @return list of subscriptions
     * @throws EntityNotFoundException  if the activity doesn't exist
     * @throws IllegalArgumentException if the key is invalid
     */
    @Nonnull
    List<Subscription> getSubscriptions(Key activityId) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param id of Subscription to get
     * @return Subscription
     * @throws EntityNotFoundException if subscription with id could be found
     */
    @Nonnull
    Subscription getSubscription(Key id) throws EntityNotFoundException;

    /**
     * Cancel a subscription, effectively doing the reverse of {@link #subscribe}
     *
     * @param subscription to cancel
     * @throws EntityNotFoundException if any of the involved entities don't exist
     */
    void cancel(Subscription subscription) throws EntityNotFoundException;

    /**
     * Cancel a subscription, effectively doing the reverse of {@link #subscribe}
     *
     * @param subscriptionId to cancel
     * @throws EntityNotFoundException if any of the involved entities don't exist
     */
    void cancelSubscription(Key subscriptionId) throws EntityNotFoundException;

    /**
     * Cancel an activityPackageExecution, effectively reversing {@link #subscribe(com.jasify.schedule.appengine.model.users.User, ActivityPackage, java.util.List)}
     *
     * @param activityPackageExecution to cancel
     * @throws EntityNotFoundException if eny entity doesn't exist
     */
    void cancel(ActivityPackageExecution activityPackageExecution) throws EntityNotFoundException;

    /**
     * Cancel an activityPackageExecution, effectively reversing {@link #subscribe(com.jasify.schedule.appengine.model.users.User, ActivityPackage, java.util.List)}
     *
     * @param activityPackageExecutionId to cancel
     * @throws EntityNotFoundException if eny entity doesn't exist
     */
    void cancelActivityPackageExecution(Key activityPackageExecutionId) throws EntityNotFoundException;

}
