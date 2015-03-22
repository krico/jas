package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertNotNull;

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
        payment.setState(PaymentStateEnum.Created);
        Datastore.put(payment);

        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.executePayment(payment);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);


        paymentService.executePayment(mockProvider, payment);

        EasyMock.verify(mockProvider);
    }


    @Test
    public void testGetPayment() throws Exception {
        PayPalPayment payment = newPayment();
        assertNotNull(paymentService.getPayment(payment.getId()));
    }
}