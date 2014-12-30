package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author krico
 * @since 27/12/14.
 */
public class JasifyAuthenticator implements Authenticator {
    private static final Logger log = LoggerFactory.getLogger(JasifyAuthenticator.class);

    @Override
    public User authenticate(HttpServletRequest req) {
        log.trace("{}", ServletUtil.debugLazy(req));
        UserSession currentUser = UserContext.getCurrentUser();
        if (currentUser == null) return null;
        return new JasifyUser(currentUser);
    }
}
