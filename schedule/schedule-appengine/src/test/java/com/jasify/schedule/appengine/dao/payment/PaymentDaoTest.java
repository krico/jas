package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
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

        List<Payment> payments = paymentDao.listSince(new Date(payment1.getCreated().getTime() + 1));
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
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

        List<Payment> payments = paymentDao.listBetween(new Date(payment1.getCreated().getTime() + 1), new Date(payment3.getCreated().getTime() - 1));
        assertEquals(1, payments.size());
        assertEquals(payment2.getId(), payments.get(0).getId());
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