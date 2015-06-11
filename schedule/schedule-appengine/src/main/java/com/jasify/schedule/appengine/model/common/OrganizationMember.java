package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.model.users.User;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * Junction Organization to (User or Group)
 *
 * @author krico
 * @since 08/01/15.
 */
@Model
public class OrganizationMember implements HasId {
    @Attribute(primaryKey = true)
    private Key id;

    private ModelRef<Organization> organizationRef = new ModelRef<>(Organization.class);

    private ModelRef<User> userRef = new ModelRef<>(User.class);

    private ModelRef<Group> groupRef = new ModelRef<>(Group.class);

    public OrganizationMember() {
    }

    public OrganizationMember(Organization o, User u) {
        organizationRef.setKey(o.getId());
        userRef.setKey(u.getId());
    }

    public OrganizationMember(Organization o, Group g) {
        organizationRef.setKey(o.getId());
        groupRef.setKey(g.getId());
    }

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public ModelRef<Organization> getOrganizationRef() {
        return organizationRef;
    }

    public ModelRef<User> getUserRef() {
        return userRef;
    }

    public ModelRef<Group> getGroupRef() {
        return groupRef;
    }
}
