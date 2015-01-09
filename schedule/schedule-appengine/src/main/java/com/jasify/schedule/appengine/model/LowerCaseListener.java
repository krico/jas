package com.jasify.schedule.appengine.model;

import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.AttributeListener;

/**
 * @author krico
 * @since 09/01/15.
 */
public class LowerCaseListener implements AttributeListener<String> {
    @Override
    public String prePut(String value) {
        return StringUtils.lowerCase(value);
    }
}
