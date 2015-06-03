package com.jasify.schedule.appengine.memcache;

import com.google.appengine.api.memcache.Expiration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author krico
 * @since 18/05/15.
 */
public abstract class BaseMemcacheTransaction<T> implements MemcacheTransaction<T> {
    private final Object key;
    private Expiration expiration;

    public BaseMemcacheTransaction(Object key) {
        this(key, null);
    }

    public BaseMemcacheTransaction(Object key, Expiration expiration) {
        this.key = key;
        this.expiration = expiration;
    }

    @Nonnull
    @Override
    public Object key() {
        return key;
    }

    @Nullable
    @Override
    public Expiration expiration() {
        return expiration;
    }

    @Override
    public boolean skipMemcacheUpdate() {
        return false;
    }
}
