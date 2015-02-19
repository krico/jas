package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.util.BeanUtil;
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

    private Subscription getSubscription(Key id) throws EntityNotFoundException {
        if (id == null) throw new EntityNotFoundException("Subscription.id=NULL");

        try {
            return Datastore.get(subscriptionMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Subscription id=" + id);
        }
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
    public Key addPayment(Subscription subscription, Payment payment) throws EntityNotFoundException, FieldValueException {
        Subscription dbSubscription = getSubscription(subscription.getId());
        Key userId = dbSubscription.getUserRef().getKey();
        if (userId == null)
            throw new EntityNotFoundException("Subscription.User.Id=NULL");
        if (dbSubscription.getPaymentRef().getKey() != null)
            throw new FieldValueException("Subscription.Payment.Id != NULL");

        payment.setId(Datastore.allocateId(userId, payment.getClass()));
        dbSubscription.getPaymentRef().setModel(payment);

        Datastore.put(payment, dbSubscription);

        return payment.getId();
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

    @Nonnull
    @Override
    public Payment updatePayment(Payment payment) throws EntityNotFoundException, FieldValueException {
        Payment dbPayment = getPayment(payment.getId());
        BeanUtil.copyPropertiesExcluding(dbPayment, payment);
        Datastore.put(dbPayment);
        return dbPayment;
    }

    @Override
    public void removePayment(Key id) throws EntityNotFoundException, IllegalArgumentException {
        Payment dbPayment = getPayment(id);
        Subscription subscription = Datastore.query(subscriptionMeta).filter(subscriptionMeta.paymentRef.equal(id)).asSingle();
        if (subscription != null) subscription.getPaymentRef().setKey(null);
        Datastore.put(subscription);
        Datastore.delete(dbPayment.getId());
    }

    private static class Singleton {
        private static final PaymentService INSTANCE = new DefaultPaymentService();
    }

}
