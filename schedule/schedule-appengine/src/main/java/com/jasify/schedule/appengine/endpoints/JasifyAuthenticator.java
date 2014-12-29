package com.jasify.schedule.appengine.endpoints;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;

import javax.servlet.http.HttpServletRequest;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasifyAuthenticator implements Authenticator {
    @Override
    public User authenticate(HttpServletRequest req) {
        UserSession currentUser = UserContext.getCurrentUser();
        if(currentUser == null) return null;
        return new JasifyUser(currentUser);
    }
}
