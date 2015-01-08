package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 08/01/15.
 */
@Model
public class GroupUser {
    @Attribute(primaryKey = true)
    private Key id;

    private ModelRef<Group> groupRef = new ModelRef<>(Group.class);

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public ModelRef<Group> getGroupRef() {
        return groupRef;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }
}
