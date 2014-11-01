package com.jasify.sandbox.appengine.model;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import java.util.Date;

/**
 * Created by krico on 01/11/14.
 */
@Model
public class Group {
    @Attribute(primaryKey = true)
    private Key name;

    private Date created;

    private Date modified;

    public Key getName() {
        return name;
    }

    public void setName(Key name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name=" + name +
                ", created=" + created +
                ", modified=" + modified +
                '}';
    }
}
