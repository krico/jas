package com.jasify.schedule.appengine.model.balance;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.common.ActivityDao;
import com.jasify.schedule.appengine.dao.common.ActivityPackageDao;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageExecutionMeta;
import com.jasify.schedule.appengine.meta.activity.SubscriptionMeta;
import com.jasify.schedule.appengine.meta.balance.*;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.balance.task.ApplyActivityPackageExecutionCharges;
import com.jasify.schedule.appengine.model.balance.task.ApplySubscriptionCharges;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.FormatUtil;
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
    private final ActivityPackageExecutionMeta activityPackageExecutionMeta;
    private final Key custodialAccountKey;

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();

    private DefaultBalanceService() {
        accountMeta = AccountMeta.get();
        userAccountMeta = UserAccountMeta.get();
        organizationAccountMeta = OrganizationAccountMeta.get();
        transferMeta = TransferMeta.get();
        transactionMeta = TransactionMeta.get();
        subscriptionMeta = SubscriptionMeta.get();
        activityPackageExecutionMeta = ActivityPackageExecutionMeta.get();
        custodialAccountKey = Datastore.createKey(accountMeta, AccountUtil.CUSTODIAL_ACCOUNT);
    }

    static BalanceService instance() {
        return Singleton.INSTANCE;
    }


    @Override
    public void unpaidSubscription(Key subscriptionId) throws EntityNotFoundException {
        Subscription subscription = Datastore.get(subscriptionMeta, subscriptionId);
        Activity activity = activityDao.get(subscription.getActivityRef().getKey());
        ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
        Key organizationId = activityType.getOrganizationRef().getKey();
        Account beneficiary = AccountUtil.memberAccountMustExist(organizationId);
        Account payer = AccountUtil.memberAccountMustExist(subscription.getUserRef().getKey());
        subscription(subscription, payer, beneficiary, true);
    }

    public void subscription(Key subscriptionId) throws EntityNotFoundException {
        subscription(Datastore.get(subscriptionMeta, subscriptionId));
    }

    @Override
    public void subscription(Subscription subscription) throws EntityNotFoundException {
        Activity activity = activityDao.get(subscription.getActivityRef().getKey());
        ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
        Key organizationId = activityType.getOrganizationRef().getKey();
        Account beneficiary = AccountUtil.memberAccountMustExist(organizationId);
        Account payer = AccountUtil.memberAccountMustExist(subscription.getUserRef().getKey());
        subscription(subscription, payer, beneficiary, false);
    }

    private void subscription(Subscription subscription, Account payer, Account beneficiary, boolean unpaid) throws EntityNotFoundException {
        //TODO: Validate balance is there before we start.

        linkToTransfer(subscription);

        Transfer transfer = createSubscriptionTransfer(subscription, payer, beneficiary, unpaid);
        applyTransfer(transfer);

        Queue queue = QueueFactory.getQueue("balance-queue");
        queue.add(TaskOptions.Builder.withPayload(new ApplySubscriptionCharges(subscription.getId())));

    }

    @Override
    public void unpaidActivityPackageExecution(Key activityPackageExecutionId) throws EntityNotFoundException {
        ActivityPackageExecution activityPackageExecution = Datastore.get(activityPackageExecutionMeta, activityPackageExecutionId);
        ActivityPackage activityPackage = activityPackageDao.get(activityPackageExecution.getActivityPackageRef().getKey());
        Key organizationId = activityPackage.getOrganizationRef().getKey();
        Account beneficiary = AccountUtil.memberAccountMustExist(organizationId);
        Account payer = AccountUtil.memberAccountMustExist(activityPackageExecution.getUserRef().getKey());
        activityPackageExecution(activityPackageExecution, payer, beneficiary, true);

    }

    @Override
    public void activityPackageExecution(ActivityPackageExecution activityPackageExecution) throws EntityNotFoundException {
        activityPackageExecution(activityPackageExecution.getId());
    }

    @Override
    public void activityPackageExecution(Key activityPackageExecutionId) throws EntityNotFoundException {
        ActivityPackageExecution activityPackageExecution = Datastore.get(activityPackageExecutionMeta, activityPackageExecutionId);
        ActivityPackage activityPackage = activityPackageDao.get(activityPackageExecution.getActivityPackageRef().getKey());
        Key organizationId = activityPackage.getOrganizationRef().getKey();
        Account beneficiary = AccountUtil.memberAccountMustExist(organizationId);
        Account payer = AccountUtil.memberAccountMustExist(activityPackageExecution.getUserRef().getKey());
        activityPackageExecution(activityPackageExecution, payer, beneficiary, false);
    }

    private void activityPackageExecution(ActivityPackageExecution execution, Account payer, Account beneficiary, boolean unpaid) throws EntityNotFoundException {
        //TODO: Validate balance is there before we start.

        linkToTransfer(execution);

        Transfer transfer = createActivityPackageExecutionTransfer(execution, payer, beneficiary, unpaid);
        applyTransfer(transfer);

        Queue queue = QueueFactory.getQueue("balance-queue");
        queue.add(TaskOptions.Builder.withPayload(new ApplyActivityPackageExecutionCharges(execution.getId())));
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
        try {
            applyTransferTransaction(transfer, true);
        } catch (ModelException e) {
            // TODO: This should not happen but if it ever does schedule a task to retry
            log.error("Failed to make debit", e);
        }

        try {
            applyTransferTransaction(transfer, false);
        } catch (ModelException e) {
            // TODO: This should not happen but if it ever does schedule a task to retry
            log.error("Failed to make credit", e);
        }
    }

    /**
     * Allocate a new transferId for the transfer linked to this HasTransfer <b>or</b>
     * restore a previously allocated transferId
     *
     * @param hasTransfer that is to be credited to the user
     * @return the newly allocated or restored transferId
     */
    private Key linkToTransfer(final HasTransfer hasTransfer) {
        Key transferId = hasTransfer.getTransferRef().getKey();
        if (transferId == null) {
            transferId = Datastore.allocateId(transferMeta);
            hasTransfer.getTransferRef().setKey(transferId);

            TransactionOperator.executeNoEx(new ModelOperation<Object>() {
                @Override
                public Void execute(com.google.appengine.api.datastore.Transaction tx) {
                    Datastore.put(tx, hasTransfer);
                    tx.commit();
                    return null;
                }
            });
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
    private Transfer createPaymentTransfer(final Payment executedPayment) {
        Preconditions.checkNotNull(executedPayment.getTransferRef().getKey());
        Key userKey = Preconditions.checkNotNull(executedPayment.getUserRef().getKey());

        final Key userAccountKey = AccountUtil.memberAccountIdMustExist(userKey);

        return TransactionOperator.executeNoEx(new ModelOperation<Transfer>() {
            @Override
            public Transfer execute(com.google.appengine.api.datastore.Transaction tx) {
                Transfer transfer = Datastore.getOrNull(tx, transferMeta, executedPayment.getTransferRef().getKey());
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
                    // Datastore does not allow String properties to exceed 500 characters. Do some magic.
                    if (transfer.getDescription().length() > 500) {
                        transfer.setDescription(transfer.getDescription().substring(0, 490) + " ...");
                    }
                    transfer.setReference(executedPayment.reference());
                    transfer.getPayerLegRef().setKey(Datastore.allocateId(custodialAccountKey, transactionMeta));
                    transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(userAccountKey, transactionMeta));

                    executedPayment.onCreateTransfer(transfer);

                    Datastore.put(tx, transfer);
                } else {
                    log.warn("Transfer already existed, Transfer.Id={}", executedPayment.getTransferRef().getKey());
                }
                tx.commit();
                return transfer;
            }
        });
    }

    private Transfer createActivityPackageExecutionTransfer(final ActivityPackageExecution execution, final Account payer, final Account beneficiary, final boolean unpaid) throws EntityNotFoundException {
        try {
            return TransactionOperator.execute(new ModelOperation<Transfer>() {
                @Override
                public Transfer execute(com.google.appengine.api.datastore.Transaction tx) throws EntityNotFoundException {
                    Transfer transfer = Datastore.getOrNull(tx, transferMeta, execution.getTransferRef().getKey());
                    if (transfer == null) {
                        ActivityPackage activityPackage = activityPackageDao.get(execution.getActivityPackageRef().getKey());
                        Double amount = activityPackage.getPrice();
                        transfer = new Transfer();
                        transfer.setId(execution.getTransferRef().getKey());
                        transfer.setAmount(amount);
                        if (unpaid) { //cash payments are marked as unpaid
                            transfer.setUnpaid(amount);
                        }
                        transfer.setCurrency(activityPackage.getCurrency());
                        transfer.setDescription(FormatUtil.toString(activityPackage));
                        transfer.setReference(Objects.toString(execution.getId()));
                        transfer.getPayerLegRef().setKey(Datastore.allocateId(payer.getId(), transactionMeta));
                        transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(beneficiary.getId(), transactionMeta));

                        Datastore.put(tx, transfer);
                    } else {
                        log.warn("Transfer already existed, Transfer.Id={}", execution.getTransferRef().getKey());
                    }
                    tx.commit();
                    return transfer;
                }
            });
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    private Transfer createSubscriptionTransfer(final Subscription subscription, final Account payer, final Account beneficiary, final boolean unpaid) throws EntityNotFoundException {
        try {
            return TransactionOperator.execute(new ModelOperation<Transfer>() {
                @Override
                public Transfer execute(com.google.appengine.api.datastore.Transaction tx) throws EntityNotFoundException {
                    Transfer transfer = Datastore.getOrNull(tx, transferMeta, subscription.getTransferRef().getKey());
                    if (transfer == null) {
                        Activity activity = activityDao.get(subscription.getActivityRef().getKey());
                        Double amount = activity.getPrice();
                        transfer = new Transfer();
                        transfer.setId(subscription.getTransferRef().getKey());
                        transfer.setAmount(amount);
                        if (unpaid) { //cash payments are marked as unpaid
                            transfer.setUnpaid(amount);
                        }
                        transfer.setCurrency(activity.getCurrency());
                        transfer.setDescription(FormatUtil.toString(activity));
                        transfer.setReference(Objects.toString(subscription.getId()));
                        transfer.getPayerLegRef().setKey(Datastore.allocateId(payer.getId(), transactionMeta));
                        transfer.getBeneficiaryLegRef().setKey(Datastore.allocateId(beneficiary.getId(), transactionMeta));

                        Datastore.put(tx, transfer);
                        tx.commit();
                    } else {
                        log.warn("Transfer already existed, Transfer.Id={}", subscription.getTransferRef().getKey());
                    }
                    return transfer;
                }
            });
        } catch (ModelException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Transfer createTransfer(final Double amount, final String currency, final String description, final String reference, final Account payerAccount, final Account beneficiaryAccount) {
        return TransactionOperator.executeNoEx(new ModelOperation<Transfer>() {
            @Override
            public Transfer execute(com.google.appengine.api.datastore.Transaction tx) {
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
            }
        });
    }

    private void applyTransferTransaction(final Transfer transfer, final boolean debit) throws ModelException {
        final ModelRef<Transaction> legRef = debit ? transfer.getPayerLegRef() : transfer.getBeneficiaryLegRef();
        final String desc = "Leg (" + (debit ? "Payer" : "Beneficiary") + ")";
        Preconditions.checkNotNull(legRef, desc);
        Preconditions.checkNotNull(legRef.getKey(), desc);
        Preconditions.checkNotNull(legRef.getKey().getParent(), desc + ".parent");

        TransactionOperator.execute(new ModelOperation<Object>() {
            @Override
            public Void execute(com.google.appengine.api.datastore.Transaction tx) {
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

                    account.setBalance(account.getBalance() + transaction.getBalanceAmount());
                    account.setUnpaid(account.getUnpaid() + transaction.getUnpaid());

                    Datastore.put(tx, account, transaction);
                    tx.commit();
                } else {
                    log.warn("Transaction already existed, Transaction.Id={}, desc={}", legRef.getKey(), desc);
                }
                return null;
            }
        });
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
        return getOrganizationAccountFromAccountId(accountId);
    }

    @Override
    public OrganizationAccount getOrganizationAccountFromAccountId(Key accountId) throws EntityNotFoundException {
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
    public List<Account> listAccounts() {
        return Datastore.query(accountMeta).sort(accountMeta.id.desc).asList();
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
