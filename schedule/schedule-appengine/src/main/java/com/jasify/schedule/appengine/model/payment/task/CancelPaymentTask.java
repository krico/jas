package com.jasify.schedule.appengine.model.payment.task;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wszarmach
 * @since 06/05/15.
 */
public class CancelPaymentTask implements DeferredTask {
    private static final Logger log = LoggerFactory.getLogger(CancelPaymentTask.class);

    private final Key paymentId;

    public CancelPaymentTask(Key paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public void run() {
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        try {
            Payment payment = paymentService.getPayment(paymentId);
            if (!payment.getState().isFinal()) {
                PaymentServiceFactory.getPaymentService().cancelPayment(payment);
                log.info("Cancelled Payment: " + paymentId);
            }
        } catch (Exception e) {
            // All we can do is log
            log.error("Failed to cancel shopping cart", e);
        }
    }
}
