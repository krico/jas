package com.jasify.schedule.appengine.model.payment;

/**
 * @author krico
 * @since 11/01/15.
 */
public enum PaymentStateEnum {
    /**
     * Payment is only internal to jasify.
     */
    New(false),
    /**
     * We have created this payment externally, for example via REST on PayPal or sending an e-mail to the user.
     * This usually indicates that any {@link PaymentTypeEnum} specific information should be available
     */
    Created(false),
    /**
     * The payment has gone through, this is a final state.
     */
    Completed(true),
    /**
     * Payment was canceled
     */
    Canceled(true);

    private final boolean finalState;

    PaymentStateEnum(boolean isFinal) {
        finalState = isFinal;
    }

    public boolean isFinal() {
        return finalState;
    }
}
