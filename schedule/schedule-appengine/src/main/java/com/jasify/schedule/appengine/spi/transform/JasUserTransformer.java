package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.spi.dm.JasUser;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author krico
 * @since 04/01/15.
 */
public class JasUserTransformer implements Transformer<User, JasUser> {

    public JasUserTransformer() {
    }

    @Override
    public JasUser transformTo(User internal) {
        JasUser external = new JasUser();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public User transformFrom(JasUser external) {
        User internal = new User();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
