package com.jasify.schedule.appengine.model.payment;

import com.jasify.schedule.appengine.model.ModelException;

/**
 * @author krico
 * @since 14/02/15.
 */
public class PaymentException extends ModelException {
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(Throwable cause) {
        super(cause);
    }
}
