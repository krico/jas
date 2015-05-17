package com.jasify.schedule.appengine.model.payment;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * @author wszarmach
 * @since 17/05/15.
 */
public class PayPalPaymentTest {
    @Test
    public void testType() {
        PayPalPayment payPalPayment = new PayPalPayment();
        assertEquals(PaymentTypeEnum.PayPal, payPalPayment.getType());
        // TODO: Seems suspect to be able to change the Type. Maybe the set method should be overwritten and throw when called
        payPalPayment.setType(PaymentTypeEnum.Cash);
        assertEquals(PaymentTypeEnum.Cash, payPalPayment.getType());
    }

    @Test
    public void testDescribe() {
        PayPalPayment payPalPayment = new PayPalPayment();
        payPalPayment.setExternalId("ExternalId");
        assertEquals(" (PayPal ID=ExternalId)", payPalPayment.describe());
    }

    @Test
    public void testToString() {
        PayPalPayment payPalPayment = new PayPalPayment();
        assertNotNull(payPalPayment.toString());
    }
}
