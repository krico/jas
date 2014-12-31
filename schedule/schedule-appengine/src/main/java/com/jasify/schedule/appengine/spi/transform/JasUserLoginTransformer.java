package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.appengine.api.datastore.KeyFactory;
import com.jasify.schedule.appengine.model.users.UserLogin;
import com.jasify.schedule.appengine.spi.dm.JasUserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 30/12/14.
 */
public class JasUserLoginTransformer implements Transformer<UserLogin, JasUserLogin> {
    private static final Logger log = LoggerFactory.getLogger(JasUserLoginTransformer.class);

    public JasUserLoginTransformer() {
    }

    /*
    public JasUserLoginTransformer(Type type) {
        log.debug("T: {}", type);
    }
    */

    @Override
    public JasUserLogin transformTo(UserLogin internal) {
        JasUserLogin external = new JasUserLogin();
        external.setEmail(internal.getEmail());
        external.setId(KeyFactory.keyToString(internal.getId()));
        external.setProvider(internal.getProvider());
        return external;
    }

    @Override
    public UserLogin transformFrom(JasUserLogin external) {
        UserLogin internal = new UserLogin();
        internal.setId(KeyFactory.stringToKey(external.getId()));
        internal.setEmail(external.getEmail());
        internal.setProvider(external.getProvider());
        return internal;
    }
}
