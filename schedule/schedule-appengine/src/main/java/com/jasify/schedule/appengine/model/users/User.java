package com.jasify.schedule.appengine.model.users;

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

    /* If the user wants his username like BigTom we keep it here with the case */
    private String nameWithCase;

    private Email email;

    private Text about;

    private ShortBlob password;

    private Set<Category> permissions = new HashSet<>();

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

    public String getNameWithCase() {
        return nameWithCase;
    }

    public void setNameWithCase(String nameWithCase) {
        this.nameWithCase = nameWithCase;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (about != null ? !about.equals(user.about) : user.about != null) return false;
        if (created != null ? !created.equals(user.created) : user.created != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (modified != null ? !modified.equals(user.modified) : user.modified != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (nameWithCase != null ? !nameWithCase.equals(user.nameWithCase) : user.nameWithCase != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (permissions != null ? !permissions.equals(user.permissions) : user.permissions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (permissions != null ? permissions.hashCode() : 0);
        return result;
    }

    public String debugString() {
        return "User{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", about=" + about +
                ", password=" + password +
                ", permissions=" + permissions +
                '}';
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name=" + name +
                '}';
    }
}
