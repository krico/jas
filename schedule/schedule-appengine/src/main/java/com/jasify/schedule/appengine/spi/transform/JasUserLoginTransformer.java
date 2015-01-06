package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasUserLogin;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 30/12/14.
 */
public class JasUserLoginTransformer implements Transformer<UserLogin, JasUserLogin> {

    public JasUserLoginTransformer() {
    }

    @Override
    public JasUserLogin transformTo(UserLogin internal) {
        JasUserLogin external = new JasUserLogin();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public UserLogin transformFrom(JasUserLogin external) {
        UserLogin internal = new UserLogin();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
