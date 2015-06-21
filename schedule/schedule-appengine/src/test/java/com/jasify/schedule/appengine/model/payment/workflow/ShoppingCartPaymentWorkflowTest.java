package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.mail.MailServicePb;
import com.google.appengine.api.mail.dev.LocalMailService;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.dao.cart.ShoppingCartDao;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.model.users.User;
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

    private PayPalPayment newPayment(List<PaymentWorkflow> paymentWorkflow) throws PaymentException {
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency("USD");
        payment.setAmount(30.35);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        paymentService.newPayment(Datastore.allocateId(User.class), payment, paymentWorkflow);
        return payment;
    }

    private ActivityPaymentWorkflow createActivityPaymentWorkflow(Subscription subscription) {
        ActivityPaymentWorkflow activityPaymentWorkflow = new ActivityPaymentWorkflow();
        activityPaymentWorkflow.setSubscriptionId(subscription.getId());
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

        ShoppingCartDao shoppingCartDao = new ShoppingCartDao();
        ShoppingCart cart = new ShoppingCart(SOME_CART);
        cart.setCurrency("BRL");
        shoppingCartDao.put(cart);


        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Created);

        assertEquals("BRL", shoppingCartDao.get(SOME_CART).getCurrency());

        PaymentWorkflowEngine.transition(workflow.getId(), PaymentStateEnum.Completed);

        assertEquals("CHF", shoppingCartDao.get(SOME_CART).getCurrency());
    }

    @Test
    public void testOnCompletedSendsEmails() throws Exception {
        // This test looks too big :(

        List<PaymentWorkflow> paymentWorkflow = new ArrayList<>();

        User subscriber = TestHelper.createUser(true);
        Organization organization = TestHelper.createOrganization(true);
        OrganizationMember organizationMember = new OrganizationMember(organization, TestHelper.createUser(true));
        Datastore.put(organizationMember);

        for (int i = 0; i < 2; i++) {
            ActivityType activityType = TestHelper.createActivityType(organization, true);
            Activity activity = TestHelper.createActivity(activityType, true);
            Subscription subscription = TestHelper.createSubscription(subscriber, activity, true);
            ActivityPaymentWorkflow activityPaymentWorkflow = createActivityPaymentWorkflow(subscription);
            paymentWorkflow.add(activityPaymentWorkflow);
        }

        ShoppingCartPaymentWorkflow shoppingCartPaymentWorkflow = new ShoppingCartPaymentWorkflow(SOME_CART);
        Datastore.put(shoppingCartPaymentWorkflow);

        paymentWorkflow.add(shoppingCartPaymentWorkflow);
        newPayment(paymentWorkflow);

        PaymentWorkflowEngine.transition(shoppingCartPaymentWorkflow.getId(), PaymentStateEnum.Created);

        LocalMailService localMailService = LocalMailServiceTestConfig.getLocalMailService();
        localMailService.clearSentMessages();

        PaymentWorkflowEngine.transition(shoppingCartPaymentWorkflow.getId(), PaymentStateEnum.Completed);
        List<MailServicePb.MailMessage> sentMessages = localMailService.getSentMessages();
        assertNotNull(sentMessages);
        // One for Subscriber, One for Publisher
        assertEquals(2, sentMessages.size());
    }
}