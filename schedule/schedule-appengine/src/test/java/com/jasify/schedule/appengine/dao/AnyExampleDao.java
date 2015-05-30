package com.jasify.schedule.appengine.dao;

import java.util.List;

/**
 * @author krico
 * @since 30/05/15.
 */
public interface AnyExampleDao {

    List<Example> byDataType(String dataType);
}
