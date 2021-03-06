package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;

import java.io.Serializable;
import java.util.List;

/**
 * @author krico
 * @since 31/05/15.
 */
public interface DaoQuery extends Serializable {
    List<Key> execute();

    QueryParameters parameters();
}
