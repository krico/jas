package com.jasify.schedule.appengine.model.balance;

import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.model.ModelMetadataUtil;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.PayPalPayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class BalanceServiceTest {
    private BalanceService balanceService;

    @Before
    public void initializeDatastore() {
        TestHelper.initializeJasify();
        balanceService = BalanceServiceFactory.getBalanceService();
    }

    @After
    public void cleanupDatastore() {
        TestHelper.cleanupDatastore();
    }


    @Test
    public void testPayment() throws Exception {
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
        assertEquals(24d, beneficiaryTransaction.getAccountRef().getModel().getBalance());

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-24d, payerTransaction.getAmount());
        assertEquals(-24d, payerTransaction.getAccountRef().getModel().getBalance());

    }

    @Test
    public void testSubscription() throws IOException {
        Activity activity = new Activity();
        activity.setPrice(18d);
        activity.setCurrency("CHF");
        activity.setName("Da Subscription");

        Subscription subscription = new Subscription();
        subscription.getActivityRef().setModel(activity);

        Account userAccount = AccountUtil.memberAccountMustExist(Datastore.allocateId(User.class));
        Account organizationAccount = AccountUtil.memberAccountMustExist(Datastore.allocateId(Organization.class));

        Datastore.put(activity, subscription);


        balanceService.subscription(subscription, userAccount, organizationAccount);
        ModelMetadataUtil.dumpDb(System.out);

        assertNotNull("Not linked", subscription.getTransferRef().getKey());
        Transfer transfer = subscription.getTransferRef().getModel();
        assertNotNull(transfer);
        assertEquals(subscription.getActivityRef().getModel().getCurrency(), transfer.getCurrency());

        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        assertNotNull(beneficiaryTransaction);
        assertEquals(transfer.getCurrency(), beneficiaryTransaction.getCurrency());
        assertEquals(18d, beneficiaryTransaction.getAmount());
        assertEquals(18d, beneficiaryTransaction.getAccountRef().getModel().getBalance());

        Transaction payerTransaction = transfer.getPayerLegRef().getModel();
        assertNotNull(payerTransaction);
        assertEquals(transfer.getCurrency(), payerTransaction.getCurrency());
        assertEquals(-18d, payerTransaction.getAmount());
        assertEquals(-18d, payerTransaction.getAccountRef().getModel().getBalance());


    }
}