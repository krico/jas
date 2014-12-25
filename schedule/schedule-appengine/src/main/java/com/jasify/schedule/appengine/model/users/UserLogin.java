package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * Represents a users' external login like for example oauth2 login with google
 *
 * @author krico
 * @since 23/12/14.
 */
@Model
public class UserLogin {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String provider;

    /**
     * The id that uniquely identifies this login with the provider
     */
    private String userId;

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    public UserLogin() {
    }

    public UserLogin(String provider, String userId) {
        this.provider = provider;
        this.userId = userId;
    }

    public UserLogin(User owner) {
        this.id = Datastore.allocateId(Preconditions.checkNotNull(owner.getId(), "Owner user must have id"), UserDetail.class);
        this.userRef.setModel(owner);
    }

    public UserLogin(Key id) {
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

    public ModelRef<User> getUserRef() {
        return userRef;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserLogin)) return false;

        UserLogin userLogin = (UserLogin) o;

        if (created != null ? !created.equals(userLogin.created) : userLogin.created != null) return false;
        if (userId != null ? !userId.equals(userLogin.userId) : userLogin.userId != null)
            return false;
        if (id != null ? !id.equals(userLogin.id) : userLogin.id != null) return false;
        if (modified != null ? !modified.equals(userLogin.modified) : userLogin.modified != null) return false;
        if (provider != null ? !provider.equals(userLogin.provider) : userLogin.provider != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", provider='" + provider + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
