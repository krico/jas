package com.jasify.schedule.appengine.model.payment;

import com.jasify.schedule.appengine.TestService;
import org.easymock.EasyMock;

/**
 * @author krico
 * @since 11/01/15.
 */
public class TestPaymentServiceFactory extends PaymentServiceFactory implements TestService {
    private PaymentService paymentServiceMock;

    public void setUp() {
        paymentServiceMock = EasyMock.createMock(PaymentService.class);
        setInstance(paymentServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        EasyMock.verify(paymentServiceMock);
        paymentServiceMock = null;
    }

    public PaymentService getPaymentServiceMock() {
        return paymentServiceMock;
    }

    public void replay() {
        EasyMock.replay(paymentServiceMock);
    }
}
