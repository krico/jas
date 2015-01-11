package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.dm.JasOrganization;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasOrganizationTransformer implements Transformer<Organization, JasOrganization> {
    public JasOrganizationTransformer() {
    }

    @Override
    public JasOrganization transformTo(Organization internal) {
        JasOrganization external = new JasOrganization();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public Organization transformFrom(JasOrganization external) {
        Organization internal = new Organization();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
