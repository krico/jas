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
    public static final int MaximumRepeatCounter = 25;
    
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
    public ActivityType getActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param organization in which to get it
     * @param name         of the activity type
     * @return the activity type with that name in that organization
     * @throws EntityNotFoundException if not found, or if the organization doesn't eist
     */
    @Nonnull
    public ActivityType getActivityType(Organization organization, String name) throws EntityNotFoundException;

    /**
     * @param organization in which to search
     * @return the list of activity type in that organization
     * @throws EntityNotFoundException if that organization doesn't exist
     */
    @Nonnull
    public List<ActivityType> getActivityTypes(Organization organization) throws EntityNotFoundException;

    public List<ActivityType> getActivityTypes(Key organizationId) throws EntityNotFoundException;

    /**
     * @param activityType to be updated
     * @return the updated ActivityType
     * @throws EntityNotFoundException   if it didn't exist
     * @throws FieldValueException       if any of the fields is invalid
     * @throws UniqueConstraintException if you are changing the name to a name that already exists within the organization
     */
    @Nonnull
    public ActivityType updateActivityType(ActivityType activityType) throws EntityNotFoundException, FieldValueException, UniqueConstraintException;

    /**
     * @param id of the activityType
     * @throws EntityNotFoundException  if the activityType doesn't exist
     * @throws IllegalArgumentException if the id is not of an ActivityType
     */
    public void removeActivityType(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param activity to add
     * @param repeatDetails information on adding repeat activities
     * @return list of ids of the added activities
     * @throws EntityNotFoundException if not found
     * @throws FieldValueException     if any fields are invalid
     */
    @Nonnull
    public List<Key> addActivity(Activity activity, RepeatDetails repeatDetails) throws EntityNotFoundException, FieldValueException;

    /**
     * @param id to fetch
     * @return the activity
     * @throws EntityNotFoundException  not found
     * @throws IllegalArgumentException the id is not of an Activity
     */
    @Nonnull
    public Activity getActivity(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param organization to search within
     * @return all activities in that organization
     * @throws EntityNotFoundException if the organization doesn't exist
     */
    @Nonnull
    public List<Activity> getActivities(Organization organization) throws EntityNotFoundException;

    /**
     * Get all activities of a certain type
     *
     * @param activityType to search by
     * @return all activities of this type
     * @throws EntityNotFoundException if the type doesn't exist
     */
    @Nonnull
    public List<Activity> getActivities(ActivityType activityType) throws EntityNotFoundException;

    /**
     * @param activity to updated
     * @return the updated activity
     * @throws EntityNotFoundException if it doesn't exist
     * @throws FieldValueException     if any of the fields are invalid
     */
    @Nonnull
    public Activity updateActivity(Activity activity) throws EntityNotFoundException, FieldValueException;

    /**
     * @param id of the activity to remove
     * @throws EntityNotFoundException  if the activity doesn't exist
     * @throws IllegalArgumentException if the id is not of an Activity
     */
    public void removeActivity(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * Subscribe a user for an activity
     *
     * @param user     to subscribe
     * @param activity to subscribe to
     * @return a newly created Subscription for this user to this activity
     * @throws EntityNotFoundException if any of the entities don't exist
     * @throws UniqueConstraintException user was already subscribed to this activity
     * @throws OperationException if activity is fully subscribed
     */
    @Nonnull
    public Subscription subscribe(User user, Activity activity) throws EntityNotFoundException, UniqueConstraintException, OperationException;

    /**
     * Subscribe a user for an activity
     *
     * @param userId     to subscribe
     * @param activityId to subscribe to
     * @return a newly created Subscription for this user to this activity
     * @throws EntityNotFoundException  if any of the entities don't exist
     * @throws IllegalArgumentException if any key is invalid
     * @throws UniqueConstraintException user was already subscribed to this activity
     * @throws OperationException if activity is fully subscribed
     */
    @Nonnull
    public Subscription subscribe(Key userId, Key activityId) throws EntityNotFoundException, IllegalArgumentException, UniqueConstraintException, OperationException;

    /**
     * List subscriptions for an activity
     *
     * @param activity to get the subscriptions from
     * @return list of subscriptions
     * @throws EntityNotFoundException if the activity doesn't exist
     */
    @Nonnull
    public List<Subscription> getSubscriptions(Activity activity) throws EntityNotFoundException;

    /**
     * List subscriptions for an activity
     *
     * @param activityId to get the subscriptions from
     * @return list of subscriptions
     * @throws EntityNotFoundException  if the activity doesn't exist
     * @throws IllegalArgumentException if the key is invalid
     */
    @Nonnull
    public List<Subscription> getSubscriptions(Key activityId) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * Cancel a subscription, effectively doing the reverse of {@link #subscribe}
     *
     * @param subscription to cancel
     * @throws EntityNotFoundException if any of the involved entities don't exist
     */
    public void cancel(Subscription subscription) throws EntityNotFoundException;

    /**
     * Cancel a subscription, effectively doing the reverse of {@link #subscribe}
     *
     * @param subscriptionId to cancel
     * @throws EntityNotFoundException if any of the involved entities don't exist
     */
    public void cancel(Key subscriptionId) throws EntityNotFoundException;
}
