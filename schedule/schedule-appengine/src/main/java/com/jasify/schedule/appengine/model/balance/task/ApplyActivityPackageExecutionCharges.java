package com.jasify.schedule.appengine.model.balance.task;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.model.activity.ActivityPackageExecution;
import com.jasify.schedule.appengine.model.balance.*;
import com.jasify.schedule.appengine.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.Objects;

/**
 * @author krico
 * @since 12/05/15.
 */
public class ApplyActivityPackageExecutionCharges implements DeferredTask {
    private static final Logger log = LoggerFactory.getLogger(ApplySubscriptionCharges.class);

    private final Key activityPackageExecutionId;

    public ApplyActivityPackageExecutionCharges(Key activityPackageExecutionId) {
        this.activityPackageExecutionId = activityPackageExecutionId;
    }

    @Override
    public void run() {
        /*
        TODO: This task should be responsible for:
        - determining what the charge model is for the org
        - inspecting current state
        - determine what fees should be applied
        - apply necessary fees

        But, right now, we only have the "BETA" plan...  So as a proof of concept, we charge 0 CHF on every activityPackageExecution
         */
        log.info("Applying activityPackageExecution fess for activityPackageExecutionId={}", activityPackageExecutionId);
        ActivityPackageExecution activityPackageExecution = Datastore.get(ActivityPackageExecutionMeta.get(), activityPackageExecutionId);
        Transfer transfer = activityPackageExecution.getTransferRef().getModel();
        Transaction beneficiaryTransaction = transfer.getBeneficiaryLegRef().getModel();
        Account payerAccount = beneficiaryTransaction.getAccountRef().getModel();
        Account beneficiaryAccount = AccountUtil.profitAndLossAccount();
        BalanceService balanceService = BalanceServiceFactory.getBalanceService();

        String description = FormatUtil.toTransactionFeeString(activityPackageExecution);

        Transfer chargeTransfer = balanceService.createTransfer(0d, transfer.getCurrency(), description,
                Objects.toString(activityPackageExecution.getId()), payerAccount, beneficiaryAccount);

        balanceService.applyTransfer(chargeTransfer);
    }
}
