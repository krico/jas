package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import org.junit.Before;
import org.junit.Test;

public class PayPalPaymentProviderTest {
    private PayPalPaymentProvider provider;

    @Before
    public void create() {
        provider = PayPalPaymentProvider.instance();
    }

    @Test
    public void testCreatePayment() throws Exception {
        PayPalPayment jasPayment = new PayPalPayment();

        jasPayment.setAmount(20d);
        jasPayment.setCurrency("CHF");

        provider.createPayment(jasPayment, new GenericUrl("http://localhost:1/"));
        String payerId = "TODO";
        jasPayment.setPayerId(payerId);
        provider.executePayment(jasPayment);
        System.err.println(jasPayment);
    }
}