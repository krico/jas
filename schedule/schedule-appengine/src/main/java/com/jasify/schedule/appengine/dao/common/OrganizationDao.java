package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.UniqueIndex;
import com.jasify.schedule.appengine.dao.UniqueIndexCache;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.common.Organization;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author krico
 * @since 29/05/15.
 */
public class OrganizationDao extends BaseCachingDao<Organization> {
    private static final String UNIQUE_ORGANIZATION_CONSTRAINT = "OrganizationDao.OrganizationName";
    private final UniqueIndex nameIndex;

    public OrganizationDao() {
        super(OrganizationMeta.get());
        nameIndex = UniqueIndexCache.get(UNIQUE_ORGANIZATION_CONSTRAINT, meta, OrganizationMeta.get().name, false);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull Organization entity) throws ModelException {
        String name = StringUtils.trimToNull(entity.getName());
        if (name == null) {
            throw new FieldValueException("Organization.name is NULL");
        }
        entity.setName(name);
        String lcName = StringUtils.lowerCase(entity.getName());
        if (entity.getId() == null) {
            //new entity, reserve
            nameIndex.reserve(lcName);
        } else {
            Organization current = getOrNull(entity.getId());
            if (current == null) {
                //new entity, reserve
                nameIndex.reserve(lcName);
            } else if (!StringUtils.equals(current.getLcName(), lcName)) {
                //existing entity, release old, reserve new
                nameIndex.release(current.getLcName());
                nameIndex.reserve(lcName);
            }
        }
        return super.save(entity);
    }

    @Override
    public void delete(@Nonnull Key id) {
        Organization found = getOrNull(id);
        if (found != null) {
            nameIndex.release(found.getLcName());
        }
        super.delete(id);
    }

    public List<Organization> getAll() {
        OrganizationMeta meta = getMeta();
        return query(new BaseDaoQuery<Organization, OrganizationMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta).asKeyList();
            }
        });
    }
}
