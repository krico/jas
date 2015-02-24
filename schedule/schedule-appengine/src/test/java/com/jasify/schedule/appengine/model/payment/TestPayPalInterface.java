package com.jasify.schedule.appengine.model.payment;

import com.jasify.schedule.appengine.TestService;
import org.easymock.EasyMock;

/**
 * @author krico
 * @since 18/02/15.
 */
public class TestPayPalInterface implements TestService {
    private PayPalPaymentProvider.PayPalInterface payPalMock;

    public void setUp() {
        payPalMock = EasyMock.createMock(PayPalPaymentProvider.PayPalInterface.class);
        PayPalPaymentProvider.instance().setPayPalInterface(payPalMock);
    }

    public void replay() {
        EasyMock.replay(payPalMock);
    }

    public PayPalPaymentProvider.PayPalInterface getPayPalMock() {
        return payPalMock;
    }

    public void tearDown() {
        EasyMock.verify(payPalMock);
        PayPalPaymentProvider.instance().setPayPalInterface(null);
    }
}
