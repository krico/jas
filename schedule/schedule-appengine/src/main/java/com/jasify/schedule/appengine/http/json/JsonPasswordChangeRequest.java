package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.util.JSON;

import java.io.Reader;

/**
 * @author krico
 * @since 25/11/14.
 */
public class JsonPasswordChangeRequest extends JsonObject {
    private String oldPassword;
    private String newPassword;

    public JsonPasswordChangeRequest() {
    }

    public JsonPasswordChangeRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public static JsonPasswordChangeRequest parse(String data) {
        return JSON.fromJson(data, JsonPasswordChangeRequest.class);
    }

    public static JsonPasswordChangeRequest parse(Reader reader) {
        return JSON.fromJson(reader, JsonPasswordChangeRequest.class);
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
