package com.jasify.schedule.appengine.model;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.Constants;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;

/**
 * @author krico
 * @since 26/11/14.
 */
@Model(kind = "Msg", schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 0)
public class MailMessage {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String pathInfo;

    private String from;

    private String subject;

    private Blob messageData;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
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

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Blob getMessageData() {
        return messageData;
    }

    public void setMessageData(Blob messageData) {
        this.messageData = messageData;
    }
}
