package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackage;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 03/05/15.
 */
public class JasActivityPackageTransformer implements Transformer<ActivityPackage, JasActivityPackage> {
    private static final Logger log = LoggerFactory.getLogger(JasActivityPackageTransformer.class);

    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasActivityPackage transformTo(ActivityPackage internal) {
        JasActivityPackage external = new JasActivityPackage();
        BeanUtil.copyProperties(external, internal);
        if (internal.getOrganizationRef().getKey() != null) {
            external.setOrganizationId(keyTransformer.transformTo(internal.getOrganizationRef().getKey()));
        }
        try {
            external.setActivityCount(internal.getActivityPackageActivityListRef().getModelList().size());
        } catch (Exception e) {
            log.warn("Failed to count activities", e);
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
