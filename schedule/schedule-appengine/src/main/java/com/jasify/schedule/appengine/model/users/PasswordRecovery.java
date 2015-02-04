package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import java.util.Date;

/**
 * @author krico
 * @since 03/02/15.
 */
@Model
public class PasswordRecovery {
    @Attribute(primaryKey = true)
    private Key code;

    @Attribute(listener = CreationDate.class)
    private Date created;

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    public Key getCode() {
        return code;
    }

    public void setCode(Key code) {
        this.code = code;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }
}
