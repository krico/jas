package com.jasify.schedule.appengine.model.history;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import java.util.Date;

/**
 * @author krico
 * @since 09/08/15.
 */
@Model
public class History {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    private HistoryTypeEnum type;

    private String message;
    /**
     * The user logged in at creation time
     */
    private ModelRef<User> currentUserRef = new ModelRef<>(User.class);

    public History() {
    }

    public History(HistoryTypeEnum type) {
        this.type = type;
    }

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

    public HistoryTypeEnum getType() {
        return type;
    }

    public void setType(HistoryTypeEnum type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ModelRef<User> getCurrentUserRef() {
        return currentUserRef;
    }
}
