package com.jasify.schedule.appengine.model.payment.workflow;

import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;

/**
 * @author krico
 * @since 05/04/15.
 */
public class InvalidWorkflowTransitionException extends PaymentWorkflowException {
    private final PaymentStateEnum fromState;
    private final PaymentStateEnum toState;

    public InvalidWorkflowTransitionException(PaymentStateEnum fromState, PaymentStateEnum toState) {
        super("[" + fromState + " -> " + toState + "]");
        this.fromState = fromState;
        this.toState = toState;
    }

    public PaymentStateEnum getFromState() {
        return fromState;
    }

    public PaymentStateEnum getToState() {
        return toState;
    }
}
