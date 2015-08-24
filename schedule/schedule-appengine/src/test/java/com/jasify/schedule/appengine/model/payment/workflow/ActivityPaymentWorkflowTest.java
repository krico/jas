package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.payment.workflow.ActivityPaymentWorkflowMeta;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.balance.TestBalanceServiceFactory;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

public class ActivityPaymentWorkflowTest {
    private TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
    private TestBalanceServiceFactory testBalanceServiceFactory = new TestBalanceServiceFactory();
    private User user;
    private Payment payment;
    private Activity activity;
    private ActivityPaymentWorkflow activityPaymentWorkflow;
    private Subscription subscription;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        user = new User();
        payment = createPayment(user);
        activity = createActivity();
        activityPaymentWorkflow = createActivityPaymentWorkflow(activity, payment);
        subscription = new Subscription();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    private Payment createPayment(User user) {
        Payment payment = new Payment();
        payment.getUserRef().setModel(user);
        return payment;
    }

    private Activity createActivity() {
        Organization organization = new Organization();
        Datastore.put(organization);
        ActivityType activityType = new ActivityType();
        activityType.getOrganizationRef().setModel(organization);
        Datastore.put(activityType);
        Activity activity = new Activity();
        activity.setId(Datastore.allocateId(activityType.getOrganizationRef().getKey(), ActivityMeta.get()));
        return activity;
    }

    private ActivityPaymentWorkflow createActivityPaymentWorkflow(Activity activity, Payment payment) {
        ActivityPaymentWorkflow activityPaymentWorkflow = new ActivityPaymentWorkflow(activity.getId());
        activityPaymentWorkflow.getPaymentRef().setModel(payment);
        return activityPaymentWorkflow;
    }

    @Test
    public void testOnCanceled() throws Exception {
        ActivityPaymentWorkflow workflow = new ActivityPaymentWorkflow();
        Datastore.put(workflow);
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Canceled);
    }

    @Test
    public void testOnCreated() throws Exception {
        testActivityServiceFactory.setUp();
        try {
            Datastore.put(payment, activityPaymentWorkflow, user, activity, subscription);
            ActivityService activityService = testActivityServiceFactory.getActivityServiceMock();
            // TODO: This is wrong
           // expect(activityService.subscribe(user, activity)).andReturn(subscription);
            expect(activityService.subscribe(anyObject(User.class), anyObject(Key.class))).andReturn(subscription);
            testActivityServiceFactory.replay();

            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPaymentWorkflow.getId(), PaymentStateEnum.Created);

            assertEquals(subscription.getId(), transition.getSubscriptionId());
            assertEquals(PaymentStateEnum.Created, transition.getState());

            //Ensure it's persisted
            transition = Datastore.get(ActivityPaymentWorkflowMeta.get(), transition.getId());
            assertEquals(subscription.getId(), transition.getSubscriptionId());
            assertEquals(PaymentStateEnum.Created, transition.getState());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testActivityServiceFactory);
        }
    }

    @Test
    public void testOnCanceledAfterCreated() throws Exception {
        testOnCreated();

        testActivityServiceFactory.setUp();
        try {
            ActivityService activityService = testActivityServiceFactory.getActivityServiceMock();
            activityService.cancelSubscription(subscription.getId());
            expectLastCall();
            testActivityServiceFactory.replay();
            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPaymentWorkflow.getId(), PaymentStateEnum.Canceled);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testActivityServiceFactory);
        }
    }

    @Test
    public void testOnCompletedForCashPayment() throws Exception {
        payment.setType(PaymentTypeEnum.Cash);
        testOnCreated();

        testBalanceServiceFactory.setUp();
        try {
            testBalanceServiceFactory.getBalanceServiceMock().unpaidSubscription(subscription.getId());
            expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPaymentWorkflow.getId(), PaymentStateEnum.Completed);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testBalanceServiceFactory);
        }
    }

    @Test
    public void testOnCompletedForPayPalPayment() throws Exception {
        payment.setType(PaymentTypeEnum.PayPal);
        testOnCreated();

        testBalanceServiceFactory.setUp();
        try {
            testBalanceServiceFactory.getBalanceServiceMock().subscription(subscription.getId());
            expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPaymentWorkflow.getId(), PaymentStateEnum.Completed);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testBalanceServiceFactory);
        }
    }
}