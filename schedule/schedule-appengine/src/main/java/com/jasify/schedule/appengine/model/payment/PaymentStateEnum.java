package com.jasify.schedule.appengine.model.payment;

/**
 * @author krico
 * @since 11/01/15.
 */
public enum PaymentStateEnum {
    /**
     * Payment is only internal to jasify.
     */
    New,
    /**
     * We have created this payment externally, for example via REST on PayPal or sending an e-mail to the user.
     * This usually indicates that any {@link PaymentTypeEnum} specific information should be available
     */
    Created,
    /**
     * The payment has gone through, this is a final state.
     */
    Completed,
    /**
     * Payment was canceled
     */
    Canceled
}
