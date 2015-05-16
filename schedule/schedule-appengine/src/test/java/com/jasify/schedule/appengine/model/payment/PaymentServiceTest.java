package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class PaymentServiceTest {

    private final LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch(1);
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig()
                    .setNoIndexAutoGen(true)
                    .setApplyAllHighRepJobPolicy(),
            new LocalTaskQueueTestConfig()
                    .setDisableAutoTaskExecution(false)
                    .setQueueXmlPath(TestHelper.relPath("src/main/webapp/WEB-INF/queue.xml").getPath())
                    .setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)
                    .setTaskExecutionLatch(latch)

    );

    private PaymentService paymentService;
    private User user = new User("testuser");
    private Activity activity = new Activity();
    private Subscription subscription = new Subscription();

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify(helper);
        paymentService = PaymentServiceFactory.getPaymentService();
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(user);
        Datastore.put(user, activity, subscription);
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore(helper);
    }

    @Test
    public void testNewPayment() throws Exception {
        newPayment();
    }

    private PayPalPayment newPayment() throws PaymentException {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        Key key = paymentService.newPayment(Datastore.allocateId(User.class), payment, Collections.<PaymentWorkflow>emptyList());
        assertNotNull(key);
        return payment;
    }

    @Test
    public void testCreatePayment() throws Exception {
        PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);

        EasyMock.verify(mockProvider);
    }

    @Test
    public void testExecutePayment() throws Exception {
        PayPalPayment payment = newPayment();
        payment.setState(PaymentStateEnum.Created);
        Datastore.put(payment);

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.executePayment(payment);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);

        paymentService.executePayment(mockProvider, payment);

        EasyMock.verify(mockProvider);
    }

    @Test
    public void testCancelPayment() throws Exception {
        PayPalPayment payment = newPayment();
        payment.setState(PaymentStateEnum.Created);
        Datastore.put(payment);
        paymentService.cancelPayment(payment);
        assertEquals(PaymentStateEnum.Canceled, payment.getState());
        assertEquals(PaymentStateEnum.Canceled, paymentService.getPayment(payment.getId()).getState());
    }

    @Test
    public void testCancelTaskIsQueued() throws Exception {
        PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();

        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());
        paymentService.createPayment(mockProvider, payment, baseUrl);
        assertEquals(1, localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().size());
    }

    @Test
    public void testCancelTaskCancelsPayment() throws Exception {
        PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assertEquals(PaymentStateEnum.Canceled, paymentService.getPayment(payment.getId()).getState());
        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());
    }

    @Test
    public void testCancelTaskIgnoresCompletedPayments() throws Exception {
        final PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        mockProvider.executePayment(payment);
        EasyMock.expectLastCall().andAnswer(new IAnswer() {
            @Override
            public Object answer() throws Throwable {
                payment.setState(PaymentStateEnum.Completed);
                return null;
            }
        });;
        EasyMock.replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);
        payment.setState(PaymentStateEnum.Created);
        Datastore.put(payment);
        paymentService.executePayment(mockProvider, payment);

        EasyMock.verify(mockProvider);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        // TODO: This test is incomplete because it doesnt check that PaymentServiceFactory.getPaymentService().cancelPayment(payment) is not called!
        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());
    }

    @Test
    public void testGetPayment() throws Exception {
        PayPalPayment payment = newPayment();
        assertNotNull(paymentService.getPayment(payment.getId()));
    }
}