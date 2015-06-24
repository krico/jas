package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasUserLogin;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author krico
 * @since 30/12/14.
 */
public class JasUserLoginTransformer implements Transformer<UserLogin, JasUserLogin> {

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasOrganization
     */
    @Override
    public JasUserLogin transformTo(UserLogin internal) {
        JasUserLogin external = new JasUserLogin();
        external.setEmail(internal.getEmail());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setProvider(internal.getProvider());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return UserLogin
     */
    @Override
    public UserLogin transformFrom(JasUserLogin external) {
        UserLogin internal = new UserLogin();
        internal.setEmail(external.getEmail());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setProvider(external.getProvider());
        return internal;
    }
}
