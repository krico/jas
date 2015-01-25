package com.jasify.schedule.appengine.model.common;

import org.easymock.EasyMock;

/**
 * @author krico
 * @since 08/01/15.
 */
public class TestOrganizationServiceFactory extends OrganizationServiceFactory {
    private OrganizationService organizationServiceMock;

    public void setUp() {
        organizationServiceMock = EasyMock.createMock(OrganizationService.class);
        setInstance(organizationServiceMock);
    }

    public void tearDown() {
        tearDown(true);
    }

    public void tearDown(boolean doVerify) {
        setInstance(null);
        if (doVerify) EasyMock.verify(organizationServiceMock);
        organizationServiceMock = null;
    }

    public OrganizationService getOrganizationServiceMock() {
        return organizationServiceMock;
    }

    public void replay() {
        EasyMock.replay(organizationServiceMock);
    }
}
