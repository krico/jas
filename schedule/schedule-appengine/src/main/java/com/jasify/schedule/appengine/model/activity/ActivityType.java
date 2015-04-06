package com.jasify.schedule.appengine.model.activity;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.Constants;
import com.jasify.schedule.appengine.model.LowerCaseListener;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.*;

import java.util.Date;

/**
 * @author krico
 * @since 07/01/15.
 */
@Model(schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 1)
public class ActivityType {
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

    private ModelRef<Organization> organizationRef = new ModelRef<>(Organization.class);

    public ActivityType() {
    }

    public ActivityType(String name) {
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

    public ModelRef<Organization> getOrganizationRef() {
        return organizationRef;
    }
}
