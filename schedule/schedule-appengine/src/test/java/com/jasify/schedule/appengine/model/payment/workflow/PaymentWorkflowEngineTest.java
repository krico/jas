package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class PaymentWorkflowEngineTest {
    private static void assertTransitionFails(PaymentStateEnum fromState, PaymentStateEnum toState) throws Exception {
        TestPaymentWorkflow workflow = new TestPaymentWorkflow();
        workflow.setState(fromState);
        Key id = Datastore.put(workflow);
        PaymentStateEnum exFromState = null;
        PaymentStateEnum exToState = null;
        try {
            PaymentWorkflowEngine.transition(id, toState);
        } catch (InvalidWorkflowTransitionException e) {
            exFromState = e.getFromState();
            exToState = e.getToState();
        }
        assertNotNull(exFromState);
        assertNotNull(exToState);
        assertEquals(fromState, exFromState);
        assertEquals(toState, exToState);
    }

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(PaymentWorkflowEngine.class);
    }

    @Test
    public void testInvalidTransitions() throws Exception {
        assertTransitionFails(PaymentStateEnum.New, PaymentStateEnum.Completed);
        assertTransitionFails(PaymentStateEnum.Completed, PaymentStateEnum.Created);
        assertTransitionFails(PaymentStateEnum.Canceled, PaymentStateEnum.Created);
        assertTransitionFails(PaymentStateEnum.Canceled, PaymentStateEnum.Completed);
    }

    @Test
    public void testTransitionToCompleted() throws Exception {
        TestPaymentWorkflow workflow = new TestPaymentWorkflow();
        Key id = Datastore.put(workflow);
        assertEquals(0, workflow.getOnCreatedCount());
        assertEquals(0, workflow.getOnCanceledCount());
        assertEquals(0, workflow.getOnCompletedCount());

        TestPaymentWorkflow transition;

        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Created);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Created, transition.getState());
        assertEquals(1, transition.getOnCreatedCount());
        assertEquals(0, transition.getOnCanceledCount());
        assertEquals(0, transition.getOnCompletedCount());

        //No change
        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Created);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Created, transition.getState());
        assertEquals(1, transition.getOnCreatedCount());
        assertEquals(0, transition.getOnCanceledCount());
        assertEquals(0, transition.getOnCompletedCount());

        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Completed);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Completed, transition.getState());
        assertEquals(1, transition.getOnCreatedCount());
        assertEquals(0, transition.getOnCanceledCount());
        assertEquals(1, transition.getOnCompletedCount());
    }

    @Test
    public void testTransitionToCanceled() throws Exception {
        TestPaymentWorkflow workflow = new TestPaymentWorkflow();
        Key id = Datastore.put(workflow);
        assertEquals(0, workflow.getOnCreatedCount());
        assertEquals(0, workflow.getOnCanceledCount());
        assertEquals(0, workflow.getOnCompletedCount());

        TestPaymentWorkflow transition;

        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Created);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Created, transition.getState());
        assertEquals(1, transition.getOnCreatedCount());
        assertEquals(0, transition.getOnCanceledCount());
        assertEquals(0, transition.getOnCompletedCount());

        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Canceled);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Canceled, transition.getState());
        assertEquals(1, transition.getOnCreatedCount());
        assertEquals(1, transition.getOnCanceledCount());
        assertEquals(0, transition.getOnCompletedCount());
    }

    @Test
    public void testTransitionToCanceledFromNew() throws Exception {
        TestPaymentWorkflow workflow = new TestPaymentWorkflow();
        Key id = Datastore.put(workflow);
        assertEquals(0, workflow.getOnCreatedCount());
        assertEquals(0, workflow.getOnCanceledCount());
        assertEquals(0, workflow.getOnCompletedCount());

        TestPaymentWorkflow transition;

        transition = PaymentWorkflowEngine.transition(id, PaymentStateEnum.Canceled);
        assertNotNull(transition);
        assertEquals(PaymentStateEnum.Canceled, transition.getState());
        assertEquals(0, transition.getOnCreatedCount());
        assertEquals(1, transition.getOnCanceledCount());
        assertEquals(0, transition.getOnCompletedCount());
    }
}