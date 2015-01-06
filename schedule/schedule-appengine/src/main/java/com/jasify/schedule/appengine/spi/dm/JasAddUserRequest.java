package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserLogin;

/**
 * @author krico
 * @since 05/01/15.
 */
public class JasAddUserRequest implements JasEndpointEntity {
    private User user;
    private String password;
    private UserLogin login;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserLogin getLogin() {
        return login;
    }

    public void setLogin(UserLogin login) {
        this.login = login;
    }
}
