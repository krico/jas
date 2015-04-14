package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;

/**
 * @author krico
 * @since 14/02/15.
 */
public interface PaymentProvider<T extends Payment> {
    String CANCEL_PATH = "/payment/cancel/";
    String ACCEPT_PATH = "/payment/accept/";

    T newPayment();

    /**
     * Create the payment, should call payment.setState(PaymentStateEnum.Created);
     *
     * @param payment to create
     * @param baseUrl on the request
     * @throws PaymentException if creation fails
     */
    void createPayment(T payment, GenericUrl baseUrl) throws PaymentException;

    void executePayment(T payment) throws PaymentException;
}
