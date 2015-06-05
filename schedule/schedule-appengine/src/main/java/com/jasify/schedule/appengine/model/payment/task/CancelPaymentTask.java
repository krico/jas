package com.jasify.schedule.appengine.model.payment.task;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
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
                if (executePayment(payment)) {
                    log.info("Payment is paid: " + paymentId);
                } else {
                    PaymentServiceFactory.getPaymentService().cancelPayment(payment);
                    log.info("Cancelled Payment: " + paymentId);
                }
            }
        } catch (EntityNotFoundException | PaymentException e) {
            // All we can do is log
            log.error("Failed to cancel shopping cart", e);
        }
    }

    private boolean executePayment(Payment payment) {
        try {
            if (payment.getType() == PaymentTypeEnum.PayPal) {
                PayPalPayment payPalPayment = (PayPalPayment) payment;
                String payerId = PayPalPaymentProvider.instance().getPayerId(payPalPayment);
                if (payerId != null) {
                    // If we have a payerId it means that the user logged into PayPal but does not guarantee that the payment was authorised
                    PaymentService paymentService = PaymentServiceFactory.getPaymentService();
                    payPalPayment.setPayerId(payerId);
                    // If the payment is not authorised a PaymentException will be thrown
                    paymentService.executePayment(PayPalPaymentProvider.instance(), payPalPayment);
                    BalanceServiceFactory.getBalanceService().payment(payPalPayment);
                    return true;
                }
            }
        } catch (PaymentException | EntityNotFoundException e) {
            log.error("Failed to complete payment", e);
        }
        return false;
    }
}
