package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author krico
 * @since 08/01/15.
 */
@Model
public class Organization {

    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    private String description;
    @Attribute(persistent = false)
    private InverseModelListRef<OrganizationMember, Organization> organizationMemberListRef =
            new InverseModelListRef<>(OrganizationMember.class, OrganizationMemberMeta.get().organizationRef.getName(), this);

    public Organization() {
    }

    public Organization(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InverseModelListRef<OrganizationMember, Organization> getOrganizationMemberListRef() {
        return organizationMemberListRef;
    }

    public List<User> getUsers() {
        List<User> ret = new ArrayList<>();
        List<OrganizationMember> members = organizationMemberListRef.getModelList();
        if (members == null) return ret;

        for (OrganizationMember member : members) {
            User model = member.getUserRef().getModel();
            if (model != null) ret.add(model);
        }

        return ret;
    }

    public List<Group> getGroups() {
        List<Group> ret = new ArrayList<>();
        List<OrganizationMember> members = organizationMemberListRef.getModelList();
        if (members == null) return ret;

        for (OrganizationMember member : members) {
            Group model = member.getGroupRef().getModel();
            if (model != null) ret.add(model);
        }

        return ret;
    }
}
