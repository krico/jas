package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.balance.*;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentResponse;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

public class BalanceEndpointTest {

    private TestBalanceServiceFactory testBalanceServiceFactory = new TestBalanceServiceFactory();
    private TestPaymentServiceFactory testPaymentServiceFactory = new TestPaymentServiceFactory();
    private TestPayPalInterface testPayPalInterface = new TestPayPalInterface();
    private BalanceEndpoint endpoint = new BalanceEndpoint();

    @Before
    public void before() {
        TestHelper.initializeDatastore();
        testBalanceServiceFactory.setUp();
        testPaymentServiceFactory.setUp();
        testPayPalInterface.setUp();
        testPayPalInterface.replay();
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
        TestHelper.tearDown(testBalanceServiceFactory, testPaymentServiceFactory, testPayPalInterface);
    }

    @Test
    public void testCreatePayment() throws Exception {
        final JasifyEndpointUser user = new JasifyEndpointUser("foo", 25, false);

        final JasPaymentRequest request = new JasPaymentRequest();
        request.setAmount(25d);
        request.setCurrency("CHF");
        request.setBaseUrl("http://localhost:8080/");
        request.setType(PaymentTypeEnum.PayPal);

        testBalanceServiceFactory.replay();

        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        final Capture<Long> userIdCapture = EasyMock.newCapture();
        final Capture<PayPalPayment> paymentCapture = EasyMock.newCapture();
        EasyMock.expect(paymentService.newPayment(EasyMock.captureLong(userIdCapture), EasyMock.capture(paymentCapture)))
                .andAnswer(new IAnswer<Key>() {
                    @Override
                    public Key answer() throws Throwable {
                        assertEquals((Long) user.getUserId(), userIdCapture.getValue());
                        PayPalPayment payment = paymentCapture.getValue();
                        assertEquals(request.getAmount(), payment.getAmount());
                        assertEquals(request.getCurrency(), payment.getCurrency());
                        assertEquals(1, payment.getItemCount());
                        payment.validate();
                        return Datastore.allocateId(PayPalPayment.class);
                    }
                });
        final Capture<PaymentProvider> providerCapture = EasyMock.newCapture();
        final Capture<PayPalPayment> createPaymentCapture = EasyMock.newCapture();
        final Capture<GenericUrl> urlCapture = EasyMock.newCapture();
        paymentService.createPayment(EasyMock.capture(providerCapture), EasyMock.capture(createPaymentCapture), EasyMock.capture(urlCapture));
        EasyMock.expectLastCall()
                .andAnswer(new IAnswer<Void>() {
                    @Override
                    public Void answer() throws Throwable {
                        assertEquals(PayPalPaymentProvider.instance(), providerCapture.getValue());
                        PayPalPayment payment = createPaymentCapture.getValue();
                        assertTrue(payment == paymentCapture.getValue());
                        assertEquals(request.getBaseUrl(), urlCapture.getValue().build());
                        payment.validate();
                        return null;
                    }
                });
        testPaymentServiceFactory.replay();


        JasPaymentResponse response = endpoint.createPayment(user, request);
        assertNotNull(response);
    }

    @Test
    public void testCancelPayment() throws Exception {
        final JasifyEndpointUser user = new JasifyEndpointUser("foo", 25, false);
        Key paymentId = Datastore.allocateId(Payment.class);
        Payment p = new Payment();
        p.setId(paymentId);
        p.getUserRef().setKey(Datastore.createKey(User.class, user.getUserId()));
        EasyMock.expect(PaymentServiceFactory.getPaymentService().getPayment(paymentId)).andReturn(p);
        EasyMock.expect(PaymentServiceFactory.getPaymentService().cancelPayment(p)).andReturn(p);

        testPaymentServiceFactory.replay();
        testBalanceServiceFactory.replay();
        endpoint.cancelPayment(user, paymentId);
    }

    @Test
    public void testGetAccount() throws Exception {
        final JasifyEndpointUser user = new JasifyEndpointUser("foo", 25, false);
        UserAccount expected = new UserAccount();
        EasyMock.expect(BalanceServiceFactory.getBalanceService().getUserAccount(user.getUserId())).andReturn(expected);
        testPaymentServiceFactory.replay();
        testBalanceServiceFactory.replay();
        Account account = endpoint.getAccount(user);
        assertTrue(expected == account);
    }

    @Test
    public void testListTransactions() throws Exception {
        final JasifyEndpointUser user = new JasifyEndpointUser("foo", 25, false);
        Key accountId = AccountUtil.memberIdToAccountId(Datastore.createKey(User.class, user.getUserId()));
        ArrayList<Transaction> expected = new ArrayList<>();
        int limit = 20;
        int offset = 10;
        EasyMock.expect(BalanceServiceFactory.getBalanceService().listTransactions(accountId, offset, limit)).andReturn(expected);
        testPaymentServiceFactory.replay();
        testBalanceServiceFactory.replay();
        List<Transaction> transactions = endpoint.listTransactions(user, accountId, limit, offset);
        assertTrue(expected == transactions);
    }
}