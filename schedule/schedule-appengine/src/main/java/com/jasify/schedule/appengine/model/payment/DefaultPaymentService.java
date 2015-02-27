package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 11/01/15.
 */
class DefaultPaymentService implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(DefaultPaymentService.class);

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
        Preconditions.checkArgument(payment.getId() == null, "newPayment cannot have an id");
        payment.validate();

        payment.setId(Datastore.allocateId(parentKey, paymentMeta));
        return Datastore.put(payment);
    }

    @Override
    public <T extends Payment> void createPayment(PaymentProvider<T> provider, T payment, GenericUrl baseUrl) throws PaymentException {
        Preconditions.checkNotNull(payment.getId(), "PaymentService.newPayment first");
        Preconditions.checkNotNull(provider);
        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());
            if (dbPayment == null) {
                throw new PaymentException("Payment not found");
            }
            if (dbPayment.getState() != PaymentStateEnum.New) {
                throw new PaymentException("Payment.State: " + dbPayment.getState() + " (expected New)");
            }
            provider.createPayment(payment, baseUrl);
            Datastore.put(tx, payment);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    @Override
    public <T extends Payment> void executePayment(PaymentProvider<T> provider, T payment) throws PaymentException {
        Preconditions.checkNotNull(provider);
        Preconditions.checkNotNull(payment.getId());
        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());

            if (dbPayment == null) {
                throw new PaymentException("Payment not found");
            }

            if (dbPayment.getState() != PaymentStateEnum.Created) {
                throw new PaymentException("Payment.State: " + dbPayment.getState() + " (expected Created)");
            }

            provider.executePayment(payment);
            Datastore.put(tx, payment);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
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

    @Override
    public Payment cancelPayment(Payment payment) throws EntityNotFoundException, PaymentException {
        Preconditions.checkNotNull(payment.getId());

        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());

            if (dbPayment == null) {
                throw new PaymentException("Payment not found");
            }

            if (dbPayment.getState() == PaymentStateEnum.Canceled) {
                return dbPayment;
            }

            if (dbPayment.getState() != null && dbPayment.getState().isFinal()) {
                throw new IllegalStateException("Payment[" + dbPayment.getId() + "] is already in a final state: " + dbPayment.getState());
            }
            dbPayment.setState(PaymentStateEnum.Canceled);
            Datastore.put(tx, dbPayment);
            tx.commit();

            log.info("Payment canceled id={}", dbPayment.getId());
            return dbPayment;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    private static class Singleton {
        private static final PaymentService INSTANCE = new DefaultPaymentService();
    }

}
