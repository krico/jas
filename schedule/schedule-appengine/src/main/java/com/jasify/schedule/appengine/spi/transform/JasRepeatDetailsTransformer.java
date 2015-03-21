package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.spi.dm.JasRepeatDetails;
import com.jasify.schedule.appengine.util.BeanUtil;

/**
 * @author wszarmach
 * @since 14/03/15.
 */
public class JasRepeatDetailsTransformer implements Transformer<RepeatDetails, JasRepeatDetails> {
    public JasRepeatDetailsTransformer() {
    }

    @Override
    public JasRepeatDetails transformTo(RepeatDetails internal) {
        JasRepeatDetails external = new JasRepeatDetails();
        BeanUtil.copyProperties(external, internal);
        return external;
    }

    @Override
    public RepeatDetails transformFrom(JasRepeatDetails external) {
        RepeatDetails internal = new RepeatDetails();
        BeanUtil.copyProperties(internal, external);
        return internal;
    }
}
