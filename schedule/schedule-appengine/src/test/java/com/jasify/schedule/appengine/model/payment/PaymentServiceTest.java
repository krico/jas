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
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflowFactory;
import com.jasify.schedule.appengine.model.users.User;
import com.paypal.api.payments.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.easymock.EasyMock.*;

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

    private User createUser() {
        User user = new User("testuser");
        Datastore.put(user);
        return user;
    }

    private CashPayment createCashPayment(PaymentProvider<CashPayment> cashPaymentProvider) {
        CashPayment cashPayment = cashPaymentProvider.newPayment();
        cashPayment.setAmount(19.95);
        cashPayment.setCurrency("NZD");
        return cashPayment;
    }

    private PayPalPayment createPayPalPayment(PaymentProvider<PayPalPayment> payPalPaymentProvider) {
        PayPalPayment payPalPayment = payPalPaymentProvider.newPayment();
        payPalPayment.setAmount(19.95);
        payPalPayment.setCurrency("NZD");
        return payPalPayment;
    }

    @Test
    public void testCreatePayment() throws Exception {
        PayPalPayment payment = newPayment();
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        expectLastCall();
        replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);

        verify(mockProvider);
    }

    @Test
    public void testExecutePayment() throws Exception {
        PayPalPayment payment = newPayment();
        payment.setState(PaymentStateEnum.Created);
        Datastore.put(payment);

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = createMock(PaymentProvider.class);
        mockProvider.executePayment(payment);
        expectLastCall();
        replay(mockProvider);

        paymentService.executePayment(mockProvider, payment);

        verify(mockProvider);
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
        PaymentProvider<PayPalPayment> mockProvider = createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        expectLastCall();
        replay(mockProvider);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());
        paymentService.createPayment(mockProvider, payment, baseUrl);
        assertEquals(1, localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().size());
    }

    @Test
    public void testActivityCancelTaskCancelsCashPayment() throws Exception {
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

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Canceled, payment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(0, activity.getSubscriptionCount());
    }

    @Test
    public void testActivityCancelTaskCancelsPayPalPayment() throws Exception {
        PayPalPaymentProvider.PayPalInterface payPalInterface = createPayPalInterface();

        PaymentProvider<PayPalPayment> paymentProvider = PayPalPaymentProvider.instance();
        PayPalPayment payPalPayment = createPayPalPayment(paymentProvider);

        User user = createUser();
        Activity activity = createActivity(createActivityType());

        PaymentWorkflow paymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        paymentService.newPayment(user.getId().getId(), payPalPayment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        expect(payPalInterface.getWebProfiles()).andReturn(Collections.<WebProfile>emptyList());
        expect(payPalInterface.create(isA(WebProfile.class))).andReturn("PROFILE");
        com.paypal.api.payments.Payment payment = createPayPalPayment();
        payment.setId("ExternalId");
        expect(payPalInterface.create(isA(com.paypal.api.payments.Payment.class))).andReturn(payment);
        expect(payPalInterface.get(payment.getId())).andReturn(null);
//        expect(payPalInterface.execute(isA(Payment.class), isA(PaymentExecution.class))).andReturn(payment);
        replay(payPalInterface);
        paymentService.createPayment(paymentProvider, payPalPayment, baseUrl);
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
        assertEquals(1, activity.getSubscriptionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        Payment dbPayment = Datastore.get(Payment.class, payPalPayment.getId());
        assertEquals(PaymentStateEnum.Canceled, dbPayment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(0, activity.getSubscriptionCount());
    }

    @Test
    public void testActivityCancelTaskExecutesPayPalPayment() throws Exception {
        PayPalPaymentProvider.PayPalInterface payPalInterface = createPayPalInterface();

        PaymentProvider<PayPalPayment> paymentProvider = PayPalPaymentProvider.instance();
        PayPalPayment payPalPayment = createPayPalPayment(paymentProvider);

        User user = createUser();
        Activity activity = createActivity(createActivityType());

        PaymentWorkflow paymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        paymentService.newPayment(user.getId().getId(), payPalPayment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        expect(payPalInterface.getWebProfiles()).andReturn(Collections.<WebProfile>emptyList());
        expect(payPalInterface.create(isA(WebProfile.class))).andReturn("PROFILE");
        com.paypal.api.payments.Payment payment = createPayPalPayment();
        payment.setId("ExternalId");
        expect(payPalInterface.create(isA(com.paypal.api.payments.Payment.class))).andReturn(payment);
        expect(payPalInterface.get(payment.getId())).andReturn(payment);
        expect(payPalInterface.execute(isA(com.paypal.api.payments.Payment.class), isA(PaymentExecution.class))).andReturn(payment);
        replay(payPalInterface);
        paymentService.createPayment(paymentProvider, payPalPayment, baseUrl);
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
        assertEquals(1, activity.getSubscriptionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        Payment dbPayment = Datastore.get(Payment.class, payPalPayment.getId());
        assertEquals(PaymentStateEnum.Completed, dbPayment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionCount());
    }

    @Test
    public void testActivityCancelTaskExecuteThrowsCancels() throws Exception {
        PayPalPaymentProvider.PayPalInterface payPalInterface = createPayPalInterface();

        PaymentProvider<PayPalPayment> paymentProvider = PayPalPaymentProvider.instance();
        PayPalPayment payPalPayment = createPayPalPayment(paymentProvider);

        User user = createUser();
        Activity activity = createActivity(createActivityType());

        PaymentWorkflow paymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        paymentService.newPayment(user.getId().getId(), payPalPayment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        expect(payPalInterface.getWebProfiles()).andReturn(Collections.<WebProfile>emptyList());
        expect(payPalInterface.create(isA(WebProfile.class))).andReturn("PROFILE");
        com.paypal.api.payments.Payment payment = createPayPalPayment();
        payment.setId("ExternalId");
        expect(payPalInterface.create(isA(com.paypal.api.payments.Payment.class))).andReturn(payment);
        expect(payPalInterface.get(payment.getId())).andReturn(payment);
        expect(payPalInterface.execute(isA(com.paypal.api.payments.Payment.class), isA(PaymentExecution.class))).andThrow(new PaymentException(""));
        replay(payPalInterface);
        paymentService.createPayment(paymentProvider, payPalPayment, baseUrl);
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
        assertEquals(1, activity.getSubscriptionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        Payment dbPayment = Datastore.get(Payment.class, payPalPayment.getId());
        assertEquals(PaymentStateEnum.Canceled, dbPayment.getState());
        activity = Datastore.get(Activity.class, activity.getId());
        assertEquals(0, activity.getSubscriptionCount());
    }

    private PayPalPaymentProvider.PayPalInterface createPayPalInterface() {
        PayPalPaymentProvider.PayPalInterface payPalInterface = createMock(PayPalPaymentProvider.PayPalInterface.class);
        TestHelper.initializeOAuthProviderProperties();
        PayPalPaymentProvider payPalPaymentProvider = PayPalPaymentProvider.instance();
        payPalPaymentProvider.setPayPalInterface(payPalInterface);
        return payPalInterface;
    }

    private com.paypal.api.payments.Payment createPayPalPayment() {
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        ArrayList<Links> links = new ArrayList<>();
        links.add(new Links("approvalUrl", "approval_url"));
        links.add(new Links("selfUrl", "self"));
        links.add(new Links("executefUrl", "execute"));
        payment.setLinks(links);
        payment.setPayer(new Payer());
        payment.getPayer().setPayerInfo(new PayerInfo());
        payment.getPayer().getPayerInfo().setPayerId("PayerId");
        return payment;
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

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

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
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, false);
        activityPackage.setItemCount(2);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Activity activity1 = TestHelper.createActivity(activityType, true);
        Activity activity2 = TestHelper.createActivity(activityType, true);

        ActivityServiceFactory.getActivityService().addActivityPackage(activityPackage, Arrays.asList(activity1, activity2));

        PaymentWorkflow paymentWorkflow = PaymentWorkflowFactory.workflowFor(activityPackage.getId(), Arrays.asList(activity1.getId(), activity2.getId()));
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        ActivityDao activityDao = new ActivityDao();
        List<Activity> activities = activityDao.getByActivityPackageId(activityPackage.getId());
        for (Activity activity : activities) {
            assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(1, activityPackage.getExecutionCount());
        paymentService.executePayment(paymentProvider, payment);

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Completed, payment.getState());
        activities = activityDao.getByActivityPackageId(activityPackage.getId());
        for (Activity activity : activities) {
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
        Organization organization = TestHelper.createOrganization(true);
        ActivityPackage activityPackage = TestHelper.createActivityPackage(organization, false);
        activityPackage.setItemCount(2);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Activity activity1 = TestHelper.createActivity(activityType, true);
        Activity activity2 = TestHelper.createActivity(activityType, true);

        ActivityServiceFactory.getActivityService().addActivityPackage(activityPackage, Arrays.asList(activity1, activity2));

        PaymentWorkflow paymentWorkflow = PaymentWorkflowFactory.workflowFor(activityPackage.getId(), Arrays.asList(activity1.getId(), activity2.getId()));
        paymentService.newPayment(user.getId().getId(), payment, Arrays.asList(paymentWorkflow));
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");
        paymentService.createPayment(paymentProvider, payment, baseUrl);
        ActivityDao activityDao = new ActivityDao();
        List<Activity> activities = activityDao.getByActivityPackageId(activityPackage.getId());
        for (Activity activity : activities) {
            assertEquals(1, activity.getSubscriptionListRef().getModelList().size());
            assertEquals(1, activity.getSubscriptionCount());
        }
        activityPackage = Datastore.get(ActivityPackage.class, activityPackage.getId());
        assertEquals(1, activityPackage.getExecutionCount());

        Queue paymentQueue = QueueFactory.getQueue("payment-queue");
        LocalTaskQueue localTaskQueue = LocalTaskQueueTestConfig.getLocalTaskQueue();
        localTaskQueue.runTask("payment-queue", localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().get(0).getTaskName());

        assert (localTaskQueue.getQueueStateInfo().get(paymentQueue.getQueueName()).getTaskInfo().isEmpty());

        payment = Datastore.get(CashPayment.class, payment.getId());
        assertEquals(PaymentStateEnum.Canceled, payment.getState());
        activityDao = new ActivityDao();
        activities = activityDao.getByActivityPackageId(activityPackage.getId());
        for (Activity activity : activities) {
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
    public void testCreatePaymentThrowsIfPaymentNotInNewState() throws Exception {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        payment.setState(PaymentStateEnum.Created);
        paymentService.newPayment(Datastore.allocateId(User.class), payment, Collections.<PaymentWorkflow>emptyList());
        GenericUrl baseUrl = new GenericUrl("http://localhost:8080");

        @SuppressWarnings("unchecked")
        PaymentProvider<PayPalPayment> mockProvider = createMock(PaymentProvider.class);
        mockProvider.createPayment(payment, baseUrl);
        expectLastCall();
        replay(mockProvider);

        paymentService.createPayment(mockProvider, payment, baseUrl);
    }
}