package com.jasify.schedule.appengine.model.payment.workflow;

/**
 * @author krico
 * @since 05/04/15.
 */
public class PaymentWorkflowException extends Exception {
    public PaymentWorkflowException(String message) {
        super(message);
    }

    public PaymentWorkflowException(Throwable cause) {
        super(cause);
    }
}
