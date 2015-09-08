package com.jasify.schedule.appengine.dao.payment;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.payment.Payment;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

/**
 * @author szarmawa
 * @since 31/08/15.
 */
public class PaymentDaoTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private PaymentDao dao;

    @BeforeClass
    public static void beforeClass() {
        TestHelper.setSystemProperties();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
    }

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        dao = new PaymentDao();
    }

    @Test
    public void testGetByNullKey() throws Exception {
        thrown.expect(NullPointerException.class);
        dao.get((Key) null);
    }

    @Test
    public void testGetByNullTransferIdKey() throws Exception {
        assertNull(dao.getByTransferId((Key) null));
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
        dao.getByTransferId(transfer.getId());
    }

    @Test
    public void testGetByTransferId() throws Exception {
        Transfer transfer = new Transfer();
        Datastore.put(transfer);
        Payment payment = new Payment();
        payment.getTransferRef().setModel(transfer);
        Datastore.put(payment);
        Payment result = dao.getByTransferId(transfer.getId());
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
        Payment result = dao.getByTransferId(Datastore.allocateId(Transfer.class));
        assertNull(result);
    }

    @Test
    public void testSave() throws Exception {
        Payment payment = new Payment();
        Key key = dao.save(payment);
        assertNotNull(dao.get(key));
    }
}