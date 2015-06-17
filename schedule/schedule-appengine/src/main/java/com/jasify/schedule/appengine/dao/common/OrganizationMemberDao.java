package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import org.slim3.datastore.Datastore;

import java.io.Serializable;
import java.util.List;

/**
 * This Dao is package protected as it's main job is to be used by {@link com.jasify.schedule.appengine.dao.common.OrganizationDao}
 *
 * @author krico
 * @since 08/06/15.
 */
class OrganizationMemberDao extends BaseCachingDao<OrganizationMember> {
    public OrganizationMemberDao() {
        super(OrganizationMemberMeta.get());
    }

    public List<OrganizationMember> byUserId(Key userId) {
        OrganizationMemberMeta meta = getMeta();
        return query(new BaseDaoQuery<OrganizationMember, OrganizationMemberMeta>(meta, new Serializable[]{userId}) {
            @Override
            public List<Key> execute() {
                Key userId = parameters.get(0);
                return Datastore
                        .query(meta).filter(meta.userRef.equal(userId))
                        .asKeyList();
            }
        });

    }

    public List<OrganizationMember> byOrganizationId(Key organizationId) {
        OrganizationMemberMeta meta = getMeta();
        return query(new BaseDaoQuery<OrganizationMember, OrganizationMemberMeta>(meta, new Serializable[]{organizationId}) {
            @Override
            public List<Key> execute() {
                Key organizationId = parameters.get(0);
                return Datastore
                        .query(meta).filter(meta.organizationRef.equal(organizationId))
                        .asKeyList();
            }
        });
    }

    public OrganizationMember byOrganizationIdAndUserId(Key organizationId, Key userId) {
        OrganizationMemberMeta meta = getMeta();
        return Datastore
                .query(meta)
                .filter(meta.organizationRef.equal(organizationId),
                        meta.userRef.equal(userId))
                .asSingle();
    }
}
