package com.jasify.schedule.appengine.memcache;

import com.google.appengine.api.memcache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 24/05/15.
 */
public final class Memcache {
    private static final Logger log = LoggerFactory.getLogger(Memcache.class);

    private static final ConsistentErrorHandler THE_ERROR_HANDLER = new TheErrorHandler();

    private Memcache() {
    }

    static MemcacheService delegate() {
        MemcacheService service = MemcacheServiceFactory.getMemcacheService();
        service.setErrorHandler(THE_ERROR_HANDLER);
        return service;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object whatever) {
        return (T) whatever;
    }

    public static <T> T get(Object key) throws IllegalArgumentException {
        return cast(delegate().get(key));
    }

    public static boolean contains(Object key) throws IllegalArgumentException {
        return cast(delegate().contains(key));
    }

    public static void put(Object key, Object value) throws IllegalArgumentException {
        delegate().put(key, value);
    }

    public static void put(Object key, Object value, Expiration expires) throws IllegalArgumentException {
        delegate().put(key, value, expires);
    }

    public static boolean delete(Object key) throws IllegalArgumentException {
        return delegate().delete(key);
    }

    private static class TheErrorHandler implements ConsistentErrorHandler {
        @Override
        public void handleDeserializationError(InvalidValueException e) {
            log.warn("Failed to deserialize memcache entry (did we upgrade jasify?)", e);
        }

        @Override
        public void handleServiceError(MemcacheServiceException e) {
            log.warn("Service error in memcache", e);
        }
    }
}
