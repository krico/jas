package com.jasify.schedule.appengine.model;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.ConcurrentModificationException;

/**
 * @author krico
 * @since 04/05/15.
 */
public final class TransactionOperator {
    public static final int DEFAULT_RETRY_COUNT = 5;
    public static final long DEFAULT_RETRY_SLEEP_MILLIS = 10;
    private static final Logger log = LoggerFactory.getLogger(TransactionOperator.class);

    private final int retryCount;
    private final long retrySleepMillis;

    private TransactionOperator() {
        ApplicationData applicationData = ApplicationData.instance();
        String retryCountKey = TransactionOperator.class.getName() + ".RetryCount";
        Integer retryCount = applicationData.getProperty(retryCountKey);
        if (retryCount == null) {
            retryCount = DEFAULT_RETRY_COUNT;
            applicationData.setProperty(retryCountKey, retryCount);
        }
        this.retryCount = Math.max(0, retryCount);
        String retrySleepKey = TransactionOperator.class.getName() + ".RetrySleepMillis";
        Long retrySleep = applicationData.getProperty(retrySleepKey);
        if (retrySleep == null) {
            retrySleep = DEFAULT_RETRY_SLEEP_MILLIS;
            applicationData.setProperty(retryCountKey, retrySleep);
        }
        this.retrySleepMillis = Math.max(0, retrySleep);

    }

    public static void execute(TransactionOperation operation) throws ConcurrentModificationException, TransactionOperationException {
        Singleton.INSTANCE.executeImpl(operation);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            //don't care
        }
    }

    private void executeImpl(TransactionOperation operation) throws ConcurrentModificationException, TransactionOperationException {
        int retries = retryCount;
        while (true) {
            Transaction tx = Datastore.beginTransaction();
            try {
                operation.execute(tx);
                break;
            } catch (ConcurrentModificationException cme) {
                if (retries == 0) throw cme;
                --retries;
                log.debug("ConcurrentModificationException, retry {} of {} will sleep {} ms", (retryCount - retries), retryCount, retrySleepMillis);
                sleep(retrySleepMillis);
            } catch (Exception e) {
                log.debug("Exception executing operation", e);
                Throwables.propagateIfInstanceOf(e, TransactionOperationException.class);
                throw new TransactionOperationException(e);
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
