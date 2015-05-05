package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Transaction;

/**
 * @author krico
 * @since 04/05/15.
 */
public interface TransactionOperation<T, Ex extends Exception> {
    /**
     * Execute an atomic operation based on a datastore transaction.
     * This method should be written so that if the datastore throws a ConcurrentModificationException, the transaction
     * can be rolled back and the operation can be executed again several times.
     *
     * @param tx the transaction for all operations (you <b>must</b> commit the transaction when you are done)
     * @throws Ex if there are problems with the operation
     */
    T execute(Transaction tx) throws Ex;
}
