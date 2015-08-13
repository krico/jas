package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.spi.dm.JasHistory;

/**
 * @author krico
 * @since 10/08/15.
 */
public class JasHistoryTransformer implements Transformer<History, JasHistory> {
    private final JasKeyTransformer keyTransformer = new JasKeyTransformer();

    @Override
    public JasHistory transformTo(History internal) {
        JasHistory external = new JasHistory();
        external.setId(keyTransformer.transformTo(internal.getId()));
        external.setCreated(internal.getCreated());
        external.setType(internal.getType());
        external.setDescription(internal.getDescription());
        external.setCurrentUserId(keyTransformer.transformTo(internal.getCurrentUserRef().getKey()));
        return external;
    }

    @Override
    public History transformFrom(JasHistory external) {
        throw new UnsupportedOperationException("History is immutable");
    }
}
