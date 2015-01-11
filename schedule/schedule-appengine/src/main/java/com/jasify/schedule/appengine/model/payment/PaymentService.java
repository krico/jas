package com.jasify.schedule.appengine.model.payment;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.activity.Subscription;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 11/01/15.
 */
public interface PaymentService {
    /**
     * Add a new payment to a subscription
     *
     * @param subscription this payment is for
     * @param payment      that is to be persisted
     * @return the key to the newly added payment
     * @throws EntityNotFoundException if the subscription doesn't exist
     * @throws FieldValueException     if fields are wrong, or if the subscription already has a payment
     */
    @Nonnull
    public Key addPayment(Subscription subscription, Payment payment) throws EntityNotFoundException, FieldValueException;

    /**
     * @param id for the payment
     * @return the payment
     * @throws EntityNotFoundException if the payment doesn't exist
     * @throws IllegalArgumentException if the key is not of a Payment
     */
    @Nonnull
    public Payment getPayment(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param payment to be updated
     * @return the updated payment
     * @throws EntityNotFoundException if this payment doesn't exist
     * @throws FieldValueException     if any fields are invalid
     */
    @Nonnull
    public Payment updatePayment(Payment payment) throws EntityNotFoundException, FieldValueException;


    /**
     * @param id of the payment to remove
     * @throws EntityNotFoundException  if the payment doesn't exist
     * @throws IllegalArgumentException if the id is not of a Payment
     */
    public void removePayment(Key id) throws EntityNotFoundException, IllegalArgumentException;
}
