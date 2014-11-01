package com.jasify.sandbox.appengine;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by krico on 28/10/14.
 */
public class ListGreetingsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        GuestBook data = new GuestBook();
        if (req.getParameter("guestBook") == null) {
            data.setGuestBook("default");
        } else {
            data.setGuestBook(req.getParameter("guestBook"));
        }


        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key guestbookKey = KeyFactory.createKey("Guestbook", data.getGuestBook());
        Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
        List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(50));
        for (Entity greeting : greetings) {
            Greeting g = new Greeting();
            g.setContent(Objects.toString(greeting.getProperty("content")));
            Object user = greeting.getProperty("user");
            g.setUser(user == null ? "Anonymous" : user.toString());
            g.setDate((Date) greeting.getProperty("date"));
            data.getGreetings().add(g);
        }
        resp.setContentType("application/json");
        Gson gson = new GsonBuilder().create();
        gson.toJson(data, resp.getWriter());
    }
}