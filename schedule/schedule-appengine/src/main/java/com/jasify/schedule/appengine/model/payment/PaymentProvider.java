package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;

import java.util.Map;

/**
 * @author krico
 * @since 14/02/15.
 */
public interface PaymentProvider<T extends Payment> {
    void createPayment(T payment, GenericUrl baseUrl) throws PaymentException;

    void executePayment(T payment) throws PaymentException;
}
