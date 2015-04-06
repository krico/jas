package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.balance.*;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.balance.task.ApplySubscriptionCharges;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;
import org.slim3.datastore.ModelRef;

import java.util.List;
import java.util.Objects;

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
    private final SubscriptionMeta subscriptionMeta;
    private final Key custodialAccountKey;

    private DefaultBalanceService() {
        accountMeta = AccountMeta.get();
        userAccountMeta = UserAccountMeta.get();
        organizationAccountMeta = OrganizationAccountMeta.get();
        transferMeta = TransferMeta.get();
        transactionMeta = TransactionMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
        custodialAccountKey = Datastore.createKey(accountMeta, AccountUtil.CUSTODIAL_ACCOUNT);
    }

    static BalanceService instance() {
        return Singleton.INSTANCE;
    }


    @Override
    public void subscription(Key subscriptionId) throws EntityNotFoundException {
        subscription(Datastore.get(subscriptionMeta, subscriptionId));
    }

    @Override
    public void subscription(Subscription subscription) throws EntityNotFoundException {
        Activity activity = subscription.getActivityRef().getModel();
        ActivityType activityType = activity.getActivityTypeRef().getModel();
        Key organizationId = activityType.getOrganizationRef().getKey();
        Account beneficiary = AccountUtil.memberAccountMustExist(organizationId);
        Account payer = AccountUtil.memberAccountMustExist(subscription.getUserRef().getKey());
        subscription(subscription, payer, beneficiary);
    }

    @Override
    public void subscription(Subscription subscription, Account payer, Account beneficiary) {

        //TODO: Validate balance is there before we start.

        linkToTransfer(subscription);

        Transfer transfer = createSubscriptionTransfer(subscription, payer, beneficiary);
        applyTransfer(transfer);

        Queue queue = QueueFactory.getQueue("balance-queue");
        queue.add(TaskOptions.Builder.withPayload(new ApplySubscriptionCharges(subscription.getId())));

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

        linkToTransfer(payment);

        Transfer transfer = createPaymentTransfer(payment);

        applyTransfer(transfer);
    }

    @Override
    public void applyTransfer(Transfer transfer) {
        applyTransferTransaction(transfer, true);
        applyTransferTransaction(transfer, false);
    }

    /**
     * Allocate a new transferId for the transfer linked to this HasTransfer <b>or</b>
     * restore a previously allocated transferId
     *
     * @param hasTransfer that is to be credited to the user
     * @return the newly allocated or restored transferId
     */
    private Key linkToTransfer(HasTransfer hasTransfer) {
        Key transferId = hasTransfer.getTransferRef().getKey();
        if (transferId == null) {
            transferId = Datastore.allocateId(transferMeta);
            hasTransfer.getTransferRef().setKey(transferId);

            com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
            try {
                Datastore.put(tx, hasTransfer);
                tx.commit();
            } finally {
                if (tx.isActive())
                    tx.rollback();
            }
        } else {
            log.warn("{} was already linked to transfer, HasTransfer.Id={}, Transfer.Id={}", hasTransfer.getClass().getName(), hasTransfer.getId(), transferId);
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

        Key userAccountKey = AccountUtil.memberAccountIdMustExist(userKey);

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

    private Transfer createSubscriptionTransfer(Subscription subscription, Account payer, Account beneficiary) {
        Transfer transfer;
        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        try {
            transfer = Datastore.getOrNull(tx, transferMeta, subscription.getTransferRef().getKey());
            if (transfer == null) {
                Activity activity = subscription.getActivityRef().getModel();
                Double amount = activity.getPrice();
                transfer = new Transfer();
                transfer.setId(subscription.getTransferRef().getKey());
                transfer.setAmount(amount);
                transfer.setCurrency(activity.getCurrency());
                transfer.setDescription(activity.getName());
                transfer.setReference(Objects.toString(subscription.getId()));
                transfer.getPayerLegRef().setKey(Datastore.allocateId(payer.getId(), transactionMeta));
                transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(beneficiary.getId(), transactionMeta));

                Datastore.put(tx, transfer);

            } else {
                log.warn("Transfer already existed, Transfer.Id={}", subscription.getTransferRef().getKey());
            }
            tx.commit();
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
        return transfer;
    }

    @Override
    public Transfer createTransfer(Double amount, String currency, String description, String reference, Account payerAccount, Account beneficiaryAccount) {
        com.google.appengine.api.datastore.Transaction tx = Datastore.beginTransaction();
        try {
            Transfer transfer = new Transfer();
            transfer.setId(Datastore.allocateId(transferMeta));
            transfer.setAmount(amount);
            transfer.setCurrency(currency);
            transfer.setDescription(description);
            transfer.setReference(reference);
            transfer.getPayerLegRef().setKey(Datastore.allocateId(payerAccount.getId(), transactionMeta));
            transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(beneficiaryAccount.getId(), transactionMeta));

            Datastore.put(tx, transfer);
            tx.commit();

            return transfer;
        } finally {
            if (tx.isActive())
                tx.rollback();
        }
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

    @Override
    public UserAccount getUserAccount(long userId) throws EntityNotFoundException {
        return getUserAccount(Datastore.createKey(User.class, userId));
    }

    @Override
    public UserAccount getUserAccount(Key userId) throws EntityNotFoundException {
        Key accountId = AccountUtil.memberIdToAccountId(userId);
        UserAccount ret = Datastore.getOrNull(userAccountMeta, accountId);
        if (ret == null) {
            throw new EntityNotFoundException("UserAccount");
        }
        return ret;
    }

    @Override
    public UserAccount getUserAccount(User user) throws EntityNotFoundException {
        return getUserAccount(user.getId());
    }

    @Override
    public OrganizationAccount getOrganizationAccount(Key organizationId) throws EntityNotFoundException {
        Key accountId = AccountUtil.memberIdToAccountId(organizationId);
        OrganizationAccount ret = Datastore.getOrNull(organizationAccountMeta, accountId);
        if (ret == null) {
            throw new EntityNotFoundException("OrganizationAccount");
        }
        return ret;
    }

    @Override
    public OrganizationAccount getOrganizationAccount(Organization organization) throws EntityNotFoundException {
        return getOrganizationAccount(organization.getId());
    }

    @Override
    public List<Transaction> listTransactions(Key accountId) throws EntityNotFoundException {
        return listTransactions(accountId, 0, 0);
    }

    @Override
    public List<Transaction> listTransactions(Account account) throws EntityNotFoundException {
        return listTransactions(account.getId(), 0, 0);
    }

    @Override
    public List<Transaction> listTransactions(Account account, int offset, int limit) throws EntityNotFoundException {
        return listTransactions(account.getId(), offset, limit);
    }

    public List<Transaction> listTransactions(Key accountId, int offset, int limit) throws EntityNotFoundException {
        ModelQuery<Transaction> query = Datastore.query(transactionMeta, accountId)
                .sort(transactionMeta.created.desc);

        if (limit > 0)
            query.limit(limit);

        if (offset > 0)
            query.offset(offset);

        return query.asList();
    }

    @Override
    public int getTransactionCount(Key accountId) {
        return Datastore.query(transactionMeta, accountId).asKeyList().size();
    }

    private static class Singleton {
        private static final BalanceService INSTANCE = new DefaultBalanceService();
    }
}
