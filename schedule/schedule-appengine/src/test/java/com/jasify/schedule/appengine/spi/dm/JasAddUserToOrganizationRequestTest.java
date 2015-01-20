package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by wszarmach on 19/01/15.
 */
public class JasAddUserToOrganizationRequestTest {

    @Test
    public void testOrganization() {
        JasAddUserToOrganizationRequest jasAddUserToOrganizationRequest = new JasAddUserToOrganizationRequest();
        Organization organization = new Organization();
        jasAddUserToOrganizationRequest.setOrganization(organization);
        assertEquals(organization, jasAddUserToOrganizationRequest.getOrganization());
    }

    @Test
    public void testUser() {
        JasAddUserToOrganizationRequest jasAddUserToOrganizationRequest = new JasAddUserToOrganizationRequest();
        User user = new User();
        jasAddUserToOrganizationRequest.setUser(user);
        assertEquals(user, jasAddUserToOrganizationRequest.getUser());
    }
}
