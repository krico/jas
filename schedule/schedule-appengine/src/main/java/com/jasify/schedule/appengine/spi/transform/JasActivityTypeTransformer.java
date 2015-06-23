package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.spi.dm.JasActivityType;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 07/01/15.
 */
public class JasActivityTypeTransformer implements Transformer<ActivityType, JasActivityType> {

    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasActivityType
     */
    @Override
    public JasActivityType transformTo(ActivityType internal) {
        JasActivityType external = new JasActivityType();
        external.setColourTag(internal.getColourTag());
        external.setCurrency(internal.getCurrency());
        external.setDescription(internal.getDescription());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setLocation(internal.getLocation());
        external.setMaxSubscriptions(internal.getMaxSubscriptions());
        external.setName(internal.getName());
        external.setPrice(internal.getPrice());
        if (internal.getOrganizationRef().getKey() != null) {
            external.setOrganizationId(keyTransformer.transformTo(internal.getOrganizationRef().getKey()));
        }
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return ActivityType
     */
    @Override
    public ActivityType transformFrom(JasActivityType external) {
        ActivityType internal = new ActivityType();
        internal.setColourTag(external.getColourTag());
        internal.setCurrency(external.getCurrency());
        internal.setDescription(external.getDescription());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setLocation(external.getLocation());
        internal.setMaxSubscriptions(external.getMaxSubscriptions());
        internal.setName(external.getName());
        internal.getOrganizationRef().setKey(KeyUtil.stringToKey(external.getOrganizationId()));
        internal.setPrice(external.getPrice());
        return internal;
    }
}
