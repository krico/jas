package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.users.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author krico
 * @since 05/01/15.
 */
public class JasUserList implements JasEndpointEntity {
    private int total;
    private List<User> users = new ArrayList<>();

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void addAll(List<User> users) {
        if (this.users == null) this.users = new ArrayList<>();
        this.users.addAll(users);
    }

    public int size() {
        return users == null ? -1 : users.size();
    }

    public User get(int i) {
        return users == null ? null : users.get(i);
    }
}
