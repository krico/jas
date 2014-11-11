package com.jasify.schedule.appengine.http;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.UserSession;
import com.jasify.schedule.appengine.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * @author krico
 * @since 10/11/14.
 */
public class HttpUserSession implements UserSession, HttpSessionBindingListener {
    static final String SESSION_KEY = "jus" /* jasify user session s*/;
    private static final Logger log = LoggerFactory.getLogger(HttpUserSession.class);
    private final Key userId;

    public HttpUserSession(User user) {
        this.userId = user.getId();
    }

    public static UserSession get(ServletRequest req) {
        if (req instanceof HttpServletRequest) {
            return get((HttpServletRequest) req);
        } else {
            return null;
        }
    }

    public static HttpUserSession get(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (HttpUserSession) session.getAttribute(SESSION_KEY);
    }

    public HttpUserSession put(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        session.setAttribute(HttpUserSession.SESSION_KEY, this);
        return this;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        log.debug("User[{}] bound to session: {}", userId, event);
        UserContext.setCurrentUser(this);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        log.debug("User[{}] unbound from session: {}", userId, event);
        UserContext.setCurrentUser(null);
    }

    @Override
    public void invalidate() {
        delete(UserContext.<HttpServletRequest>getCurrentRequest());
    }

    public void delete(HttpServletRequest req) {
        if (req != null) {
            req.getSession().removeAttribute(SESSION_KEY);
        }
    }
}
