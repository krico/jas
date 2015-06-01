package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.TestShoppingCartServiceFactory;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.model.users.User;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ShoppingCartPaymentWorkflowTest {
    public static final String SOME_CART = "someCart";

    private PayPalPayment newPayment(List<PaymentWorkflow> workflows) throws PaymentException {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        paymentService.newPayment(Datastore.allocateId(User.class), payment, workflows);
        return payment;
    }

    private User createUser() {
        User user = new User();
        user.setRealName("Real Name");
        user.setName("Name");
        user.setEmail("Em@il.com");
        Datastore.put(user);
        return user;
    }

    private Subscription createSubscription() throws Exception {
        Organization organization = new Organization("Org");
        organization.setId(Datastore.allocateId(Organization.class));
        Datastore.put(new OrganizationMember(organization, createUser()));
        ActivityType activityType = new ActivityType("AT");
        activityType.getOrganizationRef().setModel(organization);
        Activity activity = new Activity(activityType);
        Subscription subscription = new Subscription();
        subscription.setId(Datastore.allocateId(Subscription.class));
        subscription.getActivityRef().setModel(activity);
        subscription.getUserRef().setModel(createUser());
        return subscription;
    }

    private ActivityPaymentWorkflow createActivityPaymentWorkflow() {
        ActivityPaymentWorkflow activityPaymentWorkflow = new ActivityPaymentWorkflow();
        Datastore.put(activityPaymentWorkflow);
        return activityPaymentWorkflow;
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
    public void testOnCreated() throws Exception {
        //no op
        ShoppingCartPaymentWorkflow workflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(workflow);
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
        ShoppingCartPaymentWorkflow workflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(workflow);

        newPayment(Arrays.asList(new PaymentWorkflow[]{workflow}));

        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Created);
        TestShoppingCartServiceFactory testShoppingCartServiceFactory = new TestShoppingCartServiceFactory();
        try {
            testShoppingCartServiceFactory.setUp();
            EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().clearCart(SOME_CART)).andReturn(new ShoppingCart(SOME_CART));
            testShoppingCartServiceFactory.replay();
            PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Completed);
        } finally {
            testShoppingCartServiceFactory.tearDown();
        }
    }

    @Test
    public void testOnCompletedSendsEmails() throws Exception {
        // This test looks too big :(
        ActivityPaymentWorkflow activityPaymentWorkflow1 = createActivityPaymentWorkflow();
        Subscription subscription1 = createSubscription();
        activityPaymentWorkflow1.setSubscriptionId(subscription1.getId());

        ActivityPaymentWorkflow activityPaymentWorkflow2 = createActivityPaymentWorkflow();
        Subscription subscription2 = createSubscription();
        activityPaymentWorkflow2.setSubscriptionId(subscription2.getId());

        ShoppingCartPaymentWorkflow shoppingCartPaymentWorkflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(shoppingCartPaymentWorkflow);

        List<PaymentWorkflow> workflows = new ArrayList<>();
        workflows.add(activityPaymentWorkflow1);
        workflows.add(activityPaymentWorkflow2);
        workflows.add(shoppingCartPaymentWorkflow);
        newPayment(workflows);

        PaymentWorkflowEngine.transition(shoppingCartPaymentWorkflow.getId(), PaymentStateEnum.Created);
        TestShoppingCartServiceFactory testShoppingCartServiceFactory = new TestShoppingCartServiceFactory();
        TestActivityServiceFactory testActivityServiceFactory = new TestActivityServiceFactory();

        try {
            testShoppingCartServiceFactory.setUp();
            testActivityServiceFactory.setUp();

            EasyMock.expect(testActivityServiceFactory.getActivityServiceMock().getSubscription(subscription1.getId())).andReturn(subscription1);
            EasyMock.expect(testActivityServiceFactory.getActivityServiceMock().getSubscription(subscription2.getId())).andReturn(subscription2);
            EasyMock.expect(testShoppingCartServiceFactory.getShoppingCartServiceMock().clearCart(SOME_CART)).andReturn(new ShoppingCart(SOME_CART));

            testShoppingCartServiceFactory.replay();
            testActivityServiceFactory.replay();

            LocalMailService localMailService = LocalMailServiceTestConfig.getLocalMailService();
            localMailService.clearSentMessages();

            PaymentWorkflowEngine.transition(shoppingCartPaymentWorkflow.getId(), PaymentStateEnum.Completed);
            List<MailServicePb.MailMessage> sentMessages = localMailService.getSentMessages();
            assertNotNull(sentMessages);
            // One for Jasify, One for Subscriber, Two for Publisher
            assertEquals(3, sentMessages.size());
        } finally {
            testShoppingCartServiceFactory.tearDown();
            testActivityServiceFactory.tearDown();
        }
    }
}