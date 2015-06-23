package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.spi.dm.JasGroup;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasGroupTransformer implements Transformer<Group, JasGroup> {

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasGroup
     */
    @Override
    public JasGroup transformTo(Group internal) {
        JasGroup external = new JasGroup();
        external.setCreated(internal.getCreated());
        external.setDescription(internal.getDescription());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setName(internal.getName());
        external.setModified(internal.getModified());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return Group
     */
    @Override
    public Group transformFrom(JasGroup external) {
        Group internal = new Group();
        internal.setDescription(external.getDescription());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setName(external.getName());
        return internal;
    }
}
