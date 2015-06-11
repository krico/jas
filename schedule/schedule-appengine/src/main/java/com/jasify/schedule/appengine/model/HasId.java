package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;

/**
 * Interface for model entities that have <code>private Key id</code> as their primary key.
 *
 * @author krico
 * @since 11/06/15.
 */
public interface HasId {

    Key getId();

}
