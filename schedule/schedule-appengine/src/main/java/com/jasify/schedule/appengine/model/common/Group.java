package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.common.GroupUserMeta;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.*;

import java.util.*;

/**
 * @author krico
 * @since 08/01/15.
 */
@Model
public class Group {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    @Attribute(listener = LowerCaseListener.class)
    private String lcName;

    private String description;

    @Attribute(persistent = false)
    private InverseModelListRef<GroupUser, Group> groupUserListRef =
            new InverseModelListRef<>(GroupUser.class, GroupUserMeta.get().groupRef.getName(), this);

    public Group() {
    }

    public Group(String name) {
        setName(name);
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
        this.lcName = name;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InverseModelListRef<GroupUser, Group> getGroupUserListRef() {
        return groupUserListRef;
    }

    public List<User> getUsers() {
        List<User> ret = new ArrayList<>();
        List<GroupUser> list = groupUserListRef.getModelList();
        if (list == null) return ret;
        for (GroupUser groupUser : list) {
            User user = groupUser.getUserRef().getModel();
            if (user != null) ret.add(user);
        }
        return ret;
    }

    public Set<Key> getUserKeys() {
        Set<Key> ret = new HashSet<>();
        List<GroupUser> list = groupUserListRef.getModelList();
        if (list == null) return ret;
        for (GroupUser groupUser : list) {
            Key key = groupUser.getUserRef().getKey();
            if (key != null) ret.add(key);
        }
        return ret;
    }
}
