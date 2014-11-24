package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.Constants;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 23/11/14.
 */
@Model(schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 0)
public class UserDetail {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private Text about;

    public UserDetail() {
    }

    public UserDetail(User owner) {
        this.id = Datastore.allocateId(Preconditions.checkNotNull(owner.getId(), "Owner user must have id"), UserDetail.class);
    }

    public UserDetail(Key id) {
        this.id = id;
    }

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

    public Text getAbout() {
        return about;
    }

    public void setAbout(Text about) {
        this.about = about;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetail that = (UserDetail) o;

        if (about != null ? !about.equals(that.about) : that.about != null) return false;
        if (created != null ? !created.equals(that.created) : that.created != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (about != null ? about.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "id='" + id + '\'' +
                '}';
    }

}
