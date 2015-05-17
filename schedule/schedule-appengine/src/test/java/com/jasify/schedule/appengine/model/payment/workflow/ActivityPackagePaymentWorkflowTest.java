package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.meta.payment.workflow.ActivityPackagePaymentWorkflowMeta;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.activity.TestActivityServiceFactory;
import com.jasify.schedule.appengine.model.balance.TestBalanceServiceFactory;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

public class ActivityPackagePaymentWorkflowTest {
    private ActivityPackagePaymentWorkflow activityPackagePaymentWorkflow;
    private ActivityPackageExecution activityPackageExecution;
    private ArrayList<Key> activityIds;
    private User user;
    private ActivityPackage activityPackage;
    private Payment payment;


    @Before
    public void setup() {
        TestHelper.initializeDatastore();
        user = new User();
        activityIds = createActivityIds();
        activityPackage = createActivityPackage();
        activityPackageExecution = new ActivityPackageExecution();
        payment = createPayment(user);
        activityPackagePaymentWorkflow = createActivityPaymentWorkflow(payment, activityPackage);
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    private ActivityPackage createActivityPackage() {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.setId(Datastore.allocateId(ActivityPackage.class));
        return activityPackage;
    }

    private ArrayList<Key> createActivityIds () {
        ArrayList<Key> activityIds = new ArrayList<>();
        activityIds.add(Datastore.allocateId(Activity.class));
        return activityIds;
    }

    private Payment createPayment(User user) {
        Payment payment = new Payment();
        payment.getUserRef().setModel(user);
        return payment;
    }

    private ActivityPackagePaymentWorkflow createActivityPaymentWorkflow(Payment payment, ActivityPackage activityPackage) {
        ActivityPackagePaymentWorkflow activityPackagePaymentWorkflow = new ActivityPackagePaymentWorkflow(activityPackage.getId(), activityIds);
        activityPackagePaymentWorkflow.getPaymentRef().setModel(payment);
        return activityPackagePaymentWorkflow;
    }

    @Test
    public void testOnCanceled() throws Exception {
        ActivityPackagePaymentWorkflow workflow = new ActivityPackagePaymentWorkflow();
        Datastore.put(workflow);
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Canceled);
    }

    @Test
    public void testOnCreated() throws Exception {
        Datastore.put(payment, activityPackagePaymentWorkflow, user, activityPackage, activityPackageExecution);

        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
        testActivityServiceFactory.setUp();
        try {
            EasyMock.expect(testActivityServiceFactory.getActivityServiceMock().subscribe(user.getId(), activityPackage.getId(), activityIds))
                    .andReturn(activityPackageExecution);
            testActivityServiceFactory.replay();

            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPackagePaymentWorkflow.getId(), PaymentStateEnum.Created);

            assertEquals(activityPackageExecution.getId(), transition.getActivityPackageExecutionId());
            assertEquals(PaymentStateEnum.Created, transition.getState());

            //Ensure it's persisted
            transition = Datastore.get(ActivityPackagePaymentWorkflowMeta.get(), transition.getId());
            assertEquals(activityPackageExecution.getId(), transition.getActivityPackageExecutionId());
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
            testActivityServiceFactory.getActivityServiceMock().cancelActivityPackageExecution(activityPackageExecution.getId());
            EasyMock.expectLastCall();
            testActivityServiceFactory.replay();
            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPackagePaymentWorkflow.getId(), PaymentStateEnum.Canceled);
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

        TestBalanceServiceFactory testBalanceServiceFactory = new TestBalanceServiceFactory();
        testBalanceServiceFactory.setUp();
        try {
            testBalanceServiceFactory.getBalanceServiceMock().unpaidActivityPackageExecution(activityPackageExecution.getId());
            EasyMock.expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPackagePaymentWorkflow.getId(), PaymentStateEnum.Completed);
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

        TestBalanceServiceFactory testBalanceServiceFactory = new TestBalanceServiceFactory();
        testBalanceServiceFactory.setUp();
        try {
            testBalanceServiceFactory.getBalanceServiceMock().activityPackageExecution(activityPackageExecution.getId());
            EasyMock.expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(activityPackagePaymentWorkflow.getId(), PaymentStateEnum.Completed);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testBalanceServiceFactory);
        }
    }
}