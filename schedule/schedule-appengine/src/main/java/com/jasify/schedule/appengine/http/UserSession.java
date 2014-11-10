package com.jasify.schedule.appengine.http;

import com.google.appengine.api.datastore.Key;
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
public class UserSession implements HttpSessionBindingListener {
    static final String SESSION_KEY = "jus" /* jasify user session s*/;
    private static final Logger log = LoggerFactory.getLogger(UserSession.class);
    private static final ThreadLocal<UserSession> CURRENT = new ThreadLocal<>();
    private final Key userId;

    public UserSession(User user) {
        this.userId = user.getId();
    }

    public static void setCurrent(ServletRequest req) {
        UserSession current = null;
        if (req instanceof HttpServletRequest)
            current = get((HttpServletRequest) req);
        CURRENT.set(current);
    }

    public static void clearCurrent() {
        CURRENT.set(null);
    }

    public static UserSession get(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (UserSession) session.getAttribute(SESSION_KEY);
    }

    public UserSession put(HttpServletRequest req) {
        HttpSession session = req.getSession(true);
        session.setAttribute(UserSession.SESSION_KEY, this);
        return this;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        log.debug("User[{}] bound to session: {}", userId, event);
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        log.debug("User[{}] unbound from session: {}", userId, event);
        //TODO: simulate logout
    }

    public void delete(HttpServletRequest req) {
        req.getSession().removeAttribute(SESSION_KEY);
    }
}
