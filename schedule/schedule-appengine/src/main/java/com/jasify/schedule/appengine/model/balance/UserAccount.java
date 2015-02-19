package com.jasify.schedule.appengine.model.balance;

import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 19/02/15.
 */
@Model
public class UserAccount extends Account {
    private ModelRef<User> userRef = new ModelRef<>(User.class);

    public ModelRef<User> getUserRef() {
        return userRef;
    }
}
