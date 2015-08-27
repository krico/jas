package com.jasify.schedule.appengine.model.payment.workflow;

/**
 * @author krico
 * @since 27/08/15.
 */
public interface PaymentWorkflowHandler {
    void onCreated() throws PaymentWorkflowException;

    void onCanceled() throws PaymentWorkflowException;

    void onCompleted() throws PaymentWorkflowException;
}
