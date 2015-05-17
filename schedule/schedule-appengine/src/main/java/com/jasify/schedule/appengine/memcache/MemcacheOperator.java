package com.jasify.schedule.appengine.memcache;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.util.Threads;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.memcache.S3ErrorHandler;

import java.util.ConcurrentModificationException;

/**
 * @author krico
 * @since 18/05/15.
 */
public final class MemcacheOperator {
    public static final int DEFAULT_RETRY_COUNT = 50;
    public static final long DEFAULT_RETRY_SLEEP_MILLIS = 50;
    private static final Logger log = LoggerFactory.getLogger(MemcacheOperator.class);

    private final Statistics statistics = new Statistics();
    private final int retryCount;
    private final long retrySleepMillis;

    private MemcacheOperator() {
        ApplicationData applicationData = ApplicationData.instance();

        String retryCountKey = getClass().getName() + ".RetryCount";
        Integer retryCount = applicationData.getPropertyWithDefaultValue(retryCountKey, DEFAULT_RETRY_COUNT);
        this.retryCount = Math.max(0, retryCount);

        String retrySleepKey = getClass().getName() + ".RetrySleepMillis";
        long retrySleep = applicationData.getPropertyWithDefaultValue(retrySleepKey, DEFAULT_RETRY_SLEEP_MILLIS);
        this.retrySleepMillis = Math.max(0, retrySleep);
    }

    public static <T> T update(MemcacheTransaction<T> operation) {
        return Singleton.INSTANCE.updateImpl(operation);
    }

    public static Statistics statistics() {
        return Singleton.INSTANCE.statistics;
    }

    private <T> T updateImpl(MemcacheTransaction<T> operation) {
        MemcacheService service = MemcacheServiceFactory.getMemcacheService();
        service.setErrorHandler(new S3ErrorHandler());

        final Object key = operation.key();

        int retries = retryCount;
        do {
            MemcacheService.IdentifiableValue identifiable = service.getIdentifiable(key);
            final T newVal = operation.execute(identifiable);
            Preconditions.checkNotNull(newVal, "MemcacheUpdate cannot return NULL");
            Expiration expiration = operation.expiration();

            if (identifiable == null) {

                //New value, add only if not present
                if (service.put(key, newVal, expiration, MemcacheService.SetPolicy.ADD_ONLY_IF_NOT_PRESENT)) {
                    return newVal; //Value added
                }

            } else {

                //Existing value, update only if untouched
                if (service.putIfUntouched(key, identifiable, newVal)) {
                    return newVal;
                }
            }
            if (retries == 0) {
                throw new ConcurrentModificationException("Failed to update memcache (" + retryCount + " attempts) k:" + key);
            }
            --retries;
            statistics.retries++;
            long willSleep = 1 + RandomUtils.nextLong(retrySleepMillis / 2, retrySleepMillis);
            statistics.sleep += willSleep;
            log.info("ConcurrentModificationException, retry {} of {} will sleep {} ms", (retryCount - retries), retryCount, willSleep);
            Threads.sleep(willSleep);
        } while (true);
    }

    public static class Statistics {
        private long retries;
        private long sleep;
        private long failed;

        public long getRetries() {
            return retries;
        }

        public long getSleep() {
            return sleep;
        }

        public void reset() {
            retries = 0;
            sleep = 0;
        }

        public long getFailed() {
            return failed;
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "retries=" + retries +
                    ", sleep=" + sleep +
                    ", failed=" + failed +
                    '}';
        }

    }

    private static class Singleton {
        private static final MemcacheOperator INSTANCE = new MemcacheOperator();
    }
}
