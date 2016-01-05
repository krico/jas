package com.jasify.schedule.appengine.dao.multipass;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.meta.multipass.MultipassMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.multipass.Multipass;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author wszarmach
 * @since 09/11/15.
 */
public class MultipassDao  extends BaseDao<Multipass> {

    public static final MultipassDao INSTANCE = new MultipassDao();
    public static final OrganizationDao organizationDao = new OrganizationDao();

    public MultipassDao() {
        super(MultipassMeta.get());
    }

    // TODO: This is a copy paste of ActivityTypeDao - consider moving to a common class
    public boolean exists(String name, Key organizationId) {
        MultipassMeta meta = getMeta();
        return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId)
                .filter(meta.lcName.equal(StringUtils.lowerCase(name)))
                .count() > 0;
    }

    @Nonnull // TODO: This is a copy paste of ActivityTypeDao - consider moving to a common class
    public Key save(@Nonnull Multipass entity, @Nonnull Key organizationId) throws ModelException {
        organizationDao.get(organizationId);

        String name = StringUtils.trimToNull(entity.getName());
        if (name == null) {
            throw new FieldValueException("Multipass.name");
        }

        entity.setName(name);

        if (entity.getId() == null) {
            // New Multipass
            if (exists(name, organizationId)) {
                throw new UniqueConstraintException("Multipass.name=" + name + ", Organization.id=" + organizationId);
            }
            entity.getOrganizationRef().setKey(organizationId);
            return save(entity);
        }

        // Update Multipass
        Multipass dbMultipass = get(entity.getId());

        if (!StringUtils.equalsIgnoreCase(dbMultipass.getName(), entity.getName())) {
            if (exists(entity.getName(), organizationId)) {
                throw new UniqueConstraintException("Multipass.name=" + entity.getName() + ", Organization.id=" + organizationId);
            }
        }

        return save(entity);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull Multipass entity) throws ModelException {
        Preconditions.checkNotNull(entity.getOrganizationRef().getKey(), "Multipass must have organizationRef");
        if(entity.getId() == null) {
            Key organizationId = entity.getOrganizationRef().getKey();
            entity.setId(Datastore.allocateId(organizationId, getMeta()));
        }
        return super.save(entity);
    }

    public List<Multipass> getByOrganization(Key organizationId) {
        MultipassMeta meta = getMeta();
        return query(new ByOrganizationQuery(meta, organizationId));
    }

    private static class ByOrganizationQuery extends BaseDaoQuery<Multipass, MultipassMeta> {
        public ByOrganizationQuery(MultipassMeta meta, Key organizationId) {
            super(meta, new Serializable[]{organizationId});
        }

        @Override
        public List<Key> execute() {
            Key organizationId = parameters.get(0);
            return Datastore.query(Datastore.getCurrentTransaction(), meta, organizationId).asKeyList();
        }
    }
}