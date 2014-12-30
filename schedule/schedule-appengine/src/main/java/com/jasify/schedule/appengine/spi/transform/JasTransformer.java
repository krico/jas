package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;

/**
 * @author krico
 * @since 30/12/14.
 */
public class JasTransformer<TFrom, TTo> implements Transformer<TFrom, TTo> {
    @Override
    public TTo transformTo(TFrom tFrom) {
        return null;
    }

    @Override
    public TFrom transformFrom(TTo tTo) {
        return null;
    }
}
