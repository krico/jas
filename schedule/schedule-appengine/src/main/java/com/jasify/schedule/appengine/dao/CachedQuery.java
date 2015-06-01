package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;

import java.util.List;

/**
 * @author krico
 * @since 31/05/15.
 */
public interface CachedQuery {
    String key();

    List<Key> execute();
}
