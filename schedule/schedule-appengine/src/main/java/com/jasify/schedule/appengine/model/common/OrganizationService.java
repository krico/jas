package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.User;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 08/01/15.
 */
public interface OrganizationService {

    /**
     * @param organization to ge added
     * @return the id for the newly created organization
     * @throws UniqueConstraintException if the name already existed
     * @throws FieldValueException       if any fields had invalid values (e.g name missing)
     */
    @Nonnull
    public Key addOrganization(Organization organization) throws UniqueConstraintException, FieldValueException;

    /**
     * @param id of the organization
     * @return the organization
     * @throws EntityNotFoundException  if the organization doesn't exist
     * @throws IllegalArgumentException if <code>id</code> is not the key to an Organization
     */
    @Nonnull
    public Organization getOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param name to search by
     * @return the organization with name
     * @throws EntityNotFoundException if organization was not found
     */
    @Nonnull
    public Organization getOrganization(String name) throws EntityNotFoundException;

    /**
     * @param organization to add the user to
     * @param user         to be added to the organization
     * @throws EntityNotFoundException if either the user or the organization don't exist
     */
    public void addUserToOrganization(Organization organization, User user) throws EntityNotFoundException;

    /**
     * @param organization to remove the user from
     * @param user         to be removed from the organization
     * @throws EntityNotFoundException if either the user or the organization don't exist
     */
    public void removeUserFromOrganization(Organization organization, User user) throws EntityNotFoundException;

    /**
     * @param organization to remove the group from
     * @param group        to be removed from the organization
     * @throws EntityNotFoundException if either the group or the organization don't exist
     */
    public void removeGroupFromOrganization(Organization organization, Group group) throws EntityNotFoundException;

    /**
     * @param organization to add the group to
     * @param group        to be added to the organization
     * @throws EntityNotFoundException if either the group or the organization don't exist
     */
    public void addGroupToOrganization(Organization organization, Group group) throws EntityNotFoundException;

    /**
     * Remove and existing organization
     *
     * @param id of the organization to be removed
     * @throws EntityNotFoundException  if the organization doesn't exist
     * @throws IllegalArgumentException if <code>id</code> is not the key to an Organization
     */
    public void removeOrganization(Key id) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param group to be added
     * @return the ide of the newly created group
     * @throws UniqueConstraintException if the group name already existed withing this context (not so sure ATM)
     * @throws FieldValueException       if any fields had invalid values (e.g name missing)
     */
    @Nonnull
    public Key addGroup(Group group) throws UniqueConstraintException, FieldValueException;

    /**
     * @param id of the group
     * @return the group
     * @throws EntityNotFoundException  if the group doesn't exist
     * @throws IllegalArgumentException if <code>id</code> is not the key to a group
     */
    @Nonnull
    public Group getGroup(Key id) throws EntityNotFoundException, IllegalArgumentException;


    /**
     * @param group to add the user to
     * @param user  to be added to the group
     * @throws EntityNotFoundException if either don't exist
     */
    public void addUserToGroup(Group group, User user) throws EntityNotFoundException;

    /**
     * @param group to remove from
     * @param user  to add
     * @throws EntityNotFoundException if either doesn't exist
     */
    public void removeUserFromGroup(Group group, User user) throws EntityNotFoundException;

    /**
     * Remove and existing organization
     *
     * @param id of the group to be removed
     * @throws EntityNotFoundException  if the group doesn't exist
     * @throws IllegalArgumentException if <code>id</code> is not the key to a Group
     */
    public void removeGroup(Key id) throws EntityNotFoundException, IllegalArgumentException;
}
