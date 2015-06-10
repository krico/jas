package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.Key;

import java.util.List;

/**
 * @author krico
 * @since 30/05/15.
 */
public interface AnyExampleDao {

    List<Example> byDataType(String dataType);

    List<Example> byAncestor(Key ancestor);
}
