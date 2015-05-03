package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackage;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author krico
 * @since 03/05/15.
 */
public class JasActivityPackageTransformer implements Transformer<ActivityPackage, JasActivityPackage> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasActivityPackage transformTo(ActivityPackage internal) {
        JasActivityPackage external = new JasActivityPackage();
        BeanUtil.copyProperties(external, internal);
        if (internal.getOrganizationRef().getKey() != null) {
            external.setOrganizationId(keyTransformer.transformTo(internal.getOrganizationRef().getKey()));
        }
        return external;

    }

    @Override
    public ActivityPackage transformFrom(JasActivityPackage external) {
        ActivityPackage internal = new ActivityPackage();
        BeanUtil.copyProperties(internal, external);
        if (StringUtils.isNotBlank(external.getOrganizationId())) {
            internal.getOrganizationRef().setKey(keyTransformer.transformFrom(external.getOrganizationId()));
        }
        return internal;
    }
}
