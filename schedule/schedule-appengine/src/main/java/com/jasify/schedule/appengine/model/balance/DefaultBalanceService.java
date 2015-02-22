package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.balance.*;
import com.jasify.schedule.appengine.model.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 20/02/15.
 */
public class DefaultBalanceService implements BalanceService {
    private static final Logger log = LoggerFactory.getLogger(DefaultBalanceService.class);

    private final AccountMeta accountMeta;
    private final UserAccountMeta userAccountMeta;
    private final OrganizationAccountMeta organizationAccountMeta;
    private final TransferMeta transferMeta;
    private final TransactionMeta transactionMeta;
    private final Key custodialAccountKey;

    private DefaultBalanceService() {
        accountMeta = AccountMeta.get();
        userAccountMeta = UserAccountMeta.get();
        organizationAccountMeta = OrganizationAccountMeta.get();
        transferMeta = TransferMeta.get();
        transactionMeta = TransactionMeta.get();

        custodialAccountKey = Datastore.createKey(accountMeta, AccountUtil.CUSTODIAL_ACCOUNT);
    }

    static BalanceService instance() {
        return Singleton.INSTANCE;
    }

    /**
     * And attempt to make an idempotent payment procedure composed of several transactions that can fail at any point
     * and is capable of detecting it and restoring itself.
     *
     * @param payment a payment that was executed and is to be persisted so that it is credited to the user
     */
    @Override
    public void payment(Payment payment) {
        Preconditions.checkNotNull(payment.getUserRef().getKey(), "payment.UserRef");

        //TODO: Fees!\
        linkPaymentToTransfer(payment);

        Transfer transfer = createPaymentTransfer(payment);

        applyTransferTransaction(transfer, true);
        applyTransferTransaction(transfer, false);
    }

    /**
     * Allocate a new transferId for the transfer linked to this payment <b>or</b>
     * restore a previously allocated transferId
     *
     * @param executedPayment that is to be credited to the user
     * @return the newly allocated or restored transferId
     */
    private Key linkPaymentToTransfer(Payment executedPayment) {
        Key transferId = executedPayment.getTransferRef().getKey();
        if (transferId == null) {
            transferId = Datastore.allocateId(transferMeta);
            executedPayment.getTransferRef().setKey(transferId);

            com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
            try {
                Datastore.put(tx, executedPayment);
                tx.commit();
            } finally {
                if (tx.isActive())
                    tx.rollback();
            }
        } else {
            log.warn("Payment was already linked to transfer, Payment.Id={}, Transfer.Id={}", executedPayment.getId(), transferId);
        }
        return transferId;
    }

    /**
     * Creates or restores the transfer for a payment
     *
     * @param executedPayment the needs to be transfered
     * @return the created or restored transfer
     */
    private Transfer createPaymentTransfer(Payment executedPayment) {
        Preconditions.checkNotNull(executedPayment.getTransferRef().getKey());
        Key userKey = Preconditions.checkNotNull(executedPayment.getUserRef().getKey());

        Transfer transfer;

        Key userAccountKey = AccountUtil.memberAccountMustExist(userKey);

        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        try {
            transfer = Datastore.getOrNull(tx, transferMeta, executedPayment.getTransferRef().getKey());
            if (transfer == null) {
                Double amount = executedPayment.getAmount();
                Double fee = executedPayment.getFee();
                if (fee == null) {
                    fee = 0d;
                }

                amount -= fee;

                transfer = new Transfer();
                transfer.setId(executedPayment.getTransferRef().getKey());
                transfer.setAmount(amount);
                transfer.setCurrency(executedPayment.getCurrency());
                transfer.setDescription(executedPayment.describe());
                transfer.setReference(executedPayment.reference());
                transfer.getPayerLegRef().setKey(Datastore.allocateId(custodialAccountKey, transactionMeta));
                transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(userAccountKey, transactionMeta));

                Datastore.put(tx, transfer);

            } else {
                log.warn("Transfer already existed, Transfer.Id={}", executedPayment.getTransferRef().getKey());
            }
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
        return transfer;
    }

    private void applyTransferTransaction(Transfer transfer, boolean debit) {
        ModelRef<Transaction> legRef = debit ? transfer.getPayerLegRef() : transfer.getBeneficiaryLegRef();
        String desc = "Leg (" + (debit ? "Payer" : "Beneficiary") + ")";
        Preconditions.checkNotNull(legRef, desc);
        Preconditions.checkNotNull(legRef.getKey(), desc);
        Preconditions.checkNotNull(legRef.getKey().getParent(), desc + ".parent");

        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        try {
            Transaction transaction = Datastore.getOrNull(tx, transactionMeta, legRef.getKey());
            if (transaction == null) {
                transaction = new Transaction(transfer, debit);
                transaction.setId(legRef.getKey());
                transaction.getAccountRef().setKey(legRef.getKey().getParent());
                Key accountKey = transaction.getAccountRef().getKey();
                Account account = Datastore.getOrNull(tx, accountMeta, accountKey);
                if (account == null) {
                    log.warn("Creating new account for key={}", accountKey);
                    account = new Account(accountKey);
                    account.setCurrency(transaction.getCurrency()); //TODO: multiple currencies?
                }

                account.setBalance(account.getBalance() + transaction.getAmount());

                Datastore.put(tx, account, transaction);

            } else {
                log.warn("Transaction already existed, Transaction.Id={}, desc={}", legRef.getKey(), desc);
            }
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
    }

    private static class Singleton {
        private static final BalanceService INSTANCE = new DefaultBalanceService();
    }
}
