package com.jasify.schedule.appengine.dao.history;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.QueryParameters;
import com.jasify.schedule.appengine.meta.history.HistoryMeta;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.consistency.ImmutableEntityException;
import com.jasify.schedule.appengine.model.history.History;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author krico
 * @since 09/08/15.
 */
public class HistoryDao extends BaseCachingDao<History> {
    /**
     * History entries are immutable, they can stay in cache as long as we want :-)
     */
    public static final int DEFAULT_CACHE_EXPIRY_SECONDS = (int) TimeUnit.MINUTES.toSeconds(90);

    public HistoryDao() {
        super(HistoryMeta.get(), DEFAULT_CACHE_EXPIRY_SECONDS);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<History> entities) throws ModelException {
        for (History entity : entities) {
            if (entity.getId() != null) {
                throw new ImmutableEntityException("One cannot change history: " + entity.getId());
            }
        }
        return super.save(entities);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull History entity) throws ModelException {
        if (entity.getId() != null) {
            throw new ImmutableEntityException("One cannot change history: " + entity.getId());
        }
        return super.save(entity);
    }

    public List<History> listSince(final Date when) {
        return query(new SinceQuery(this.<HistoryMeta>getMeta(), when));
    }

    private static class SinceQuery extends BaseDaoQuery<History, HistoryMeta> {
        public SinceQuery(HistoryMeta meta, Date since) {
            super(meta, QueryParameters.of(since));
        }

        @Override
        public List<Key> execute() {
            Date since = parameters.get(0);
            return Datastore
                    .query(meta)
                    .filter(meta.created.greaterThanOrEqual(since))
                    .sort(meta.created.asc)
                    .asKeyList();
        }
    }

}
