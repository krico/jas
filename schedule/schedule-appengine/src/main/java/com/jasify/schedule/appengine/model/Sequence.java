package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

/**
 * @author krico
 * @since 05/08/15.
 */
@Model(kind = "SEQ")
public class Sequence {
    @Attribute(primaryKey = true)
    private Key name;
    private Long next;

    public Key getName() {
        return name;
    }

    public void setName(Key name) {
        this.name = name;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }
}
