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
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.workflow.ActivityPackagePaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflowFactory;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify(helper);
        paymentService = PaymentServiceFactory.getPaymentService();
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

    private ActivityType createActivityType() {
        Organization organization = new Organization();
        Datastore.put(organization);
        ActivityType activityType = new ActivityType();
        activityType.getOrganizationRef().setModel(organization);
        Datastore.put(activityType);
        return activityType;
    }

    private Activity createActivity(ActivityType activityType) {
        Activity activity = new Activity(activityType);
        activity.setPrice(19.95);
        Datastore.put(activity);
        return activity;
    }

    private ActivityPackage createActivityPackage(ActivityType activityType) {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.getOrganizationRef().setModel(activityType.getOrganizationRef().getModel());
        activityPackage.setItemCount(2);
        activityPackage.setPrice(29.95);
        Datastore.put(activityPackage);
        return activityPackage;
    }

    private User createUser() {
        User user = new User("testuser");
        Datastore.put(user);
        return user;
    }

    private ActivityPackageActivity createActivityPackageActivity(ActivityPackage activityPackage, Activity activity) {
        ActivityPackageActivity activityPackageActivity = new ActivityPackageActivity();
        activityPackageActivity.getActivityPackageRef().setModel(activityPackage);
        activityPackageActivity.getActivityRef().setModel(activity);
        Datastore.put(activityPackageActivity);
        return activityPackageActivity;
    }

    private CashPayment createCashPayment(PaymentProvider<CashPayment> cashPaymentPaymentProvider) {
        CashPayment cashPayment = cashPaymentPaymentProvider.newPayment();
        cashPayment.setAmount(19.95);
        cashPayment.setCurrency("NZD");
        return cashPayment;
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
    public void testActivityCancelTaskCancelsPayment() throws Exception {
        PaymentProvider<CashPayment> paymentProvider = CashPaymentProvider.instance();
        CashPayment payment = createCashPayment(paymentProvider);

        User user = createUser();
        Activity activity = createActivity(createActivityType());

        PaymentWorkflow paymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
        assertEquals(1, activity.getSubscriptionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Canceled, payment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(0, activity.getSubscriptionCount());
    }

    @Test
    public void testActivityCancelTaskIgnoresCompletedPayments() throws Exception {
        PaymentProvider<CashPayment> paymentProvider = CashPaymentProvider.instance();
        CashPayment payment = createCashPayment(paymentProvider);

        User user = createUser();
        Activity activity = createActivity(createActivityType());

        PaymentWorkflow paymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
        assertEquals(1, activity.getSubscriptionCount());
        paymentService.executePayment(paymentProvider, payment);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Completed, payment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionCount());
    }

    @Test
    public void testActivityPackageCancelTaskCancelsPayment() throws Exception {
        PaymentProvider<CashPayment> paymentProvider = CashPaymentProvider.instance();
        CashPayment payment = createCashPayment(paymentProvider);

        User user = createUser();
        ActivityType activityType = createActivityType();
        ActivityPackage activityPackage = createActivityPackage(activityType);
        Activity activity1 = createActivity(activityType);
        Activity activity2 = createActivity(activityType);

        ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity1);
        ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity2);

        PaymentWorkflow paymentWorkflow = PaymentWorkflowFactory.workflowFor(activityPackage.getId(), Arrays.asList(activity1.getId(), activity2.getId()));
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        for (Activity activity : activityPackage.getActivities()) {
            assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(1, activityPackage.getExecutionCount());
        paymentService.executePayment(paymentProvider, payment);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Completed, payment.getState());
        for (Activity activity : activityPackage.getActivities()) {
            assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(1, activityPackage.getExecutionCount());
    }

    @Test
    public void testActivityPackageCancelTaskIgnoresCompletedPayments() throws Exception {
        PaymentProvider<CashPayment> paymentProvider = CashPaymentProvider.instance();
        CashPayment payment = createCashPayment(paymentProvider);

        User user = createUser();
        ActivityType activityType = createActivityType();
        ActivityPackage activityPackage = createActivityPackage(activityType);
        Activity activity1 = createActivity(activityType);
        Activity activity2 = createActivity(activityType);

        ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity1);
        ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity2);

        PaymentWorkflow paymentWorkflow = PaymentWorkflowFactory.workflowFor(activityPackage.getId(), Arrays.asList(activity1.getId(), activity2.getId()));
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        for (Activity activity : activityPackage.getActivities()) {
            assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(1, activityPackage.getExecutionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert(localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Canceled, payment.getState());
        for (Activity activity : activityPackage.getActivities()) {
            assertEquals(0, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(0, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(0, activityPackage.getExecutionCount());
    }

    @Test
    public void testGetPayment() throws Exception {
        PayPalPayment payment = newPayment();
        assertNotNull(paymentService.getPayment(payment.getId()));
    }

    @Test(expected = PaymentException.class)
    public void testCreatePaymentThrowsIfPaymentNotInNewState () throws Exception {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        payment.setState(PaymentStateEnum.Created);
        paymentService.newPayment(Datastore.allocateId(User.class), payment, Collections.<PaymentWorkflow>emptyList());
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = EasyMock.createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        EasyMock.expectLastCall();
        EasyMock.replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);
    }
}