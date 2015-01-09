package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.meta.common.GroupMeta;
import com.jasify.schedule.appengine.meta.common.GroupUserMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author krico
 * @since 08/01/15.
 */
final class DefaultOrganizationService implements OrganizationService {
    private static final Logger log = LoggerFactory.getLogger(DefaultOrganizationService.class);

    private final UserMeta userMeta;
    private final OrganizationMeta organizationMeta;
    private final UniqueConstraint uniqueOrganizationName;

    private final OrganizationMemberMeta organizationMemberMeta;
    private final GroupMeta groupMeta;
    private final GroupUserMeta groupUserMeta;

    private DefaultOrganizationService() {
        userMeta = UserMeta.get();
        organizationMeta = OrganizationMeta.get();
        uniqueOrganizationName = UniqueConstraint.create(organizationMeta, organizationMeta.name);
        organizationMemberMeta = OrganizationMemberMeta.get();
        groupMeta = GroupMeta.get();
        groupUserMeta = GroupUserMeta.get();
    }

    static OrganizationService instance() {
        return Singleton.INSTANCE;
    }

    private User getUser(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(userMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("User id=" + id);
        }
    }

    @Nonnull
    @Override
    public Key addOrganization(Organization organization) throws UniqueConstraintException, FieldValueException {
        String name = StringUtils.trimToNull(organization.getName());
        if (name == null) {
            throw new FieldValueException("Organization.name");
        }
        organization.setName(name);

        uniqueOrganizationName.reserve(StringUtils.lowerCase(name));

        organization.setId(null);

        Transaction tx = Datastore.beginTransaction();
        Key ret = Datastore.put(tx, organization);
        tx.commit();

        return ret;
    }

    @Nonnull
    @Override
    public Organization getOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(organizationMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Organization id=" + id);
        }
    }

    @Nonnull
    @Override
    public Organization getOrganization(String name) throws EntityNotFoundException {
        Organization ret = Datastore.query(organizationMeta).filter(organizationMeta.lcName.equal(StringUtils.lowerCase(name))).asSingle();
        if (ret == null) throw new EntityNotFoundException("Organization name=" + name);
        return ret;
    }

    @Override
    public void addUserToOrganization(Organization organization, User user) throws EntityNotFoundException {
        Organization dbOrganization = getOrganization(organization.getId());
        User dbUser = getUser(user.getId());

        if (!dbOrganization.getUserKeys().contains(user.getId())) {
            OrganizationMember junction = new OrganizationMember(dbOrganization, dbUser);
            Datastore.put(junction);
            organization.getOrganizationMemberListRef().clear(); // might be cached
        }
    }

    @Override
    public void removeUserFromOrganization(Organization organization, User user) throws EntityNotFoundException {
        Organization dbOrganization = getOrganization(organization.getId());
        User dbUser = getUser(user.getId());

        Set<Key> toRemove = new HashSet<>();
        List<OrganizationMember> list = dbOrganization.getOrganizationMemberListRef().getModelList();
        for (OrganizationMember organizationMember : list) {
            if (user.getId().equals(organizationMember.getUserRef().getKey())) {
                toRemove.add(organizationMember.getId());
            }
        }

        Datastore.delete(toRemove);
        organization.getOrganizationMemberListRef().clear();
    }

    @Override
    public void removeGroupFromOrganization(Organization organization, Group group) throws EntityNotFoundException {
        Organization dbOrganization = getOrganization(organization.getId());
        getGroup(group.getId());

        Set<Key> toRemove = new HashSet<>();
        List<OrganizationMember> list = dbOrganization.getOrganizationMemberListRef().getModelList();
        for (OrganizationMember organizationMember : list) {
            if (group.getId().equals(organizationMember.getGroupRef().getKey())) {
                toRemove.add(organizationMember.getId());
            }
        }

        Datastore.delete(toRemove);
        organization.getOrganizationMemberListRef().clear();
    }

    @Override
    public void addGroupToOrganization(Organization organization, Group group) throws EntityNotFoundException {
        Organization dbOrganization = getOrganization(organization.getId());
        Group dbGroup = getGroup(group.getId());

        if (!dbOrganization.getGroupKeys().contains(dbGroup.getId())) {
            OrganizationMember junction = new OrganizationMember(dbOrganization, dbGroup);
            Datastore.put(junction);
            organization.getOrganizationMemberListRef().clear(); // might be cached
        }
    }

    @Override
    public void removeOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException {
        Organization dbOrganization = getOrganization(id);
        List<OrganizationMember> list = dbOrganization.getOrganizationMemberListRef().getModelList();
        List<Key> toDel = new ArrayList<>();
        for (OrganizationMember member : list) {
            toDel.add(member.getId());
        }
        Datastore.delete(toDel);
        Datastore.delete(dbOrganization.getId());
    }

    @Nonnull
    @Override
    public Key addGroup(Group group) throws UniqueConstraintException, FieldValueException {
        String name = StringUtils.trimToNull(group.getName());
        if (name == null) {
            throw new FieldValueException("Group.name");
        }
        group.setName(name);

        //TODO: name constraint

        group.setId(null);

        Transaction tx = Datastore.beginTransaction();
        Key ret = Datastore.put(tx, group);
        tx.commit();

        return ret;
    }

    @Nonnull
    @Override
    public Group getGroup(Key id) throws EntityNotFoundException, IllegalArgumentException {
        try {
            return Datastore.get(groupMeta, id);
        } catch (EntityNotFoundRuntimeException e) {
            throw new EntityNotFoundException("Group id=" + id);
        }
    }

    @Override
    public void addUserToGroup(Group group, User user) throws EntityNotFoundException {
        Group dbGroup = getGroup(group.getId());
        User dbUser = getUser(user.getId());

        if (!dbGroup.getUserKeys().contains(dbUser.getId())) {
            GroupUser junction = new GroupUser(dbGroup, dbUser);
            Datastore.put(junction);
            group.getGroupUserListRef().clear(); // might be cached
        }
    }

    @Override
    public void removeUserFromGroup(Group group, User user) throws EntityNotFoundException {
        Group dbGroup = getGroup(group.getId());
        getUser(user.getId());

        Set<Key> toRemove = new HashSet<>();
        List<GroupUser> list = dbGroup.getGroupUserListRef().getModelList();
        for (GroupUser groupUser : list) {
            if (user.getId().equals(groupUser.getUserRef().getKey())) {
                toRemove.add(groupUser.getId());
            }
        }

        Datastore.delete(toRemove);
        group.getGroupUserListRef().clear();

    }

    @Override
    public void removeGroup(Key id) throws EntityNotFoundException, IllegalArgumentException {
        Group dbGroup = getGroup(id);
        List<GroupUser> list = dbGroup.getGroupUserListRef().getModelList();
        List<Key> toDel = new ArrayList<>();
        for (GroupUser member : list) {
            toDel.add(member.getId());
        }
        Datastore.delete(toDel);
        Datastore.delete(dbGroup.getId());
    }

    private static class Singleton {
        private static final OrganizationService INSTANCE = new DefaultOrganizationService();
    }
}
