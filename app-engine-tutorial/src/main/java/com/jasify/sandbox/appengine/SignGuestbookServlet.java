package com.jasify.sandbox.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by krico on 10/26/14.
 */
public class SignGuestbookServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String guestbookName = req.getParameter("guestbookName");
        String content;
        Gson gson = null;
        if (guestbookName == null) {//Let's hope it's a json request now :-)
            gson = new GsonBuilder().create();
            String jsonData = IOUtils.toString(req.getInputStream());
            Map map = gson.fromJson(jsonData, Map.class);
            guestbookName = Objects.toString(map.get("guestbookName"));
            content = Objects.toString(map.get("content"));
        } else {
            content = req.getParameter("content");
        }
        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
        Date date = new Date();
        Entity greeting = new Entity("Greeting", guestbookKey);
        greeting.setProperty("user", user);
        greeting.setProperty("date", date);
        greeting.setProperty("content", content);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(greeting);
        if (gson != null) {
            Greeting g = new Greeting();
            g.setUser(user == null ? "Anonymous" : user.toString());
            g.setContent(content);
            g.setDate(date);
            resp.setContentType("application/json");
            gson.toJson(g, resp.getWriter());
        } else {
            resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
        }
    }
}