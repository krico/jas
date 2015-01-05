package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.users.User;

import java.util.ArrayList;

/**
 * @author krico
 * @since 05/01/15.
 */
public class JasUserList extends ArrayList<User> implements JasEndpointEntity {
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
