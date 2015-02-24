package com.jasify.schedule.appengine.model.common;

import com.jasify.schedule.appengine.TestService;
import org.easymock.EasyMock;

/**
 * @author krico
 * @since 08/01/15.
 */
public class TestOrganizationServiceFactory extends OrganizationServiceFactory implements TestService {
    private OrganizationService organizationServiceMock;

    public void setUp() {
        organizationServiceMock = EasyMock.createMock(OrganizationService.class);
        setInstance(organizationServiceMock);
    }

    public void tearDown() {
        setInstance(null);
        EasyMock.verify(organizationServiceMock);
        organizationServiceMock = null;
    }

    public OrganizationService getOrganizationServiceMock() {
        return organizationServiceMock;
    }

    public void replay() {
        EasyMock.replay(organizationServiceMock);
    }
}