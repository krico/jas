package com.jasify.schedule.appengine.http.json;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 25/11/14.
 */
public class JsonPasswordChangeRequest extends JsonObject {
    private Credentials credentials = new Credentials();
    private String newPassword;

    public JsonPasswordChangeRequest() {
    }

    public JsonPasswordChangeRequest(User user, String oldPassword, String newPassword) {
        credentials.setId(Preconditions.checkNotNull(user.getId()).getId());
        credentials.setName(user.getName());
        credentials.setPassword(oldPassword);
        this.newPassword = newPassword;
    }

    public static JsonPasswordChangeRequest parse(String data) {
        return JSON.fromJson(data, JsonPasswordChangeRequest.class);
    }

    public static JsonPasswordChangeRequest parse(Reader reader) {
        return JSON.fromJson(reader, JsonPasswordChangeRequest.class);
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public static class Credentials {
        private long id;
        private String name;
        private String password;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
