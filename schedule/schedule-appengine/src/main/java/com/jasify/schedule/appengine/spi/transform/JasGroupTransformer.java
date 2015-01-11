package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.spi.dm.JasGroup;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 11/01/15.
 */
public class JasGroupTransformer implements Transformer<Group, JasGroup> {
    public JasGroupTransformer() {
    }

    @Override
    public JasGroup transformTo(Group internal) {
        JasGroup external = new JasGroup();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public Group transformFrom(JasGroup external) {
        Group internal = new Group();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
