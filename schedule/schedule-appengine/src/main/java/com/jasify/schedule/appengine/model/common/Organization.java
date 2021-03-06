package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;
import com.jasify.schedule.appengine.model.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.*;

import java.util.*;

/**
 * @author krico
 * @since 08/01/15.
 */
@Model
public class Organization implements HasId {
    private static final Logger log = LoggerFactory.getLogger(Organization.class);

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

    private Set<PaymentTypeEnum> paymentTypes = new HashSet<>();

    @Attribute(persistent = false)
    private InverseModelListRef<OrganizationMember, Organization> organizationMemberListRef =
            new InverseModelListRef<>(OrganizationMember.class, OrganizationMemberMeta.get().organizationRef.getName(), this);


    public Organization() {
    }

    public Organization(String name) {
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
        setLcName(name);
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

    public InverseModelListRef<OrganizationMember, Organization> getOrganizationMemberListRef() {
        return organizationMemberListRef;
    }

    /**
     * @return the users
     * @deprecated You should use
     * {@link com.jasify.schedule.appengine.dao.common.OrganizationDao#getUsersOfOrganization(com.google.appengine.api.datastore.Key)}
     */
    @Deprecated
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

    public Set<Key> getUserKeys() {
        Set<Key> ret = new HashSet<>();
        List<OrganizationMember> members = organizationMemberListRef.getModelList();
        if (members == null) return ret;

        for (OrganizationMember member : members) {
            Key key = member.getUserRef().getKey();
            if (key != null) ret.add(key);
        }

        return ret;
    }

    public List<Group> getGroups() {
        List<Group> ret = new ArrayList<>();
        List<OrganizationMember> members = organizationMemberListRef.getModelList();
        if (members == null) return ret;

        for (OrganizationMember member : members) {
            Group model = null;
            try {
                model = member.getGroupRef().getModel();
            } catch (Exception e) {
                log.warn("Missing group", e);
            }
            if (model != null) ret.add(model);
        }

        return ret;
    }

    public Set<Key> getGroupKeys() {
        Set<Key> ret = new HashSet<>();
        List<OrganizationMember> members = organizationMemberListRef.getModelList();
        if (members == null) return ret;

        for (OrganizationMember member : members) {
            Key key = member.getGroupRef().getKey();
            if (key != null) ret.add(key);
        }

        return ret;
    }

    public Set<PaymentTypeEnum> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(Set<PaymentTypeEnum> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
