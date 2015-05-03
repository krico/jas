package com.jasify.schedule.appengine.model;

/**
 * @author krico
 * @since 04/05/15.
 */
public class TransactionOperationException extends Exception {
    public TransactionOperationException(Exception cause) {
        super(cause);
    }
}
