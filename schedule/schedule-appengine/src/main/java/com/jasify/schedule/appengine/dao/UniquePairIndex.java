package com.jasify.schedule.appengine.dao;

import com.jasify.schedule.appengine.model.UniqueConstraintException;

/**
 * @author krico
 * @since 30/05/15.
 */
public interface UniquePairIndex {
    void reserve(String uniqueValue, String classifier) throws UniqueConstraintException;

    void release(String uniqueValue, String classifier);
}
