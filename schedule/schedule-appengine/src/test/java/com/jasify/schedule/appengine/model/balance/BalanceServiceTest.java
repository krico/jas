package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelMetadataUtil;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.CashPayment;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.*;

public class BalanceServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BalanceServiceTest.class);

    private final LocalTaskQueueTestConfig.TaskCountDownLatch latch = new LocalTaskQueueTestConfig.TaskCountDownLatch(1);
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            TestHelper.createDatastoreServiceTestConfig(),
            new LocalTaskQueueTestConfig()
                    .setDisableAutoTaskExecution(false)
                    .setQueueXmlPath(TestHelper.relPath("src/main/webapp/WEB-INF/queue.xml").getPath())
                    .setCallbackClass(LocalTaskQueueTestConfig.DeferredTaskCallback.class)
                    .setTaskExecutionLatch(latch)
    );
    private BalanceService balanceService;

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify(helper);
        balanceService = BalanceServiceFactory.getBalanceService();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore(helper);

    }


    @Test
    public void testPaymentCash() throws Exception {
        Payment payment = new CashPayment();
        payment.setAmount(25d);
        payment.setFee(1d);
        payment.setCurrency("CHF");
        payment.setState(PaymentStateEnum.Completed);

        User user = new User("lehman");
        payment.getUserRef().setModel(user);
        Datastore.put(payment, user);
        balanceService.payment(payment);
        assertNotNull("Transfer not linked", payment.getTransferRef().getKey());
        Transfer transfer = payment.getTransferRef().getModel();
        assertNotNull(transfer);
        assertEquals(payment.getCurrency(), transfer.getCurrency());

        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        assertNotNull(beneficiaryTransaction);
        assertEquals(transfer.getCurrency(), beneficiaryTransaction.getCurrency());
        assertEquals(24d, beneficiaryTransaction.getAmount());
        assertEquals(24d, beneficiaryTransaction.getUnpaid());
        assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
        assertEquals(24d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-24d, payerTransaction.getAmount());
        assertEquals(-24d, payerTransaction.getUnpaid());
        assertEquals(0d, payerTransaction.getAccountRef().getModel().getBalance());
        assertEquals(-24d, payerTransaction.getAccountRef().getModel().getUnpaid());

    }

    @Test
    public void testPaymentPayPal() throws Exception {
        Payment payment = new PayPalPayment();
        payment.setAmount(25d);
        payment.setFee(1d);
        payment.setCurrency("CHF");
        payment.setState(PaymentStateEnum.Completed);

        User user = new User("lehman");
        payment.getUserRef().setModel(user);
        Datastore.put(payment, user);
        balanceService.payment(payment);
        assertNotNull("Transfer not linked", payment.getTransferRef().getKey());
        Transfer transfer = payment.getTransferRef().getModel();
        assertNotNull(transfer);
        assertEquals(payment.getCurrency(), transfer.getCurrency());

        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        assertNotNull(beneficiaryTransaction);
        assertEquals(transfer.getCurrency(), beneficiaryTransaction.getCurrency());
        assertEquals(24d, beneficiaryTransaction.getAmount());
        assertEquals(0d, beneficiaryTransaction.getUnpaid());
        assertEquals(24d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
        assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-24d, payerTransaction.getAmount());
        assertEquals(-0d, payerTransaction.getUnpaid());
        assertEquals(-24d, payerTransaction.getAccountRef().getModel().getBalance());
        assertEquals(0d, payerTransaction.getAccountRef().getModel().getUnpaid());

    }

    @Test
    public void testSubscriptionWithSubscriptionId() throws Exception {
        assertSubscription(new DoSubscription() {
            @Override
            public void subscription(Subscription subscription, Account payer, Account beneficiary) throws ModelException {
                balanceService.subscription(subscription.getId());
            }
        }, false);
    }

    @Test
    public void testSubscriptionWithSubscription() throws Exception {
        assertSubscription(new DoSubscription() {
            @Override
            public void subscription(Subscription subscription, Account payer, Account beneficiary) throws ModelException {
                balanceService.subscription(subscription);
            }
        }, false);
    }

    @Test
    public void testUnpaidSubscriptionWithSubscriptionId() throws Exception {
        assertSubscription(new DoSubscription() {
            @Override
            public void subscription(Subscription subscription, Account payer, Account beneficiary) throws ModelException {
                balanceService.unpaidSubscription(subscription.getId());
            }
        }, true);
    }

    @Test
    public void testActivityPackageExecutionWithId() throws Exception {
        assertActivityPackageExecution(new DoActivityPackageExecution() {
            @Override
            public void activityPackageExecution(ActivityPackageExecution execution, Account payer, Account beneficiary) throws ModelException {
                balanceService.activityPackageExecution(execution.getId());
            }
        }, false);
    }

    @Test
    public void testActivityPackageExecutionWithModel() throws Exception {
        assertActivityPackageExecution(new DoActivityPackageExecution() {
            @Override
            public void activityPackageExecution(ActivityPackageExecution execution, Account payer, Account beneficiary) throws ModelException {
                balanceService.activityPackageExecution(execution);
            }
        }, false);
    }

    @Test
    public void testUnpaidActivityPackageExecutionWithId() throws Exception {
        assertActivityPackageExecution(new DoActivityPackageExecution() {
            @Override
            public void activityPackageExecution(ActivityPackageExecution execution, Account payer, Account beneficiary) throws ModelException {
                balanceService.unpaidActivityPackageExecution(execution.getId());
            }
        }, true);
    }

    private void assertSubscription(DoSubscription doer, boolean unpaid) throws Exception {
        Activity activity = new Activity();
        activity.setPrice(18d);
        activity.setCurrency("CHF");
        activity.setName("Da Subscription");

        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);

        Key userId = Datastore.allocateId(User.class);
        Key organizationId = Datastore.allocateId(Organization.class);

        User user = new User();
        user.setId(userId);
        Organization org = new Organization();
        org.setId(organizationId);
        subscription.getUserRef().setKey(userId);
        Account userAccount = AccountUtil.memberAccountMustExist(userId);
        ActivityType activityType = new ActivityType();
        activityType.setId(Datastore.allocateId(organizationId, ActivityType.class));
        activityType.getOrganizationRef().setKey(organizationId);
        activity.getActivityTypeRef().setModel(activityType);

        Account organizationAccount = AccountUtil.memberAccountMustExist(organizationId);

        Datastore.put(activity, activityType, subscription, user, org);


        doer.subscription(subscription, userAccount, organizationAccount);

        assertTrue(latch.await(50, TimeUnit.SECONDS));
        log.info("{}", ModelMetadataUtil.dumpDb(new StringBuilder("DB DUMP\n")));
        subscription = Datastore.get(Subscription.class, subscription.getId());
        assertNotNull("Not linked", subscription.getTransferRef().getKey());
        Transfer transfer = subscription.getTransferRef().getModel();
        assertNotNull(transfer);
        assertEquals(subscription.getActivityRef().getModel().getCurrency(), transfer.getCurrency());

        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        assertNotNull(beneficiaryTransaction);
        assertEquals(transfer.getCurrency(), beneficiaryTransaction.getCurrency());
        assertEquals(18d, beneficiaryTransaction.getAmount());
        if (unpaid) {
            assertEquals(18d, beneficiaryTransaction.getUnpaid());
            assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
            assertEquals(18d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());
        } else {
            assertEquals(0d, beneficiaryTransaction.getUnpaid());
            assertEquals(18d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
            assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());
        }

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-18d, payerTransaction.getAmount());
        if (unpaid) {
            assertEquals(-18d, payerTransaction.getUnpaid());
            assertEquals(0d, payerTransaction.getAccountRef().getModel().getBalance());
            assertEquals(-18d, payerTransaction.getAccountRef().getModel().getUnpaid());
        } else {
            assertEquals(-0d, payerTransaction.getUnpaid());
            assertEquals(-18d, payerTransaction.getAccountRef().getModel().getBalance());
            assertEquals(0d, payerTransaction.getAccountRef().getModel().getUnpaid());
        }
    }

    private void assertActivityPackageExecution(DoActivityPackageExecution doer, boolean unpaid) throws Exception {
        ActivityPackage activityPackage = new ActivityPackage();
        activityPackage.setPrice(18d);
        activityPackage.setCurrency("CHF");
        activityPackage.setName("Da Subscription");

        ActivityPackageExecution activityPackageExecution = new ActivityPackageExecution();
        activityPackageExecution.getActivityPackageRef().setModel(activityPackage);

        Key userId = Datastore.allocateId(User.class);
        Key organizationId = Datastore.allocateId(Organization.class);

        User user = new User();
        user.setId(userId);

        Organization org = new Organization();
        org.setId(organizationId);
        activityPackage.getOrganizationRef().setModel(org);

        activityPackageExecution.getUserRef().setKey(userId);

        Account userAccount = AccountUtil.memberAccountMustExist(userId);

        Account organizationAccount = AccountUtil.memberAccountMustExist(organizationId);

        Datastore.put(activityPackage, activityPackageExecution, user, org);


        doer.activityPackageExecution(activityPackageExecution, userAccount, organizationAccount);

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        log.info("{}", ModelMetadataUtil.dumpDb(new StringBuilder("DB DUMP\n")));
        activityPackageExecution = Datastore.get(ActivityPackageExecution.class, activityPackageExecution.getId());
        assertNotNull("Not linked", activityPackageExecution.getTransferRef().getKey());
        Transfer transfer = activityPackageExecution.getTransferRef().getModel();
        assertNotNull(transfer);
        assertEquals(activityPackageExecution.getActivityPackageRef().getModel().getCurrency(), transfer.getCurrency());

        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        assertNotNull(beneficiaryTransaction);
        assertEquals(transfer.getCurrency(), beneficiaryTransaction.getCurrency());
        assertEquals(18d, beneficiaryTransaction.getAmount());
        if (unpaid) {
            assertEquals(18d, beneficiaryTransaction.getUnpaid());
            assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
            assertEquals(18d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());
        } else {
            assertEquals(0d, beneficiaryTransaction.getUnpaid());
            assertEquals(18d, beneficiaryTransaction.getAccountRef().getModel().getBalance());
            assertEquals(0d, beneficiaryTransaction.getAccountRef().getModel().getUnpaid());
        }

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-18d, payerTransaction.getAmount());
        if (unpaid) {
            assertEquals(-18d, payerTransaction.getUnpaid());
            assertEquals(0d, payerTransaction.getAccountRef().getModel().getBalance());
            assertEquals(-18d, payerTransaction.getAccountRef().getModel().getUnpaid());
        } else {
            assertEquals(-0d, payerTransaction.getUnpaid());
            assertEquals(-18d, payerTransaction.getAccountRef().getModel().getBalance());
            assertEquals(0d, payerTransaction.getAccountRef().getModel().getUnpaid());
        }
    }

    @Test
    public void testGetUserAccountById() throws Exception {
        User aUser = new User("who");
        Datastore.put(aUser);
        Account expected = AccountUtil.memberAccountMustExist(aUser.getId());
        UserAccount userAccount = balanceService.getUserAccount(aUser.getId());
        assertNotNull(userAccount);
        assertEquals(expected.getId(), userAccount.getId());
    }

    @Test
    public void testGetUserAccount() throws Exception {
        User aUser = new User("who");
        Datastore.put(aUser);
        Account expected = AccountUtil.memberAccountMustExist(aUser.getId());
        UserAccount userAccount = balanceService.getUserAccount(aUser);
        assertNotNull(userAccount);
        assertEquals(expected.getId(), userAccount.getId());
    }

    @Test
    public void testGetOrganizationAccountById() throws Exception {
        Organization anOrganization = new Organization("who");
        Datastore.put(anOrganization);
        Account expected = AccountUtil.memberAccountMustExist(anOrganization.getId());
        OrganizationAccount organizationAccount = balanceService.getOrganizationAccount(anOrganization.getId());
        assertNotNull(organizationAccount);
        assertEquals(expected.getId(), organizationAccount.getId());
    }

    @Test
    public void testGetOrganizationAccount() throws Exception {
        Organization anOrganization = new Organization("who");
        Datastore.put(anOrganization);
        Account expected = AccountUtil.memberAccountMustExist(anOrganization.getId());
        OrganizationAccount organizationAccount = balanceService.getOrganizationAccount(anOrganization);
        assertNotNull(organizationAccount);
        assertEquals(expected.getId(), organizationAccount.getId());
    }

    @Test
    public void testListTransactions() throws Exception {
        Key memberId = Datastore.allocateId(User.class);
        Key otherId = Datastore.allocateId(Organization.class);
        Account memberAccount = AccountUtil.memberAccountMustExist(memberId);
        Account otherAccount = AccountUtil.memberAccountMustExist(otherId);

        List<Transaction> transactions = balanceService.listTransactions(memberAccount);
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());

        Transfer transfer = balanceService.createTransfer(5d, "CHF", "whatever", null, memberAccount, otherAccount);
        balanceService.applyTransfer(transfer);

        transactions = balanceService.listTransactions(memberAccount);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertEquals(transaction.getAccountRef().getKey(), memberAccount.getId());
        assertEquals(transfer.getId(), transaction.getTransferRef().getKey());

        transactions = balanceService.listTransactions(otherAccount);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        transaction = transactions.get(0);
        assertEquals(transaction.getAccountRef().getKey(), otherAccount.getId());
        assertEquals(transfer.getId(), transaction.getTransferRef().getKey());
    }

    @Test
    public void testListTransactionsMultiple() throws Exception {
        Key memberId = Datastore.allocateId(User.class);
        Key otherId = Datastore.allocateId(Organization.class);
        Account memberAccount = AccountUtil.memberAccountMustExist(memberId);
        Account otherAccount = AccountUtil.memberAccountMustExist(otherId);


        List<Transfer> transfers = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            Transfer transfer = balanceService.createTransfer(5d + i, "CHF", "whatever", null, memberAccount, otherAccount);
            balanceService.applyTransfer(transfer);
            Thread.sleep(10);
            transfers.add(transfer);
        }

        List<Transaction> memberTransactions = balanceService.listTransactions(memberAccount.getId());
        assertNotNull(memberTransactions);
        assertEquals(transfers.size(), memberTransactions.size());

        List<Transaction> otherTransactions = balanceService.listTransactions(otherAccount);
        assertNotNull(otherTransactions);
        assertEquals(transfers.size(), otherTransactions.size());

        for (int i = 0; i < transfers.size(); ++i) {
            Transfer transfer = transfers.get(transfers.size() - (i + 1));

            Transaction memberTransaction = memberTransactions.get(i);
            assertEquals(memberTransaction.getAccountRef().getKey(), memberAccount.getId());
            assertEquals(transfer.getId(), memberTransaction.getTransferRef().getKey());

            Transaction otherTransaction = otherTransactions.get(i);
            assertEquals(otherTransaction.getAccountRef().getKey(), otherAccount.getId());
            assertEquals(transfer.getId(), otherTransaction.getTransferRef().getKey());
        }

        List<Transaction> limited = balanceService.listTransactions(memberAccount, 0, 2);
        assertNotNull(limited);
        assertEquals(2, limited.size());
        assertEquals(memberTransactions.get(0).getId(), limited.get(0).getId());
        assertEquals(memberTransactions.get(1).getId(), limited.get(1).getId());

        List<Transaction> offset = balanceService.listTransactions(otherAccount, 2, 3);
        assertNotNull(offset);
        assertEquals(3, offset.size());
        assertEquals(otherTransactions.get(2).getId(), offset.get(0).getId());
        assertEquals(otherTransactions.get(3).getId(), offset.get(1).getId());
        assertEquals(otherTransactions.get(4).getId(), offset.get(2).getId());

        otherTransactions.get(4).setCreated(null);
        Datastore.put(otherTransactions.get(4));

        List<Transaction> timed = balanceService.listTransactions(otherAccount.getId(), 2, 3);
        assertNotNull(timed);
        assertEquals(3, timed.size());
        assertEquals(otherTransactions.get(1).getId(), timed.get(0).getId());
        assertEquals(otherTransactions.get(2).getId(), timed.get(1).getId());
        assertEquals(otherTransactions.get(3).getId(), timed.get(2).getId());
    }

    private interface DoSubscription {
        void subscription(Subscription subscription, Account payer, Account beneficiary) throws ModelException;
    }

    private interface DoActivityPackageExecution {
        void activityPackageExecution(ActivityPackageExecution activityPackageExecution, Account payer, Account beneficiary) throws ModelException;
    }
}