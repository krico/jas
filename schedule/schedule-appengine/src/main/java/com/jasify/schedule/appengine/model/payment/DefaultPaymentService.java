package com.jasify.schedule.appengine.model.payment;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.activity.Subscription;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 11/01/15.
 */
class DefaultPaymentService implements PaymentService {
    private final PaymentMeta paymentMeta;

    private DefaultPaymentService() {
        paymentMeta = PaymentMeta.get();
    }

    static PaymentService instance() {
        return Singleton.INSTANCE;
    }

    @Nonnull
    @Override
    public Key addPayment(Subscription subscription, Payment payment) throws EntityNotFoundException, FieldValueException {
        return null;
    }

    @Nonnull
    @Override
    public Payment getPayment(Key id) throws EntityNotFoundException {
        return null;
    }

    @Nonnull
    @Override
    public Payment updatePayment(Payment payment) throws EntityNotFoundException, FieldValueException {
        return null;
    }

    @Override
    public void removePayment(Key id) throws EntityNotFoundException, IllegalArgumentException {

    }

    private static class Singleton {
        private static final PaymentService INSTANCE = new DefaultPaymentService();
    }

}
