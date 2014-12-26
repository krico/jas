package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.JSON;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.util.Date;

/**
 * @author krico
 * @since 11/11/14.
 */
public class JsonUser extends JsonObject {
    private long id;
    private Date created;
    private Date modified;
    private String name;
    private String realName;
    private String email;
    private String about;
    private boolean admin;

    public JsonUser() {
    }

    public JsonUser(User user) {
        id = user.getId().getId();
        created = user.getCreated();
        modified = user.getModified();
        name = user.getName();
        realName = user.getRealName();
        email = user.getEmail();
        about = user.getAbout();
        admin = user.isAdmin();
    }

    public static JsonUser parse(String data) {
        return JSON.fromJson(data, JsonUser.class);
    }

    public static JsonUser parse(Reader reader) {
        return JSON.fromJson(reader, JsonUser.class);
    }

    public User writeTo(User user) {
        /* we only set fields that the user is allowed to modify  */
        user.setEmail(email);
        user.setRealName(realName);
        user.setAbout(about);
        return user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
