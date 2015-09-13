package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InvoicePaymentProviderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
        payment.setId(Datastore.allocateId(InvoicePayment.class));
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
    public void testCreatePaymentNoId() throws Exception {
        thrown.expectMessage("Payment.Id == NULL");
        thrown.expect(IllegalArgumentException.class);
        InvoicePaymentProvider provider = InvoicePaymentProvider.instance();
        InvoicePayment payment = provider.newPayment();
        populate(payment);
        payment.setId(null);
        provider.createPayment(payment, new GenericUrl());
    }

    @Test
    public void testCreatePaymentWithAttachment() throws Exception {
        thrown.expectMessage("Payment.Attachment != NULL");
        thrown.expect(IllegalArgumentException.class);
        InvoicePaymentProvider provider = InvoicePaymentProvider.instance();
        InvoicePayment payment = provider.newPayment();
        populate(payment);
        payment.getAttachmentRef().setKey(Datastore.allocateId(Attachment.class));
        provider.createPayment(payment, new GenericUrl());
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