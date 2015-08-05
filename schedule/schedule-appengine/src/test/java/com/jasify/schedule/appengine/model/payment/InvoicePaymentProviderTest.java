package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InvoicePaymentProviderTest {
    @BeforeClass
    public static void setup() {
        TestHelper.initializeJasifyWithOAuthProviderData();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testInstance() throws Exception {
        assertNotNull(InvoicePaymentProvider.instance());
    }

    @Test
    public void testNewPayment() throws Exception {
        InvoicePayment payment = InvoicePaymentProvider.instance().newPayment();
        assertNotNull(payment);
        assertEquals(PaymentStateEnum.New, payment.getState());
    }

    private void populate(InvoicePayment payment) {
        payment.setCurrency("CHF");
        payment.addItem("Test", 1, 1d);
    }

    @Test
    public void testCreatePayment() throws Exception {
        InvoicePaymentProvider provider = InvoicePaymentProvider.instance();
        InvoicePayment payment = provider.newPayment();
        populate(payment);
        provider.createPayment(payment, new GenericUrl());
        assertEquals(PaymentStateEnum.Created, payment.getState());
    }

    @Test
    public void testExecutePayment() throws Exception {
        InvoicePaymentProvider provider = InvoicePaymentProvider.instance();
        InvoicePayment payment = provider.newPayment();
        populate(payment);
        provider.createPayment(payment, new GenericUrl());
        provider.executePayment(payment);
        assertEquals(PaymentStateEnum.Completed, payment.getState());
    }
}