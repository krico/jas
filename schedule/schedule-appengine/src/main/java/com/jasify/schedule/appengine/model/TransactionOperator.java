package com.jasify.schedule.appengine.model;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import java.util.ConcurrentModificationException;

/**
 * @author krico
 * @since 04/05/15.
 */
public final class TransactionOperator {
    public static final long DEFAULT_RETRY_COUNT = 10;
    public static final long DEFAULT_RETRY_SLEEP_MILLIS = 50;

    private static final Logger log = LoggerFactory.getLogger(TransactionOperator.class);

    private final int retryCount;
    private final long retrySleepMillis;

    private TransactionOperator() {
        ApplicationData applicationData = ApplicationData.instance();

        String retryCountKey = TransactionOperator.class.getName() + ".RetryCount";
        Long retryCount = applicationData.getPropertyWithDefaultValue(retryCountKey, DEFAULT_RETRY_COUNT);
        this.retryCount = Math.max(0, retryCount.intValue());

        String retrySleepKey = TransactionOperator.class.getName() + ".RetrySleepMillis";
        long retrySleep = applicationData.getPropertyWithDefaultValue(retrySleepKey, DEFAULT_RETRY_SLEEP_MILLIS);
        this.retrySleepMillis = Math.max(0, retrySleep);
    }

    /**
     * Is a convenience method that propagates any exception a a runtime exception.
     *
     * @param operation to execute
     * @param <T>       return type
     * @return the return of operation
     * @throws ConcurrentModificationException if we failed to complete the transaction more than retryCount times
     */
    public static <T> T executeNoEx(TransactionOperation<T, ?> operation) throws ConcurrentModificationException {
        try {
            return execute(operation);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Execute the operation, retry <code>retryCount</code> times in case of Concurrent modification
     *
     * @param operation to execute
     * @param <T>       return type
     * @param <Ex>      the exception that can be thrown
     * @return the return of operation
     * @throws ConcurrentModificationException if we failed to complete the transaction more than retryCount times
     * @throws Ex                              if operation throws Ex
     */
    public static <T, Ex extends Exception> T execute(TransactionOperation<T, Ex> operation) throws ConcurrentModificationException, Ex {
        return Singleton.INSTANCE.executeImpl(operation);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //don't care
        }
    }

    private <T, Ex extends Exception> T executeImpl(TransactionOperation<T, Ex> operation) throws ConcurrentModificationException, Ex {
        int retries = retryCount;
        while (true) {
            Transaction tx = Datastore.beginTransaction();
            try {
                return operation.execute(tx);
            } catch (EntityNotFoundRuntimeException ere) {

                //As a convenience we replace EntityNotFoundRUNTIME with our own EntityNotFound exception
                if (operation instanceof ModelOperation) {
                    //noinspection unchecked
                    throw (Ex) new EntityNotFoundException(ere);
                }

                throw ere;
            } catch (ConcurrentModificationException cme) {
                if (retries == 0) throw cme;
                --retries;
                log.info("ConcurrentModificationException, retry {} of {} will sleep {} ms", (retryCount - retries), retryCount, retrySleepMillis);
                sleep(retrySleepMillis);
            } finally {
                if (tx.isActive()) {
                    log.debug("Rolling back transaction");
                    tx.rollback();
                }
            }
        }
    }

    private static class Singleton {
        private static final TransactionOperator INSTANCE = new TransactionOperator();
    }
}
