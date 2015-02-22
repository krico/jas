package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.common.Organization;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

/**
 * @author krico
 * @since 19/02/15.
 */
@Model
public class OrganizationAccount extends Account {
    private ModelRef<Organization> organizationRef = new ModelRef<>(Organization.class);

    public OrganizationAccount() {
    }

    public OrganizationAccount(Key id) {
        super(id);
    }

    public ModelRef<Organization> getOrganizationRef() {
        return organizationRef;
    }
}
