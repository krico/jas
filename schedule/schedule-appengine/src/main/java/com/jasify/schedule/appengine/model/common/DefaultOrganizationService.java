package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
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

/**
 * @author krico
 * @since 08/01/15.
 */
final class DefaultOrganizationService implements OrganizationService {
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

    @Override
    public Key addOrganization(Organization organization) throws UniqueConstraintException, FieldValueException {
        return null;
    }

    @Override
    public Organization getOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException {
        return null;
    }

    @Override
    public Organization getOrganization(String name) throws EntityNotFoundException {
        return null;
    }

    @Override
    public void addUserToOrganization(Organization organization, User user) throws EntityNotFoundException {

    }

    @Override
    public void removeUserFromOrganization(Organization organization, User user) throws EntityNotFoundException {

    }

    @Override
    public void removeGroupFromOrganization(Organization organization, Group group) throws EntityNotFoundException {

    }

    @Override
    public void addGroupToOrganization(Organization organization, Group group) throws EntityNotFoundException {

    }

    @Override
    public void removeOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException {

    }

    @Override
    public Key addGroup(Group group) throws UniqueConstraintException, FieldValueException {
        return null;
    }

    @Override
    public Group getGroup(Key id) throws EntityNotFoundException, IllegalArgumentException {
        return null;
    }

    @Override
    public void addUserToGroup(Group group, User user) throws EntityNotFoundException {

    }

    @Override
    public void removeUserFromGroup(Group group, User user) throws EntityNotFoundException {

    }

    @Override
    public void removeGroup(Key id) throws EntityNotFoundException, IllegalArgumentException {

    }

    private static class Singleton {
        private static final OrganizationService INSTANCE = new DefaultOrganizationService();
    }
}
