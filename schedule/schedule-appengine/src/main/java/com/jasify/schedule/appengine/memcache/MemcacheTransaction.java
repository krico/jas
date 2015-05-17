package com.jasify.schedule.appengine.memcache;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author krico
 * @since 17/05/15.
 */
public interface MemcacheTransaction<T> {
    /**
     * @return an <b>immutable</b> key for the memcache entity manipulated by this operation
     */
    @Nonnull
    Object key();

    /**
     * @return the expiration for this entity on memcache or null
     */
    @Nullable
    Expiration expiration();

    /**
     * An idempotent manipulation of the entity in memcache.  If the entity is updated by a different thread, the
     * operation will be executed again.
     *
     * @param identifiable with the entity that was bound to {@link #key()} or null if nothing was bound to key
     * @return the new value that should be updated
     */
    @Nonnull
    T execute(@Nullable MemcacheService.IdentifiableValue identifiable);
}
