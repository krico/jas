package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.ActivityPackage;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackage;
import com.jasify.schedule.appengine.util.KeyUtil;
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

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasActivityPackage
     */
    @Override
    public JasActivityPackage transformTo(ActivityPackage internal) {
        JasActivityPackage external = new JasActivityPackage();
        try {
            external.setActivityCount(internal.getActivityPackageActivityListRef().getModelList().size());
        } catch (Exception e) {
            log.warn("Failed to count activities", e);
        }
        external.setCreated(internal.getCreated());
        external.setCurrency(internal.getCurrency());
        external.setDescription(internal.getDescription());
        external.setExecutionCount(internal.getExecutionCount());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setItemCount(internal.getItemCount());
        external.setMaxExecutions(internal.getMaxExecutions());
        external.setModified(internal.getModified());
        external.setName(internal.getName());
        if (internal.getOrganizationRef().getKey() != null) {
            external.setOrganizationId(keyTransformer.transformTo(internal.getOrganizationRef().getKey()));
        }
        external.setPrice(internal.getPrice());
        external.setValidFrom(internal.getValidFrom());
        external.setValidUntil(internal.getValidUntil());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return ActivityPackage
     */
    @Override
    public ActivityPackage transformFrom(JasActivityPackage external) {
        ActivityPackage internal = new ActivityPackage();
        internal.setCurrency(external.getCurrency());
        internal.setDescription(external.getDescription());
        internal.setExecutionCount(external.getExecutionCount());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setItemCount(external.getItemCount());
        internal.setMaxExecutions(external.getMaxExecutions());
        internal.setName(external.getName());
        if (StringUtils.isNotBlank(external.getOrganizationId())) {
            internal.getOrganizationRef().setKey(keyTransformer.transformFrom(external.getOrganizationId()));
        }
        internal.setPrice(external.getPrice());
        internal.setValidFrom(external.getValidFrom());
        internal.setValidUntil(external.getValidUntil());
        return internal;
    }
}
