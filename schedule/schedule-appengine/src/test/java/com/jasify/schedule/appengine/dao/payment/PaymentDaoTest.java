package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.*;

public class PaymentDaoTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private PaymentDao paymentDao;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        paymentDao = new PaymentDao();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testInheritance() throws Exception {
        PayPalPayment payPalPayment = new PayPalPayment();
        InvoicePayment invoicePayment = new InvoicePayment();
        List<Key> saved = paymentDao.save(Arrays.asList(payPalPayment, invoicePayment));
        assertEquals(2, saved.size());
        assertEquals(payPalPayment.getId(), saved.get(0));
        assertEquals(invoicePayment.getId(), saved.get(1));
        Payment payment = paymentDao.get(payPalPayment.getId());
        assertTrue(payment instanceof PayPalPayment);
        payment = paymentDao.get(saved.get(1));
        assertTrue(payment instanceof InvoicePayment);
    }

    @Test
    public void testListSince() throws Exception {
        InvoicePayment payment1 = new InvoicePayment();
        payment1.setCreated(new Date(12345));
        PayPalPayment payment2 = new PayPalPayment();
        payment2.setCreated(new Date(payment1.getCreated().getTime() + 1000));
        paymentDao.save(Arrays.asList(payment1, payment2));

        List<Payment> payments = paymentDao.list(new Date(payment1.getCreated().getTime() + 1));
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
    }

    @Test
    public void testListSinceWithState() throws Exception {
        InvoicePayment payment1 = new InvoicePayment();
        payment1.setCreated(new Date(12345));
        PayPalPayment payment2 = new PayPalPayment();
        payment2.setCreated(new Date(payment1.getCreated().getTime() + 1000));
        paymentDao.save(Arrays.asList(payment1, payment2));

        List<Payment> payments = paymentDao.list(new Date(payment1.getCreated().getTime() + 1), PaymentStateEnum.New);
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
        assertTrue(paymentDao.list(new Date(payment1.getCreated().getTime() + 1), PaymentStateEnum.Canceled).isEmpty());
    }

    @Test
    public void testListBetween() throws Exception {
        InvoicePayment payment1 = new InvoicePayment();
        payment1.setCreated(new Date(12345));
        PayPalPayment payment2 = new PayPalPayment();
        payment2.setCreated(new Date(payment1.getCreated().getTime() + 1000));
        PayPalPayment payment3 = new PayPalPayment();
        payment3.setCreated(new Date(payment2.getCreated().getTime() + 1000));
        paymentDao.save(Arrays.asList(payment1, payment2, payment3));

        List<Payment> payments = paymentDao.list(new Date(payment1.getCreated().getTime() + 1), new Date(payment3.getCreated().getTime() - 1));
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
    }

    @Test
    public void testListState() {
        Payment newState = new Payment();
        Payment canceledState = new Payment();
        canceledState.setState(PaymentStateEnum.Canceled);

        Datastore.put(newState, canceledState);

        List<Payment> newList = paymentDao.list(PaymentStateEnum.New);
        assertEquals(1, newList.size());
        assertEquals(newState.getId(), newList.get(0).getId());

        List<Payment> cxlState = paymentDao.list(PaymentStateEnum.Canceled);
        assertEquals(1, cxlState.size());
        assertEquals(canceledState.getId(), cxlState.get(0).getId());
    }

    @Test
    public void testListReferenceCode() {
        InvoicePayment payment1 = new InvoicePayment();
        payment1.setReferenceCode("1");
        InvoicePayment payment2 = new InvoicePayment();
        payment2.setReferenceCode("2");

        Datastore.put(payment1, payment2);

        List<Payment> list1 = paymentDao.list(payment1.getReferenceCode());
        assertEquals(1, list1.size());
        assertEquals(payment1.getId(), list1.get(0).getId());

        List<Payment> list2 = paymentDao.list(payment2.getReferenceCode());
        assertEquals(1, list2.size());
        assertEquals(payment2.getId(), list2.get(0).getId());
    }

    @Test
    public void testListBetweenWithState() throws Exception {
        InvoicePayment payment1 = new InvoicePayment();
        payment1.setCreated(new Date(12345));
        PayPalPayment payment2 = new PayPalPayment();
        payment2.setCreated(new Date(payment1.getCreated().getTime() + 1000));
        PayPalPayment payment3 = new PayPalPayment();
        payment3.setCreated(new Date(payment2.getCreated().getTime() + 1000));
        paymentDao.save(Arrays.asList(payment1, payment2, payment3));

        Date start = new Date(payment1.getCreated().getTime() + 1);
        Date end = new Date(payment3.getCreated().getTime() - 1);
        List<Payment> payments = paymentDao.list(start, end, PaymentStateEnum.New);
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
        assertTrue(paymentDao.list(start, end, PaymentStateEnum.Created).isEmpty());
    }

    @Test
    public void testGetByNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        paymentDao.get((Key) null);
    }

    @Test
    public void testGetByNullTransferIdKey() throws Exception {
        assertNull(paymentDao.getByTransferId((Key) null));
    }

    @Test
    public void testGetByTransferIdWithMultiplePayments() throws Exception {
        thrown.expect(PreparedQuery.TooManyResultsException.class);
        Transfer transfer = new Transfer();
        Datastore.put(transfer);
        for (int i = 0; i < 3; i++) {
            Payment payment = new Payment();
            payment.getTransferRef().setModel(transfer);
            Datastore.put(payment);
        }
        paymentDao.getByTransferId(transfer.getId());
    }

    @Test
    public void testGetByTransferId() throws Exception {
        Transfer transfer = new Transfer();
        Datastore.put(transfer);
        Payment payment = new Payment();
        payment.getTransferRef().setModel(transfer);
        Datastore.put(payment);
        Payment result = paymentDao.getByTransferId(transfer.getId());
        assertNotNull(result);
        assertEquals(payment.getId(), result.getId());
    }

    @Test
    public void testGetByTransferIdWithNoAssociatedPayment() throws Exception {
        Transfer transfer = new Transfer();
        Datastore.put(transfer);
        Payment payment = new Payment();
        payment.getTransferRef().setModel(transfer);
        Datastore.put(payment);
        Payment result = paymentDao.getByTransferId(Datastore.allocateId(Transfer.class));
        assertNull(result);
    }

    @Test
    public void testSave() throws Exception {
        Payment payment = new Payment();
        Key key = paymentDao.save(payment);
        assertNotNull(paymentDao.get(key));
    }
}