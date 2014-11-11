package com.jasify.schedule.appengine.http.json;

import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.JSON;
import com.jasify.schedule.appengine.util.TypeUtil;

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
    private String email;
    private String about;

    public JsonUser() {
    }

    public JsonUser(User user) {
        id = user.getId().getId();
        created = user.getCreated();
        modified = user.getModified();
        name = user.getNameWithCase(); /* This is what the user wants to see */
        email = TypeUtil.toString(user.getEmail());
        about = TypeUtil.toString(user.getAbout());
    }


    public static JsonUser parse(String data) {
        return JSON.fromJson(data, JsonUser.class);
    }

    public static JsonUser parse(Reader reader) {
        return JSON.fromJson(reader, JsonUser.class);
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
}
