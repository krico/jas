package com.jasify.schedule.appengine.users;

import com.google.appengine.api.datastore.*;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by krico on 08/11/14.
 */
@Model
public class User {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    private Email email;

    private Text about;

    private ShortBlob password;

    private Set<Category> permissions;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Text getAbout() {
        return about;
    }

    public void setAbout(Text about) {
        this.about = about;
    }

    public ShortBlob getPassword() {
        return password;
    }

    public void setPassword(ShortBlob password) {
        this.password = password;
    }

    public Set<Category> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Category> permissions) {
        this.permissions = permissions;
    }

    public boolean addPermission(Category newPermission) {
        if (permissions == null) {
            permissions = new HashSet<>(1);
        }
        return permissions.add(newPermission);
    }

    public boolean hasPermission(Category check) {
        return permissions != null && permissions.contains(check);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name=" + name +
                '}';
    }
}
