package com.jasify.schedule.appengine.model.common;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.User;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author krico
 * @since 08/01/15.
 */
public interface OrganizationService {

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
     * @return all groups
     */
    @Nonnull
    public List<Group> getGroups();

    /**
     * @param group to update
     * @return the updated group
     * @throws EntityNotFoundException if the group didn't exist before
     * @throws FieldValueException     if fields have invalid values
     */
    @Nonnull
    public Group updateGroup(Group group) throws EntityNotFoundException, FieldValueException;

    /**
     * @param group to add the user to
     * @param user  to be added to the group
     * @throws EntityNotFoundException if either don't exist
     */
    public void addUserToGroup(Group group, User user) throws EntityNotFoundException;

    /**
     * @param groupId to add to
     * @param userId  to be added
     * @throws EntityNotFoundException  if either doesn't exist
     * @throws IllegalArgumentException if either is not the entity they are expected to be
     */
    public void addUserToGroup(Key groupId, Key userId) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * @param group to remove from
     * @param user  to add
     * @throws EntityNotFoundException if either doesn't exist
     */
    public void removeUserFromGroup(Group group, User user) throws EntityNotFoundException;

    /**
     * @param groupId to remove from
     * @param userId  to remove
     * @throws EntityNotFoundException  if any doesn't exist
     * @throws IllegalArgumentException if any is not the expected entity
     */
    public void removeUserFromGroup(Key groupId, Key userId) throws EntityNotFoundException, IllegalArgumentException;

    /**
     * Remove and existing organization
     *
     * @param id of the group to be removed
     * @throws EntityNotFoundException  if the group doesn't exist
     * @throws IllegalArgumentException if <code>id</code> is not the key to a Group
     */
    public void removeGroup(Key id) throws EntityNotFoundException, IllegalArgumentException;
}
