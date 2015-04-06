package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.payment.workflow.ActivityPaymentWorkflowMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.activity.TestActivityServiceFactory;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.balance.TestBalanceServiceFactory;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.*;

public class ActivityPaymentWorkflowTest {
    private Key workflowId;
    private Key subscriptionId;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testOnCanceled() throws Exception {
        ActivityPaymentWorkflow workflow = new ActivityPaymentWorkflow();
        Datastore.put(workflow);
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Canceled);
    }

    @Test
    public void testOnCreated() throws Exception {
        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
        testActivityServiceFactory.setUp();
        try {
            Payment payment = new Payment();
            User user = new User();
            payment.getUserRef().setModel(user);
            Activity activity = new Activity();
            activity.setId(Datastore.allocateId(Activity.class));

            ActivityPaymentWorkflow workflow = new ActivityPaymentWorkflow(activity.getId());
            workflow.getPaymentRef().setModel(payment);

            Subscription subscription = new Subscription();
            Datastore.put(payment, workflow, user, activity, subscription);
            workflowId = workflow.getId();
            subscriptionId = subscription.getId();
            EasyMock.expect(testActivityServiceFactory.getActivityServiceMock().subscribe(user.getId(), activity.getId()))
                    .andReturn(subscription);
            testActivityServiceFactory.replay();

            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Created);

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

        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
        testActivityServiceFactory.setUp();
        try {
            testActivityServiceFactory.getActivityServiceMock().cancel(subscriptionId);
            EasyMock.expectLastCall();
            testActivityServiceFactory.replay();
            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(workflowId, PaymentStateEnum.Canceled);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testActivityServiceFactory);
        }
    }

    @Test
    public void testOnCompleted() throws Exception {
        testOnCreated();

        TestBalanceServiceFactory testBalanceServiceFactory = new TestBalanceServiceFactory();
        testBalanceServiceFactory.setUp();
        try {
            testBalanceServiceFactory.getBalanceServiceMock().subscription(subscriptionId);
            EasyMock.expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPaymentWorkflow transition = PaymentWorkflowEngine.transition(workflowId, PaymentStateEnum.Completed);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testBalanceServiceFactory);
        }
    }
}