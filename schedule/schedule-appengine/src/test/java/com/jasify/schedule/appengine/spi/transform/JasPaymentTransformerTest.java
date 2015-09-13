package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasPayment;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class JasPaymentTransformerTest {

    private JasPaymentTransformer transformer = new JasPaymentTransformer();

    @BeforeClass
    public static void datastore() {
        TestHelper.initializeDatastore();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testTransformTo() throws Exception {
        Payment internal = new Payment();
        internal.setCurrency("CHF");
        internal.setAmount(66.22d);
        internal.setState(PaymentStateEnum.Created);
        internal.setFee(1.22d);
        internal.setRealFee(1.21d);
        internal.getUserRef().setKey(Datastore.allocateId(User.class));
        internal.getTransferRef().setKey(Datastore.allocateId(Transfer.class));
        Datastore.put(internal);
        JasPayment external = transformer.transformTo(internal);

        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(KeyUtil.keyToString(internal.getUserRef().getKey()), external.getUserId());
        assertEquals(KeyUtil.keyToString(internal.getTransferRef().getKey()), external.getTransferId());
        assertEquals(internal.getCurrency(), external.getCurrency());
        assertEquals(internal.getAmount(), external.getAmount());
        assertEquals(internal.getState(), external.getState());
        assertEquals(internal.getFee(), external.getFee());
        assertEquals(internal.getRealFee(), external.getRealFee());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getType(), external.getType());
        assertNull(external.getReferenceCode());
    }

    @Test
    public void testTransformInvoicePaymentTo() throws Exception {
        InvoicePayment internal = new InvoicePayment();
        internal.setReferenceCode("RefC0de");
        internal.setExpireDays(5);
        Datastore.put(internal);
        JasPayment external = transformer.transformTo(internal);

        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getType(), external.getType());
        assertEquals(internal.getReferenceCode(), external.getReferenceCode());
        assertEquals((Integer) internal.getExpireDays(), external.getExpireDays());
    }

    @Test
    public void testTransformPayPalPaymentTo() throws Exception {
        PayPalPayment internal = new PayPalPayment();
        internal.setExternalId("EID");
        internal.setExternalState("ES");
        internal.setPayerId("PID");
        internal.setPayerEmail("PE");
        internal.setPayerFirstName("PFN");
        internal.setPayerLastName("PLN");
        Datastore.put(internal);
        JasPayment external = transformer.transformTo(internal);

        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getType(), external.getType());
        assertEquals(internal.getExternalId(), external.getExternalId());
        assertEquals(internal.getExternalState(), external.getExternalState());
        assertEquals(internal.getPayerId(), external.getPayerId());
        assertEquals(internal.getPayerEmail(), external.getPayerEmail());
        assertEquals(internal.getPayerFirstName(), external.getPayerFirstName());
        assertEquals(internal.getPayerLastName(), external.getPayerLastName());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTransformFrom() throws Exception {
        transformer.transformFrom(new JasPayment());
    }
}