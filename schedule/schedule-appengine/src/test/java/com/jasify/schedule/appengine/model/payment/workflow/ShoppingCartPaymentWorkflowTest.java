package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.TestShoppingCartServiceFactory;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

public class ShoppingCartPaymentWorkflowTest {
    public static final String SOME_CART = "someCart";
    private Key workflowId;

    @Before
    public void setup() {
        TestHelper.initializeDatastore();
    }

    @After
    public void cleanup() {
        TestHelper.cleanupDatastore();
    }

    @Test
    public void testOnCreated() throws Exception {
        //no op
        ShoppingCartPaymentWorkflow workflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(workflow);
        workflowId = workflow.getId();
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Created);
    }

    @Test
    public void testOnCanceled() throws Exception {
        //no op
        ShoppingCartPaymentWorkflow workflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(workflow);
        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Canceled);
    }

    @Test
    public void testOnCompleted() throws Exception {
        testOnCreated();
        TestShoppingCartServiceFactory testShoppingCartServiceFactory = new TestShoppingCartServiceFactory();
        try {
            testShoppingCartServiceFactory.setUp();

            EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().clearCart(SOME_CART))
                    .andReturn(new ShoppingCart(SOME_CART));

            testShoppingCartServiceFactory.replay();

            PaymentWorkflowEngine.transition(workflowId, PaymentStateEnum.Completed);
        } finally {
            testShoppingCartServiceFactory.tearDown();
        }
    }
}