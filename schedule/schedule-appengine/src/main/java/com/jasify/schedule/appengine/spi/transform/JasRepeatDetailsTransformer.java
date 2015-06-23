package com.jasify.schedule.appengine.spi.transform;

import com.google.api.server.spi.config.Transformer;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import com.jasify.schedule.appengine.spi.dm.JasRepeatDetails;
import com.jasify.schedule.appengine.util.KeyUtil;

/**
 * @author wszarmach
 * @since 14/03/15.
 */
public class JasRepeatDetailsTransformer implements Transformer<RepeatDetails, JasRepeatDetails> {

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param internal is the object we are copying from
     * @return JasRepeatDetails
     */
    @Override
    public JasRepeatDetails transformTo(RepeatDetails internal) {
        JasRepeatDetails external = new JasRepeatDetails();
        external.setFridayEnabled(internal.isFridayEnabled());
        external.setId(KeyUtil.keyToString(internal.getId()));
        external.setMondayEnabled(internal.isMondayEnabled());
        external.setRepeatEvery(internal.getRepeatEvery());
        external.setRepeatType(internal.getRepeatType());
        external.setRepeatUntilType(internal.getRepeatUntilType());
        external.setSaturdayEnabled(internal.isSaturdayEnabled());
        external.setSundayEnabled(internal.isSundayEnabled());
        external.setThursdayEnabled(internal.isThursdayEnabled());
        external.setTuesdayEnabled(internal.isTuesdayEnabled());
        external.setUntilCount(internal.getUntilCount());
        external.setUntilDate(internal.getUntilDate());
        external.setWednesdayEnabled(internal.isWednesdayEnabled());
        return external;
    }

    /**
     * Copy all the relevant properties. Note that every time a new property is added we must update this method
     *
     * @param external is the object we are copying from
     * @return RepeatDetails
     */
    @Override
    public RepeatDetails transformFrom(JasRepeatDetails external) {
        RepeatDetails internal = new RepeatDetails();
        internal.setFridayEnabled(external.isFridayEnabled());
        internal.setId(KeyUtil.stringToKey(external.getId()));
        internal.setMondayEnabled(external.isMondayEnabled());
        internal.setRepeatEvery(external.getRepeatEvery());
        internal.setRepeatType(external.getRepeatType());
        internal.setRepeatUntilType(external.getRepeatUntilType());
        internal.setSaturdayEnabled(external.isSaturdayEnabled());
        internal.setSundayEnabled(external.isSundayEnabled());
        internal.setThursdayEnabled(external.isThursdayEnabled());
        internal.setTuesdayEnabled(external.isTuesdayEnabled());
        internal.setUntilCount(external.getUntilCount());
        internal.setUntilDate(external.getUntilDate());
        internal.setWednesdayEnabled(external.isWednesdayEnabled());
        return internal;
    }
}
