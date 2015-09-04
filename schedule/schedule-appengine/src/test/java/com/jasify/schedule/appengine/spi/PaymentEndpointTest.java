package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.response.ForbiddenException;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasInvoice;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class PaymentEndpointTest {
    private PaymentEndpoint paymentEndpoint;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        paymentEndpoint = new PaymentEndpoint();
    }

    @After
    public void done() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testGetPaymentInvoice() throws Exception {
        User user = TestHelper.createUser(true);
        JasifyEndpointUser jasifyEndpointUser = newCaller(user.getId().getId());

        InvoicePayment payment = new InvoicePayment();
        Attachment att = new Attachment();
        payment.getUserRef().setModel(user);
        payment.getAttachmentRef().setModel(att);
        Datastore.put(payment, att);

        JasInvoice paymentInvoice = paymentEndpoint.getPaymentInvoice(jasifyEndpointUser, payment.getId());
        assertNotNull(paymentInvoice);
    }

    @Test(expected = ForbiddenException.class)
    public void testGetPaymentsAdminOnly() throws Exception {
        User user = TestHelper.createUser(true);
        JasifyEndpointUser jasifyEndpointUser = newCaller(user.getId().getId());
        paymentEndpoint.getPayments(jasifyEndpointUser, null, null, null);
    }

    @Test
    //TODO: test other cases
    public void testGetPayments() throws Exception {
        User user = TestHelper.createUser(true);
        JasifyEndpointUser jasifyEndpointUser = newAdminCaller(user.getId().getId());

        long now = System.currentTimeMillis();
        Date outside = DateUtils.truncate(new Date(now - HistoryEndpoint.DEFAULT_TIME_WINDOW_MILLIS), Calendar.HOUR);
        outside = new Date(outside.getTime() - 1);

        Payment payment1 = new Payment();
        payment1.setCreated(outside);
        Payment payment2 = new InvoicePayment();

        Datastore.put(payment1, payment2);

        List<Payment> payments = paymentEndpoint.getPayments(jasifyEndpointUser, null, null, null);
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());

    }
}