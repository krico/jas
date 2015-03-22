package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;

/**
 * @author krico
 * @since 31/01/15.
 */
public class JasProviderAuthenticateResponse extends JasLoginResponse {
    private String data;

    public JasProviderAuthenticateResponse(String data) {
        this.data = data;
    }

    public JasProviderAuthenticateResponse(User user, UserSession userSession, String data) {
        super(user, userSession);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
