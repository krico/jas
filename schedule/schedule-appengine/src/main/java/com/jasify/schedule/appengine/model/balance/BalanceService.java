package com.jasify.schedule.appengine.model.balance;

import com.jasify.schedule.appengine.model.activity.Subscription;
import com.jasify.schedule.appengine.model.payment.Payment;

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

    void subscription(Subscription subscription, Account payer, Account beneficiary);
}
