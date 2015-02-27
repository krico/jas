package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 11/01/15.
 */
public interface PaymentService {
    @Nonnull
    <T extends Payment> Key newPayment(long userId, T payment) throws PaymentException;

    @Nonnull
    <T extends Payment> Key newPayment(Key parentKey, T payment) throws PaymentException;

    <T extends Payment> void createPayment(PaymentProvider<T> provider, T payment, GenericUrl baseUrl) throws PaymentException;

    <T extends Payment> void executePayment(PaymentProvider<T> provider, T payment) throws PaymentException;


    /**
     * @param id for the payment
     * @return the payment
     * @throws EntityNotFoundException  if the payment doesn't exist
     * @throws IllegalArgumentException if the key is not of a Payment
     */
    @Nonnull
    Payment getPayment(Key id) throws EntityNotFoundException, IllegalArgumentException;

    Payment cancelPayment(Payment payment) throws EntityNotFoundException, PaymentException;
}
