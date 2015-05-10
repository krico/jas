package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author krico
 * @since 11/01/15.
 */
public interface PaymentService {
    @Nonnull
    <T extends Payment> Key newPayment(long userId, T payment, List<PaymentWorkflow> workflowList);

    @Nonnull
    <T extends Payment> Key newPayment(Key parentKey, T payment, List<PaymentWorkflow> workflowList);

    <T extends Payment> void createPayment(PaymentProvider<T> provider, T payment, GenericUrl baseUrl) throws EntityNotFoundException, PaymentException;

    <T extends Payment> void executePayment(PaymentProvider<T> provider, T payment) throws EntityNotFoundException, PaymentException;

    <T extends Payment> void cancelPayment(T payment) throws EntityNotFoundException, PaymentException;

    /**
     * @param id for the payment
     * @return the payment
     * @throws EntityNotFoundException  if the payment doesn't exist
     * @throws IllegalArgumentException if the key is not of a Payment
     */
    @Nonnull
    Payment getPayment(Key id) throws EntityNotFoundException, IllegalArgumentException;

}
