package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.*;
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


    /**
     * @param activityPackage to be created
     * @param activities      that are allowed in this package
     * @return the key to the new package
     * @throws FieldValueException     if fields are invalid
     */
    Key addActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws FieldValueException;

    /**
     * This method updates the activityPackage and the activities at once
     *
     * @param activityPackage to be updated
     * @param activities      new list of activities for this package
     * @return the updated activity package
     * @throws EntityNotFoundException
     * @throws FieldValueException     if fields are invalid
     */
    ActivityPackage updateActivityPackage(ActivityPackage activityPackage, List<Activity> activities) throws FieldValueException, EntityNotFoundException;


    /**
     * Subscribe a user for an activity
     *
     * @param user     to subscribe
     * @param activityId to subscribe to
     * @return a newly created Subscription for this user to this activity
     * @throws OperationException        if activity is fully subscribed
     * @throws EntityNotFoundException
     */
    @Nonnull
    Subscription subscribe(User user, Key activityId) throws OperationException, EntityNotFoundException;

    /**
     * @param user            who is subscribing
     * @param activityPackage tha package used to aqcuire the activities
     * @param activities      activities to subscribe to
     * @return the execution of this subscription
     * @throws EntityNotFoundException   if any of the entities don't exist
     * @throws OperationException        if activity is fully subscribed
     *                                   if there are more activities then allowed by the package
     * @throws IllegalArgumentException  if any of the activities is not part of the package
     */
    ActivityPackageExecution subscribe(User user, ActivityPackage activityPackage, List<Activity> activities) throws EntityNotFoundException, OperationException, IllegalArgumentException;

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
    void cancelActivityPackageExecution(ActivityPackageExecution activityPackageExecution) throws EntityNotFoundException;
}
