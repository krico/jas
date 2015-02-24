package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 11/01/15.
 */
class DefaultPaymentService implements PaymentService {
    private final PaymentMeta paymentMeta;
    private final SubscriptionMeta subscriptionMeta;

    private DefaultPaymentService() {
        paymentMeta = PaymentMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
    }

    static PaymentService instance() {
        return Singleton.INSTANCE;
    }

    @Nonnull
    @Override
    public <T extends Payment> Key newPayment(long userId, T payment) throws PaymentException {
        Key userKey = Datastore.createKey(UserMeta.get(), userId);
        payment.getUserRef().setKey(userKey);
        return newPayment(userKey, payment);
    }

    @Nonnull
    @Override
    public <T extends Payment> Key newPayment(Key parentKey, T payment) throws PaymentException {
        payment.validate();

        payment.setId(Datastore.allocateId(parentKey, paymentMeta));

        return Datastore.put(payment);
    }

    @Override
    public <T extends Payment> void createPayment(PaymentProvider<T> provider, T payment, GenericUrl baseUrl) throws PaymentException {
        Preconditions.checkNotNull(payment.getId(), "PaymentService.newPayment first");
        Preconditions.checkNotNull(provider);
        provider.createPayment(payment, baseUrl);
        Datastore.put(payment);
    }

    @Override
    public <T extends Payment> void executePayment(PaymentProvider<T> provider, T payment) throws PaymentException {
        Preconditions.checkNotNull(provider);
        provider.executePayment(payment);
        Datastore.put(payment);
    }

    @Nonnull
    @Override
    public Payment getPayment(Key id) throws EntityNotFoundException {
        try {
            return Datastore.get(paymentMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Payment id=" + id);
        }
    }

    private static class Singleton {
        private static final PaymentService INSTANCE = new DefaultPaymentService();
    }

}
