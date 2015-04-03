package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 04/01/15.
 */
public class JasKeyTransformer implements Transformer<Key, String> {

    @Override
    public String transformTo(Key internal) {
        return KeyUtil.keyToString(internal);
    }

    @Override
    public Key transformFrom(String external) {
        return KeyUtil.stringToKey(external);
    }
}
