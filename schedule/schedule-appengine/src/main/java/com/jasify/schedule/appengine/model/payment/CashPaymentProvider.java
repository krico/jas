package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;

/**
 * @author krico
 * @since 08/04/15.
 */
public class CashPaymentProvider implements PaymentProvider<CashPayment> {
    private CashPaymentProvider() {
    }

    public static CashPaymentProvider instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public CashPayment newPayment() {
        return new CashPayment();
    }

    @Override
    public void createPayment(CashPayment payment, GenericUrl baseUrl) throws PaymentException {
        payment.validate();
        payment.setState(PaymentStateEnum.Created);
    }

    @Override
    public void executePayment(CashPayment payment) throws PaymentException {
        payment.setState(PaymentStateEnum.Completed);
    }

    private static final class Singleton {
        private static final CashPaymentProvider INSTANCE = new CashPaymentProvider();
    }

}
