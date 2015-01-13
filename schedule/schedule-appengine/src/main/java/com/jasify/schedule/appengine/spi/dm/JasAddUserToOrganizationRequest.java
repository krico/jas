package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;

/**
 * @author krico
 * @since 13/01/15.
 */
public class JasAddUserToOrganizationRequest {
    private Organization organization;
    private User user;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
