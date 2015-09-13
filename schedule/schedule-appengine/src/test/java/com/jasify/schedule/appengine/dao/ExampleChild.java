package com.jasify.schedule.appengine.dao;

import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 07/09/15.
 */
@Model
public class ExampleChild extends Example {
    private String childField;

    public String getChildField() {
        return childField;
    }

    public void setChildField(String childField) {
        this.childField = childField;
    }

    @Override
    public String toString() {
        return "ExampleChild{" +
                "childField='" + childField + '\'' +
                '}' +
                super.toString();
    }
}
