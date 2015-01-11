package com.jasify.schedule.appengine.model.payment;

/**
 * @author krico
 * @since 11/01/15.
 */
public class PaymentServiceFactory {
    private static PaymentService instance;

    protected PaymentServiceFactory() {
    }

    public static PaymentService getPaymentService() {
        if (instance == null)
            return DefaultPaymentService.instance();
        return instance;
    }

    protected static void setInstance(PaymentService instance) {
        PaymentServiceFactory.instance = instance;
    }
}
