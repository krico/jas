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
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;

import static junit.framework.TestCase.*;

public class ActivityPackagePaymentWorkflowTest {
    private Key workflowId;
    private Key activityPackageExecutionId;

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
        ActivityPackagePaymentWorkflow workflow = new ActivityPackagePaymentWorkflow();
        Datastore.put(workflow);
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Canceled);
    }

    @Test
    public void testOnCreated() throws Exception {
        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();
        testActivityServiceFactory.setUp();
        try {
            ArrayList<Key> activityIds = new ArrayList<>();
            activityIds.add(Datastore.allocateId(Activity.class));

            Payment payment = new Payment();
            User user = new User();
            payment.getUserRef().setModel(user);
            ActivityPackage activityPackage = new ActivityPackage();
            activityPackage.setId(Datastore.allocateId(ActivityPackage.class));

            ActivityPackagePaymentWorkflow workflow = new ActivityPackagePaymentWorkflow(activityPackage.getId(), activityIds);
            workflow.getPaymentRef().setModel(payment);

            ActivityPackageExecution activityPackageExecution = new ActivityPackageExecution();
            Datastore.put(payment, workflow, user, activityPackage, activityPackageExecution);
            workflowId = workflow.getId();
            activityPackageExecutionId = activityPackageExecution.getId();

            EasyMock.expect(testActivityServiceFactory.getActivityServiceMock().subscribe(user.getId(), activityPackage.getId(), activityIds))
                    .andReturn(activityPackageExecution);
            testActivityServiceFactory.replay();

            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Created);

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
            testActivityServiceFactory.getActivityServiceMock().cancelActivityPackageExecution(activityPackageExecutionId);
            EasyMock.expectLastCall();
            testActivityServiceFactory.replay();
            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(workflowId, PaymentStateEnum.Canceled);
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
            testBalanceServiceFactory.getBalanceServiceMock().activityPackageExecution(activityPackageExecutionId);
            EasyMock.expectLastCall();
            testBalanceServiceFactory.replay();
            ActivityPackagePaymentWorkflow transition = PaymentWorkflowEngine.transition(workflowId, PaymentStateEnum.Completed);
            assertNotNull(transition);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.toString());
        } finally {
            TestHelper.tearDown(testBalanceServiceFactory);
        }
    }
}