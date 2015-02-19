package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.payment.PaymentMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class PaymentServiceTest {
    private PaymentService paymentService;
    private User user = new User("testuser");
    private Activity activity = new Activity();
    private Subscription subscription = new Subscription();

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        paymentService = PaymentServiceFactory.getPaymentService();
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(user);
        Datastore.put(user, activity, subscription);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testNewPayment() throws Exception {
        newPayment();
    }

    private PayPalPayment newPayment() throws PaymentException {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        Key key = paymentService.newPayment(Datastore.allocateId(User.class), payment);
        assertNotNull(key);
        return payment;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatePayment() throws Exception {
        PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);


        paymentService.createPayment(mockProvider, payment, baseUrl);

        EasyMock.verify(mockProvider);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExecutePayment() throws Exception {
        PayPalPayment payment = newPayment();

        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.executePayment(payment);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);


        paymentService.executePayment(mockProvider, payment);

        EasyMock.verify(mockProvider);
    }

    @Test
    public void testAddPayment() throws Exception {
        Payment payment = new PayPalPayment();
        Key key = paymentService.addPayment(subscription, payment);
        assertNotNull(key);
        Payment fetched = Datastore.get(PaymentMeta.get(), key);
        assertNotNull(fetched);
        assertTrue(fetched instanceof PayPalPayment);
    }

    @Test
    public void testGetPayment() throws Exception {
        Key key = paymentService.addPayment(subscription, new PayPalPayment());
        Payment payment = paymentService.getPayment(key);
        assertNotNull(payment);
        assertTrue(payment instanceof PayPalPayment);
    }

    @Test
    public void testUpdatePayment() throws Exception {
        PayPalPayment pp = new PayPalPayment();
        Key key = paymentService.addPayment(subscription, pp);

        pp.setAmount(71.20);
        pp.setCurrency("CHF");
        pp.setExternalId("PAY-XXX");
        pp.setApproveUrl(TypeUtil.toLink("http://localhost/approve"));
        pp.setExecuteUrl(TypeUtil.toLink("http://localhost/execute"));
        pp.setSelfUrl(TypeUtil.toLink("http://localhost/self"));

        Payment updated = paymentService.updatePayment(pp);
        assertNotNull(updated);
        assertTrue(updated instanceof PayPalPayment);
        PayPalPayment gotten = (PayPalPayment) paymentService.getPayment(key);
        assertEquals(71.20, gotten.getAmount());
        assertEquals("CHF", gotten.getCurrency());
        assertEquals("PAY-XXX", gotten.getExternalId());
        assertEquals(TypeUtil.toLink("http://localhost/approve"), gotten.getApproveUrl());
        assertEquals(TypeUtil.toLink("http://localhost/execute"), gotten.getExecuteUrl());
        assertEquals(TypeUtil.toLink("http://localhost/self"), gotten.getSelfUrl());
    }

    @Test
    public void testRemovePayment() throws Exception {
        Key key = paymentService.addPayment(subscription, new PayPalPayment());
        paymentService.removePayment(key);
        assertNull(Datastore.getOrNull(key));
        Subscription gotten = Datastore.get(SubscriptionMeta.get(), subscription.getId());
        assertNotNull(gotten);
        assertNull(gotten.getPaymentRef().getKey());
    }
}