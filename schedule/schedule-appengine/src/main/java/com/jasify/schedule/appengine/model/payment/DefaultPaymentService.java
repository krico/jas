package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.payment.task.CancelPaymentTask;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflowEngine;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflowException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 11/01/15.
 */
class DefaultPaymentService implements PaymentService {
    private static final Logger log = LoggerFactory.getLogger(DefaultPaymentService.class);

    private static final int DEFAULT_CANCEL_TASK_DELAY_IN_MILLISECONDS = 1800000;

    private final PaymentMeta paymentMeta;

    private DefaultPaymentService() {
        paymentMeta = PaymentMeta.get();
    }

    static PaymentService instance() {
        return Singleton.INSTANCE;
    }

    @Nonnull
    @Override
    public <T extends Payment> Key newPayment(long userId, T payment, List<PaymentWorkflow> workflowList) {
        Key userKey = Datastore.createKey(UserMeta.get(), userId);
        payment.getUserRef().setKey(userKey);
        return newPayment(userKey, payment, workflowList);
    }

    @Nonnull
    @Override
    public <T extends Payment> Key newPayment(Key parentKey, T payment, List<PaymentWorkflow> workflowList) {
        Preconditions.checkArgument(payment.getId() == null, "newPayment cannot have an id");
        payment.validate();

        Key paymentId = Datastore.allocateId(parentKey, paymentMeta);
        payment.setId(paymentId);

        for (PaymentWorkflow paymentWorkflow : workflowList) {
            payment.linkWorkflow(paymentWorkflow);
        }

        ArrayList<Object> all = new ArrayList<>();
        all.addAll(workflowList);
        all.add(payment);
        List<Key> keys = Datastore.put(all);
        return keys.get(keys.size() - 1);
    }

    private <T extends Payment> void transitionWorkflowList(T payment) throws PaymentException {
        //force blank
        payment.getWorkflowListRef().clear();

        StringBuilder exceptions = new StringBuilder();
        List<PaymentWorkflow> modelList = payment.getWorkflowListRef().getModelList();

        if (modelList.isEmpty()) return;

        for (PaymentWorkflow workflow : modelList) {
            try {
                PaymentWorkflowEngine.transition(workflow, payment.getState());
            } catch (PaymentWorkflowException e) {
                log.error("Failed to transition workflow for payment: {}", payment.getId(), e);
                //TODO: What are we supposed to do here?
                exceptions.append(e).append('\n');
            }
        }

        if (exceptions.length() != 0) {
            throw new PaymentException(exceptions.toString());
        }
    }

    private void queueCancelTask(Transaction tx, Key paymentId) {
        // If the transaction completes add a task to cancel the shopping cart to cancel the ShoppingCart
        ApplicationData applicationData = ApplicationData.instance();
        String cancelTaskDelayInMillisecondsKey = PaymentService.class.getName() + ".CancelTaskDelayInMilliseconds";
        Integer countdownMillis = applicationData.getPropertyWithDefaultValue(cancelTaskDelayInMillisecondsKey, DEFAULT_CANCEL_TASK_DELAY_IN_MILLISECONDS);
        Queue queue = QueueFactory.getQueue("payment-queue");
        queue.add(tx, TaskOptions.Builder.withPayload(new CancelPaymentTask(paymentId)).countdownMillis(countdownMillis));
    }

    @Override
    public <T extends Payment> void createPayment(PaymentProvider<T> provider, T payment, GenericUrl baseUrl) throws EntityNotFoundException, PaymentException {
        Preconditions.checkNotNull(payment.getId(), "PaymentService.newPayment first");
        Preconditions.checkNotNull(provider);
        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());
            if (dbPayment == null) {
                throw new EntityNotFoundException("Payment id=" + payment.getId());
            }
            if (dbPayment.getState() != PaymentStateEnum.New) {
                throw new PaymentException("Payment.State: " + dbPayment.getState() + " (expected New)");
            }
            provider.createPayment(payment, baseUrl);
            Datastore.put(tx, payment);

            queueCancelTask(tx, dbPayment.getId());

            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }

        transitionWorkflowList(payment);
    }

    @Override
    public <T extends Payment> void executePayment(PaymentProvider<T> provider, T payment) throws EntityNotFoundException, PaymentException {
        Preconditions.checkNotNull(provider);
        Preconditions.checkNotNull(payment.getId());
        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());

            if (dbPayment == null) {
                throw new EntityNotFoundException("Payment id=" + payment.getId());
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

        transitionWorkflowList(payment);
    }

    @Override
    public <T extends Payment> void cancelPayment(T payment) throws EntityNotFoundException, PaymentException {
        Preconditions.checkNotNull(payment.getId());

        Transaction tx = Datastore.beginTransaction();
        try {
            Payment dbPayment = Datastore.getOrNull(tx, paymentMeta, payment.getId());

            if (dbPayment == null) {
                throw new EntityNotFoundException("Payment id=" + payment.getId());
            }

            if (dbPayment.getState() == PaymentStateEnum.Canceled) {
                log.warn("Cancelling a canceled payment", new Throwable()); // How did this happen
                return;
            }

            if (dbPayment.getState() != null && dbPayment.getState().isFinal()) {
                throw new IllegalStateException("Payment[" + dbPayment.getId() + "] is already in a final state: " + dbPayment.getState());
            }
            dbPayment.setState(PaymentStateEnum.Canceled);
            Datastore.put(tx, dbPayment);
            tx.commit();
            /*
             Must also update the payment object. It is forwarded to transitionWorkflowList call and
             our current model always reflects the current state in the passed in object
             */
            payment.setState(PaymentStateEnum.Canceled);
            log.info("Payment canceled id={}", dbPayment.getId());
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }

        transitionWorkflowList(payment);
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
