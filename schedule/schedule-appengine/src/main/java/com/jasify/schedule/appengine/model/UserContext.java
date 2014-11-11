package com.jasify.schedule.appengine.model;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author krico
 * @since 11/11/14.
 */
public final class UserContext {
    private static final ThreadLocal<Context> CURRENT_USER = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        }
    };

    private UserContext() {
    }

    public static void setContext(UserSession userSession, ServletRequest request, ServletResponse response) {
        Context context = CURRENT_USER.get();
        context.userSession = userSession;
        context.request = request;
        context.response = response;
    }

    public static void clearContext() {
        CURRENT_USER.get().clear();
    }

    public static UserSession getCurrentUser() {
        return CURRENT_USER.get().userSession;
    }

    public static void setCurrentUser(UserSession userSession) {
        CURRENT_USER.get().userSession = userSession;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServletRequest> T getCurrentRequest() {
        return (T) CURRENT_USER.get().request;
    }

    @SuppressWarnings("unchecked")
    public static <T extends ServletResponse> T getCurrentResponse() {
        return (T) CURRENT_USER.get().response;
    }

    private static class Context {
        private UserSession userSession;
        private ServletRequest request;
        private ServletResponse response;

        void clear() {
            userSession = null;
            request = null;
            response = null;
        }
    }

}
