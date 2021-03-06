package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.balance.HasTransfer;
import com.jasify.schedule.appengine.model.balance.Transfer;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 11/01/15.
 */
@Model
public class Subscription implements HasTransfer {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private ModelRef<Activity> activityRef = new ModelRef<>(Activity.class);

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    private ModelRef<Transfer> transferRef = new ModelRef<>(Transfer.class);

    @Override
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

    public ModelRef<Activity> getActivityRef() {
        return activityRef;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }

    @Override
    public ModelRef<Transfer> getTransferRef() {
        return transferRef;
    }
}
