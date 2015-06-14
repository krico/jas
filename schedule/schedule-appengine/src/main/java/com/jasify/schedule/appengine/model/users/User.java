package com.jasify.schedule.appengine.model.users;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 08/11/14.
 */
@Model(schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 2)
public class User implements HasId {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    @Attribute(listener = LowerCaseListener.class)
    private String name;

    private String realName;

    @Attribute(listener = LowerCaseListener.class)
    private String email;

    private boolean emailVerified;

    private ShortBlob password;

    private boolean admin = false;

    private String locale;

    private ModelRef<UserDetail> detailRef = new ModelRef<>(UserDetail.class);

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, String email, String realName) {
        setName(name);
        setEmail(email);
        setRealName(realName);
    }

    public User(UserLogin userLogin) {
        readFrom(userLogin);
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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

            userDetail = new UserDetail(this);
            getDetailRef().setModel(userDetail);
        }
        userDetail.setAbout(text);
        //TODO: test
        //TODO: We need to save this
    }


    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public ModelRef<UserDetail> getDetailRef() {
        return detailRef;
    }

    public String getDisplayName() {
        if (getRealName() != null) {
            return getRealName();
        }
        return getName();
    }

    private void readFrom(UserLogin userLogin) {
        setName(userLogin.getEmail());
        setEmail(userLogin.getEmail());
        setRealName(userLogin.getRealName());
    }

    public String debugString() {
        return "User{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", name='" + name + '\'' +
                ", realName='" + realName + '\'' +
                ", email=" + email +
                ", emailVerified=" + emailVerified +
                ", admin=" + admin +
                ", locale=" + locale +
                ", password=" + password +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (admin != user.admin) return false;
        if (emailVerified != user.emailVerified) return false;
        if (created != null ? !created.equals(user.created) : user.created != null) return false;
        //TODO: bug reported https://github.com/Slim3/slim3/issues/19
        // if (detailRef != null ? !detailRef.equals(user.detailRef) : user.detailRef != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (modified != null ? !modified.equals(user.modified) : user.modified != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (locale != null ? !locale.equals(user.locale) : user.locale != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (realName != null ? !realName.equals(user.realName) : user.realName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (realName != null ? realName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (emailVerified ? 1 : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (admin ? 1 : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        result = 31 * result + (detailRef != null ? detailRef.hashCode() : 0);
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
