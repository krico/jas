package com.jasify.schedule.appengine.model.payment.workflow;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class PaymentWorkflowFactoryTest {
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
        TestHelper.assertUtilityClassWellDefined(PaymentWorkflowFactory.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalKey() throws Exception {
        PaymentWorkflowFactory.workflowFor(Datastore.allocateId(User.class), null);
    }

    @Test
    public void testUnknownKey() throws Exception {
        PaymentWorkflow workflow = PaymentWorkflowFactory.workflowFor(Datastore.allocateId(Activity.class), null);
        assertNotNull(workflow);
        assertTrue(workflow instanceof ActivityPaymentWorkflow);
    }
}