package com.jasify.schedule.appengine.http.filter;

import com.jasify.schedule.appengine.http.HttpUserSession;
import com.jasify.schedule.appengine.model.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author krico
 * @since 10/11/14.
 */
public class UserContextFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(UserContextFilter.class);
    private String filterName;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        filterName = filterConfig.getFilterName();
        log.info("UserSessionFilter.init name={}", filterName);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserContext.setContext(HttpUserSession.get(request), request, response);
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clearContext();
        }
    }

    @Override
    public void destroy() {
        log.info("UserSessionFilter.destroy name={}", filterName);
    }
}
