package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.users.User;

import java.util.List;

/**
 * @author krico
 * @since 20/02/15.
 */
public interface BalanceService {


    /**
     * Apply a Payment to the balance system.
     * <p/>
     * Requirements:
     * <ul>
     * <li>payment must be "executed", meaning the money should have been transferred</li>
     * <li>payment.UserRef must point to a user (cannot be null)</li>
     * </ul>
     * Creates:
     * <ul>
     * <li>A Transfer that is linked to payment.TransferRef</li>
     * <li>a <b>debit</b> Transaction against {@link AccountUtil#CUSTODIAL_ACCOUNT}</li>
     * <li>a <b>credit</b> Transaction against the user's account (from UserRef)</li>
     * <li>Fees are also handled (not clearly how at the time of this doc)</li>
     * </ul>
     * <b>Missing account are created by this procedure if they don't exist</b>
     *
     * @param payment - a payment that has been executed and needs to be reflected on the balance
     */
    void payment(Payment payment);

    void subscription(Key subscriptionId) throws EntityNotFoundException;

    void subscription(Subscription subscription) throws EntityNotFoundException;

    void subscription(Subscription subscription, Account payer, Account beneficiary) throws EntityNotFoundException;

    Transfer createTransfer(Double amount, String currency, String description, String reference, Account payerAccount, Account beneficiaryAccount);

    void applyTransfer(Transfer transfer);

    UserAccount getUserAccount(long userId) throws EntityNotFoundException;

    UserAccount getUserAccount(Key userId) throws EntityNotFoundException;

    UserAccount getUserAccount(User user) throws EntityNotFoundException;

    OrganizationAccount getOrganizationAccount(Key organizationId) throws EntityNotFoundException;

    OrganizationAccount getOrganizationAccount(Organization organization) throws EntityNotFoundException;

    List<Account> listAccounts();

    List<Transaction> listTransactions(Key accountId) throws EntityNotFoundException;

    List<Transaction> listTransactions(Account account) throws EntityNotFoundException;

    List<Transaction> listTransactions(Account account, int offset, int limit) throws EntityNotFoundException;

    List<Transaction> listTransactions(Key accountId, int offset, int limit) throws EntityNotFoundException;

    int getTransactionCount(Key accountId);
}
