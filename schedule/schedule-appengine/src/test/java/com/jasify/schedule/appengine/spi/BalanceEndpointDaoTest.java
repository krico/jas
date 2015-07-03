package com.jasify.schedule.appengine.spi;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.AccountUtil;
import com.jasify.schedule.appengine.model.balance.Transaction;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.CashPaymentProvider;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasCheckoutPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentResponse;
import com.jasify.schedule.appengine.spi.dm.JasTransactionList;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import static com.jasify.schedule.appengine.spi.JasifyEndpointTest.newAdminCaller;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author wszarmach
 * @since 29/06/15.
 */
public class BalanceEndpointDaoTest {
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

    @Before
    public void before() {
        TestHelper.initializeJasify(helper);
    }

    @After
    public void after() {
        TestHelper.cleanupDatastore();
        UserContext.clearContext();
    }

    @Test
    public void testDatastoreContention() throws Exception {
        ShoppingCartEndpoint shoppingCartEndpoint = new ShoppingCartEndpoint();
        User user = TestHelper.createUser(true);
        JasifyEndpointUser jasifyEndpointUser = newAdminCaller(user.getId().getId());
        Organization organization = TestHelper.createOrganization(true);
        ActivityType activityType = TestHelper.createActivityType(organization, true);
        Activity activity = TestHelper.createActivity(activityType, false);
        activity.setDescription(null);
        ShoppingCart shoppingCart = shoppingCartEndpoint.addUserActivity(jasifyEndpointUser, Datastore.put(activity));
        for (int i = 0; i < 15; i++) {
            activity = TestHelper.createActivity(activityType, false);
            activity.setDescription(null);
            assertEquals(shoppingCart.getId(), shoppingCartEndpoint.addUserActivity(jasifyEndpointUser, Datastore.put(activity)).getId());
        }

        JasCheckoutPaymentRequest jasCheckoutPaymentRequest = new JasCheckoutPaymentRequest();
        jasCheckoutPaymentRequest.setCartId(shoppingCart.getId());
        jasCheckoutPaymentRequest.setType(PaymentTypeEnum.Cash);
        jasCheckoutPaymentRequest.setBaseUrl("http://SomeUrl");
        BalanceEndpoint balanceEndpoint = new BalanceEndpoint();
        JasPaymentResponse jasPaymentResponse = balanceEndpoint.createCheckoutPayment(jasifyEndpointUser, jasCheckoutPaymentRequest);
        String urlToStrip = "http://SomeUrl#" + CashPaymentProvider.ACCEPT_PATH;
        Key paymentId = KeyUtil.stringToKey(jasPaymentResponse.getApproveUrl().replace(urlToStrip, ""));
        balanceEndpoint.executePayment(jasifyEndpointUser, paymentId, null);
    }

    @Test
    public void testStringPropertyTooLong() throws Exception {
        // Tests that the transaction has its description cut back to less than 500 characters
        ShoppingCartEndpoint shoppingCartEndpoint = new ShoppingCartEndpoint();
        User user = TestHelper.createUser(true);
        JasifyEndpointUser jasifyEndpointUser = newAdminCaller(user.getId().getId());

        ShoppingCart shoppingCart = shoppingCartEndpoint.addUserActivity(jasifyEndpointUser, TestHelper.createActivity(true).getId());
        for (int i = 0; i < 15; i++) {
            assertEquals(shoppingCart.getId(), shoppingCartEndpoint.addUserActivity(jasifyEndpointUser, TestHelper.createActivity(true).getId()).getId());
        }

        JasCheckoutPaymentRequest jasCheckoutPaymentRequest = new JasCheckoutPaymentRequest();
        jasCheckoutPaymentRequest.setCartId(shoppingCart.getId());
        jasCheckoutPaymentRequest.setType(PaymentTypeEnum.Cash);
        jasCheckoutPaymentRequest.setBaseUrl("http://SomeUrl");
        BalanceEndpoint balanceEndpoint = new BalanceEndpoint();
        JasPaymentResponse jasPaymentResponse = balanceEndpoint.createCheckoutPayment(jasifyEndpointUser, jasCheckoutPaymentRequest);
        String urlToStrip = "http://SomeUrl#" + CashPaymentProvider.ACCEPT_PATH;
        Key paymentId = KeyUtil.stringToKey(jasPaymentResponse.getApproveUrl().replace(urlToStrip, ""));
        balanceEndpoint.executePayment(jasifyEndpointUser, paymentId, null);
        Account account = AccountUtil.memberAccountMustExist(user.getId());
        JasTransactionList jasTransactionList = balanceEndpoint.getTransactions(jasifyEndpointUser, account.getId(), null, null);
        // Find the credit transaction
        boolean creditFound = false;
        for (Transaction transaction : jasTransactionList.getTransactions()) {
            if (!transaction.isDebit()) {
                assertTrue(transaction.getDescription().length() < 500);
                assertTrue(transaction.getDescription().contains("..."));
                creditFound = true;
            }
        }

        assertTrue(creditFound);
    }
}
