package com.jasify.schedule.appengine.spi.transform;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
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
    }

    @Test
    public void testTransformToInvoice() throws Exception {
        InvoicePayment internal = new InvoicePayment();
        Datastore.put(internal);
        JasPayment external = transformer.transformTo(internal);

        assertEquals(KeyUtil.keyToString(internal.getId()), external.getId());
        assertEquals(internal.getCreated(), external.getCreated());
        assertEquals(internal.getType(), external.getType());
    }

    @Test
    public void testTransformFrom() throws Exception {

    }
}