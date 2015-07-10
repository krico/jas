package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.activity.RepeatDetailsMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.activity.RepeatDetails;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wszarmach
 * @since 03/07/15.
 */
public class RepeatDetailsDao extends BaseCachingDao<RepeatDetails> {

    public RepeatDetailsDao() {
        super(RepeatDetailsMeta.get());
    }

    private void validate(RepeatDetails entity) throws FieldValueException {
        if (entity.getRepeatType() == null) throw new FieldValueException("RepeatDetails.repeatType");
        if (entity.getRepeatType() != RepeatDetails.RepeatType.No) {
            if (entity.getRepeatEvery() <= 0) throw new FieldValueException("RepeatDetails.repeatEvery");
            if (entity.getRepeatUntilType() == null)
                throw new FieldValueException("RepeatDetails.repeatUntilType");
            if (entity.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Count && entity.getUntilCount() <= 0)
                throw new FieldValueException("RepeatDetails.untilCount");
            if (entity.getRepeatUntilType() == RepeatDetails.RepeatUntilType.Date) {
                if (entity.getUntilDate() == null)
                    throw new FieldValueException("RepeatDetails.untilDate");
                if (entity.getUntilDate().getTime() < System.currentTimeMillis())
                    throw new FieldValueException("RepeatDetails.untilDate");
            }
        }

        if (entity.getRepeatType() == RepeatDetails.RepeatType.Weekly) {
            boolean dayEnabled = entity.isMondayEnabled() || entity.isTuesdayEnabled() || entity.isWednesdayEnabled()
                    || entity.isThursdayEnabled() || entity.isFridayEnabled() || entity.isSaturdayEnabled()
                    || entity.isSundayEnabled();
            if (!dayEnabled) throw new FieldValueException("RepeatDetails.repeatDays");
        }
    }

    @Nonnull
    public Key save(@Nonnull RepeatDetails entity, @Nonnull Key organizationId) throws ModelException {
        if (entity.getId() == null) {
            // New RepeatDetails
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }

        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull RepeatDetails entity) throws ModelException {
        Preconditions.checkNotNull(entity.getId(), "RepeatDetails must have id");
        validate(entity);
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<RepeatDetails> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (RepeatDetails entity : entities) {
            result.add(save(entity));
        }
        return result;
    }
}
