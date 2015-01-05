package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 04/01/15.
 */
public class JasKeyTransformer implements Transformer<Key, String> {
    private static final Logger log = LoggerFactory.getLogger(JasKeyTransformer.class);

    @Override
    public String transformTo(Key internal) {
        if (internal == null) return null;
        return KeyFactory.keyToString(internal);
    }

    @Override
    public Key transformFrom(String external) {
        if (external == null) return null;
        try {
            return KeyFactory.stringToKey(external);
        } catch (Exception e) {
            log.debug("Failed to parse key: {}", external, e);
            return null;
        }
    }
}
