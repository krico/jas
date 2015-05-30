package com.jasify.schedule.appengine.dao;

import com.jasify.schedule.appengine.model.UniqueConstraintException;

/**
 * @author krico
 * @since 29/05/15.
 */
public interface UniqueIndex {
    void reserve(String uniqueValue) throws UniqueConstraintException;

    void release(String uniqueValue);
}
