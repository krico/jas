package com.jasify.schedule.appengine.model.common;

/**
 * @author krico
 * @since 08/01/15.
 */
public class OrganizationServiceFactory {
    private static OrganizationService instance;

    protected OrganizationServiceFactory() {
    }

    public static OrganizationService getOrganizationService() {
        if (instance == null)
            return DefaultOrganizationService.instance();
        return instance;
    }

    protected static void setInstance(OrganizationService instance) {
        OrganizationServiceFactory.instance = instance;
    }

}
