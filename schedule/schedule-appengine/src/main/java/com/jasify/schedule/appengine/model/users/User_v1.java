package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * This is a temporary class for migration purposes from User schemaVersion 1 -> 2
 *
 * @author krico
 * @since 08/11/14.
 */
@Model(kind = "User" /* so we get the same entity */, schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 1)
public class User_v1 {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    /* If the user wants his username like BigTom we keep it here with the case */
    private String nameWithCase;

    private String email;

    private ShortBlob password;

    private boolean admin = false;

    private ModelRef<UserDetail> detailRef = new ModelRef<>(UserDetail.class);

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public ShortBlob getPassword() {
        return password;
    }

    public void setPassword(ShortBlob password) {
        this.password = password;
    }

    public String getAbout() {
        UserDetail userDetail = getDetailRef().getModel();
        if (userDetail == null) return null;
        return TypeUtil.toString(userDetail.getAbout());
    }

    public void setAbout(String about) {
        Text text = TypeUtil.toText(StringUtils.trimToNull(about));
        UserDetail userDetail = getDetailRef().getModel();
        if (userDetail == null) {

            if (text == null) return; // no need to create detail to set text to null

            userDetail = new UserDetail(Datastore.allocateId(Preconditions.checkNotNull(this.getId(), "Owner user must have id"), UserDetail.class));
            getDetailRef().setModel(userDetail);
        }
        userDetail.setAbout(text);
        //TODO: test
        //TODO: We need to save this
    }

    public ModelRef<UserDetail> getDetailRef() {
        return detailRef;
    }

    public String debugString() {
        return "User{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", admin=" + admin +
                ", password=" + password +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User_v1 user = (User_v1) o;

        if (admin != user.admin) return false;
        if (created != null ? !created.equals(user.created) : user.created != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (modified != null ? !modified.equals(user.modified) : user.modified != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (nameWithCase != null ? !nameWithCase.equals(user.nameWithCase) : user.nameWithCase != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nameWithCase != null ? nameWithCase.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (admin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name=" + name +
                '}';
    }
}
