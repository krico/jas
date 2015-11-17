package com.jasify.schedule.appengine.dao.multipass;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.meta.multipass.MultipassActivityTypeMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import com.jasify.schedule.appengine.model.multipass.MultipassActivityType;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wszarmach
 * @since 17/11/15.
 */
public class MultipassActivityTypeDao extends BaseCachingDao<MultipassActivityType> {
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final MultipassDao multipassDao = new MultipassDao();

    public MultipassActivityTypeDao() {
        super(MultipassActivityTypeMeta.get());
    }

    @Nonnull
    public Key save(@Nonnull MultipassActivityType entity, @Nonnull Key multipassId) throws ModelException {
        if (entity.getId() == null) {
            entity.getMultipassRef().setKey(multipassId);
        }
        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull MultipassActivityType entity) throws ModelException {
        Preconditions.checkNotNull(entity.getMultipassRef().getKey(), "MultipassActivityType must have multipassRef");
        if(entity.getId() == null) {
            Multipass multipass = multipassDao.get(entity.getMultipassRef().getKey());
            Key organizationId = multipass.getOrganizationRef().getKey();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<MultipassActivityType> entities) throws ModelException {
        List<Key> result = new ArrayList<>();
        for (MultipassActivityType entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    public List<Key> getKeysByMultipassId(Key multipassId) throws EntityNotFoundException {
        MultipassActivityTypeMeta meta = getMeta();

        Multipass multipass = multipassDao.get(multipassId);
        Key organizationId = multipass.getOrganizationRef().getKey();

        return queryKeys(new ByOrganizationAndMultipassQuery(meta, organizationId, multipassId));
    }

    public List<MultipassActivityType> getByMultipassId(Key multipassId) throws EntityNotFoundException {
        MultipassActivityTypeMeta meta = getMeta();

        Multipass multipass = multipassDao.get(multipassId);
        Key organizationId = multipass.getOrganizationRef().getKey();

        return query(new ByOrganizationAndMultipassQuery(meta, organizationId, multipassId));
    }

    private static class ByOrganizationAndMultipassQuery extends BaseDaoQuery<MultipassActivityType, MultipassActivityTypeMeta> {
        public ByOrganizationAndMultipassQuery(MultipassActivityTypeMeta meta, Key organizationId, Key multipassId) {
            super(meta, new Serializable[]{organizationId, multipassId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            Key multipassId = parameters.get(1);
            return Datastore
                    .query(Datastore.getCurrentTransaction(), meta, organizationId)
                    .filter(meta.multipassRef.equal(multipassId))
                    .asKeyList();
        }
    }
}
