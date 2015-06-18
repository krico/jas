package com.jasify.schedule.appengine.dao.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.dao.UniqueIndex;
import com.jasify.schedule.appengine.dao.UniqueIndexCache;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationMember;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

    @Nonnull
    @Override
    public List<Key> save(@Nonnull List<Organization> entities) throws ModelException {
        List<Key> ret = new ArrayList<>();
        for (Organization entity : entities) {
            ret.add(save(entity));
        }
        return ret;
    }

    @Override
    public void delete(@Nonnull Key id) {
        Organization found = getOrNull(id);
        if (found != null) {
            nameIndex.release(found.getLcName());
        }
        super.delete(id);
    }

    @Override
    public void delete(@Nonnull List<Key> ids) {
        for (Key id : ids) {
            delete(id);
        }
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

    public List<Organization> byMemberUserId(long userId) throws EntityNotFoundException {
        return byMemberUserId(Datastore.createKey(User.class, userId));
    }

    public List<Organization> byMemberUserId(Key userId) throws EntityNotFoundException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        List<OrganizationMember> organizationMembers = organizationMemberDao.byUserId(userId);
        if (organizationMembers.isEmpty()) return Collections.emptyList();
        List<Key> organizationIds = new ArrayList<>();
        for (OrganizationMember organizationMember : organizationMembers) {
            Key key = organizationMember.getOrganizationRef().getKey();
            if (key != null) organizationIds.add(key);
        }
        if (organizationIds.isEmpty()) return Collections.emptyList();
        return get(organizationIds);
    }

    public boolean isUserMemberOfAnyOrganization(Key userId) {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        return !organizationMemberDao.byUserIdKeys(userId).isEmpty();
    }

    public List<User> getUsersOfOrganization(Key organizationId) throws EntityNotFoundException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        UserDao userDao = new UserDao();
        List<OrganizationMember> organizationMembers = organizationMemberDao.byOrganizationId(organizationId);
        if (organizationMembers.isEmpty()) return Collections.emptyList();
        List<Key> userIds = new ArrayList<>();
        for (OrganizationMember organizationMember : organizationMembers) {
            Key key = organizationMember.getUserRef().getKey();
            if (key != null) userIds.add(key);
        }
        if (userIds.isEmpty()) return Collections.emptyList();
        return userDao.get(userIds);
    }

    public List<Group> getGroupsOfOrganization(Key organizationId) throws EntityNotFoundException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        GroupDao groupDao = new GroupDao();
        List<OrganizationMember> organizationMembers = organizationMemberDao.byOrganizationId(organizationId);
        if (organizationMembers.isEmpty()) return Collections.emptyList();
        List<Key> groupIds = new ArrayList<>();
        for (OrganizationMember organizationMember : organizationMembers) {
            Key key = organizationMember.getGroupRef().getKey();
            if (key != null) groupIds.add(key);
        }
        if (groupIds.isEmpty()) return Collections.emptyList();
        return groupDao.get(groupIds);
    }

    public boolean addUserToOrganization(Key organizationId, Key userId) throws ModelException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        OrganizationMember existing = organizationMemberDao.byOrganizationIdAndUserId(organizationId, userId);
        if (existing != null) return false;
        existing = new OrganizationMember();
        existing.getOrganizationRef().setKey(organizationId);
        existing.getUserRef().setKey(userId);
        organizationMemberDao.save(existing);
        return true;
    }

    public boolean removeUserFromOrganization(Key organizationId, Key userId) throws ModelException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        OrganizationMember existing = organizationMemberDao.byOrganizationIdAndUserId(organizationId, userId);
        if (existing == null) return false;
        organizationMemberDao.delete(existing.getId());
        return true;
    }

    public boolean addGroupToOrganization(Key organizationId, Key groupId) throws ModelException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        OrganizationMember existing = organizationMemberDao.byOrganizationIdAndGroupId(organizationId, groupId);
        if (existing != null) return false;
        existing = new OrganizationMember();
        existing.getOrganizationRef().setKey(organizationId);
        existing.getGroupRef().setKey(groupId);
        organizationMemberDao.save(existing);
        return true;
    }

    public boolean removeGroupFromOrganization(Key organizationId, Key groupId) throws ModelException {
        OrganizationMemberDao organizationMemberDao = new OrganizationMemberDao();
        OrganizationMember existing = organizationMemberDao.byOrganizationIdAndGroupId(organizationId, groupId);
        if (existing == null) return false;
        organizationMemberDao.delete(existing.getId());
        return true;
    }
}
