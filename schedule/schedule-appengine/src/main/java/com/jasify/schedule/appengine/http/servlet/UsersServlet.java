package com.jasify.schedule.appengine.http.servlet;

import com.google.appengine.api.datastore.Query;
import com.jasify.schedule.appengine.http.json.JsonUser;
import com.jasify.schedule.appengine.model.UserContext;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 23/11/14.
 */
public class UsersServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UsersServlet.class);

    private final Pattern PAGE_PATTERN = Pattern.compile("/page/([0-9]+)/?");
    private final Pattern SIZE_PATTERN = Pattern.compile("/size/([0-9]+)/?");
    private final Pattern SORT_PATTERN = Pattern.compile("(?i)/sort/(asc|desc)/?");


    /* URL example: /users/page/1/size/10/sort/DESC?field=email&query=user */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());

        resp.setContentType(JSON.CONTENT_TYPE);

        if (!UserContext.isCurrentUserAdmin()) {

            log.info("Unauthorized GET access to {}", pathInfo);
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }
        int page = 1;
        int size = 20;
        Query.SortDirection order = Query.SortDirection.ASCENDING;

        Matcher m = PAGE_PATTERN.matcher(pathInfo);
        if (m.find()) page = Integer.parseInt(m.group(1));

        m = SIZE_PATTERN.matcher(pathInfo);
        if (m.find()) size = Integer.parseInt(m.group(1));

        m = SORT_PATTERN.matcher(pathInfo);
        if (m.find())
            order = StringUtils.equalsIgnoreCase("desc", m.group(1)) ? Query.SortDirection.DESCENDING : Query.SortDirection.ASCENDING;

        int offset = (page - 1) * size;

        String field = req.getParameter("field");

        List<User> list;
        String query = StringUtils.trimToNull(req.getParameter("query"));
        if ("email".equals(field)) {
            list = UserServiceFactory.getUserService().searchByEmail(query == null ? null : Pattern.compile(query), order, offset, size);
        } else if ("name".equals(field)) {
            list = UserServiceFactory.getUserService().searchByName(query == null ? null : Pattern.compile(query), order, offset, size);
        } else {
            list = UserServiceFactory.getUserService().list(order, offset, size);
        }
        ArrayList<JsonUser> ret = new ArrayList<>();
        for (User user : list) {
            ret.add(new JsonUser(user));
        }
        JSON.toJson(resp.getWriter(), ret);
    }
}
