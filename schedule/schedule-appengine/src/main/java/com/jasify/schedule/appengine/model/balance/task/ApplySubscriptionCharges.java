package com.jasify.schedule.appengine.model.balance.task;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.*;
import com.jasify.schedule.appengine.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.Objects;

/**
 * @author krico
 * @since 23/02/15.
 */
public class ApplySubscriptionCharges implements DeferredTask {
    private static final Logger log = LoggerFactory.getLogger(ApplySubscriptionCharges.class);

    private final Key subscriptionId;

    public ApplySubscriptionCharges(Key subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @Override
    public void run() {
        /*
        TODO: This task should be responsible for:
        - determining what the charge model is for the org
        - inspecting current state
        - determine what fees should be applied
        - apply necessary fees

        But, right now, we only have the "BETA" plan...  So as a proof of concept, we charge 0 CHF on every subscription
         */
        log.info("Applying subscription fess for subscription={}", subscriptionId);
        Subscription subscription = Datastore.get(SubscriptionMeta.get(), subscriptionId);
        Transfer transfer = subscription.getTransferRef().getModel();
        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        Account payerAccount = beneficiaryTransaction.getAccountRef().getModel();
        Account beneficiaryAccount = AccountUtil.profitAndLossAccount();
        BalanceService balanceService = BalanceServiceFactory.getBalanceService();
        Transfer chargeTransfer = balanceService.createTransfer(0d, transfer.getCurrency(), "Subscription charge " + FormatUtil.toString(subscription),
                Objects.toString(subscription.getId()), payerAccount, beneficiaryAccount);
        balanceService.applyTransfer(chargeTransfer);
    }
}
