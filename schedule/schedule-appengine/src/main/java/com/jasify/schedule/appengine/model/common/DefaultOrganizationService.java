package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.meta.common.GroupMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMemberMeta;
import com.jasify.schedule.appengine.meta.common.OrganizationMeta;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraint;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import org.apache.commons.lang3.StringUtils;
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
    private final OrganizationMeta organizationMeta;
    private final OrganizationMemberMeta organizationMemberMeta;
    private final UniqueConstraint uniqueOrganizationName;
    private final GroupMeta groupMeta;

    private DefaultOrganizationService() {
        organizationMeta = OrganizationMeta.get();
        organizationMemberMeta = OrganizationMemberMeta.get();
        uniqueOrganizationName = UniqueConstraint.create(organizationMeta, organizationMeta.name);
        groupMeta = GroupMeta.get();
    }

    static OrganizationService instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public void addUserToGroup(Key groupId, Key userId) throws EntityNotFoundException, IllegalArgumentException {
        addUserToGroup(getGroup(groupId), UserServiceFactory.getUserService().getUser(userId));
    }

    @Override
    public void removeUserFromGroup(Key groupId, Key userId) throws EntityNotFoundException, IllegalArgumentException {
        removeUserFromGroup(getGroup(groupId), UserServiceFactory.getUserService().getUser(userId));
    }

    @Nonnull
    @Override
    public Key addGroup(Group group) throws UniqueConstraintException, FieldValueException {
        String name = StringUtils.trimToNull(group.getName());
        if (name == null) {
            throw new FieldValueException("Group.name");
        }
        group.setName(name);

        if (!isGroupNameAvailable(name)) {
            throw new UniqueConstraintException("Group.name=" + name);
        }

        group.setId(null);

        return Datastore.put(group);
    }

    private boolean isGroupNameAvailable(String name) {
        return Datastore.query(groupMeta)
                .filter(groupMeta.lcName.equal(StringUtils.lowerCase(name)))
                .asKeyList()
                .isEmpty();
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

    @Nonnull
    @Override
    public List<Group> getGroups() {
        return Datastore.query(groupMeta).asList();
    }

    @Nonnull
    @Override
    public Group updateGroup(Group group) throws EntityNotFoundException, FieldValueException {
        String name = StringUtils.trimToNull(group.getName());
        if (name == null) {
            throw new FieldValueException("Group.name");
        }

        Group dbGroup = getGroup(group.getId());
        dbGroup.setName(name); //TODO: Unique name?
        dbGroup.setDescription(group.getDescription());
        Datastore.put(dbGroup);
        return dbGroup;
    }

    @Override
    public void addUserToGroup(Group group, User user) throws EntityNotFoundException {
        Group dbGroup = getGroup(group.getId());
        User dbUser = UserServiceFactory.getUserService().getUser(user.getId());

        if (!dbGroup.getUserKeys().contains(dbUser.getId())) {
            GroupUser junction = new GroupUser(dbGroup, dbUser);
            Datastore.put(junction);
            group.getGroupUserListRef().clear(); // might be cached
        }
    }

    @Override
    public void removeUserFromGroup(Group group, User user) throws EntityNotFoundException {
        Group dbGroup = getGroup(group.getId());
        UserServiceFactory.getUserService().getUser(user.getId());

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

        //TODO: this should be transactional

        List<Key> memberShips = Datastore.query(organizationMemberMeta)
                .filter(organizationMemberMeta.groupRef.equal(dbGroup.getId()))
                .asKeyList();

        for (Key memberShip : memberShips) {
            Datastore.delete(memberShip);
        }

        Datastore.delete(dbGroup.getId());
    }

    private static class Singleton {
        private static final OrganizationService INSTANCE = new DefaultOrganizationService();
    }
}
