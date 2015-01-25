package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.checkFound;
import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeAdmin;

/**
 * @author krico
 * @since 11/01/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class GroupEndpoint {

    @ApiMethod(name = "groups.query", path = "groups", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Group> getGroups(User caller) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        return OrganizationServiceFactory.getOrganizationService().getGroups();
    }

    @ApiMethod(name = "groups.get", path = "groups/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Group getGroup(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        try {
            return checkFound(OrganizationServiceFactory.getOrganizationService().getGroup(id));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.update", path = "groups/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Group updateGroup(User caller, @Named("id") Key id, Group group) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdmin(caller);
        checkFound(id);
        group.setId(id);
        try {
            return OrganizationServiceFactory.getOrganizationService().updateGroup(group);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.add", path = "groups", httpMethod = ApiMethod.HttpMethod.POST)
    public Group addGroup(User caller, Group group) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        Key id;
        try {
            id = OrganizationServiceFactory.getOrganizationService().addGroup(group);
        } catch (UniqueConstraintException | FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }
        try {
            return OrganizationServiceFactory.getOrganizationService().getGroup(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.remove", path = "groups/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeGroup(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        checkFound(id);
        try {
            OrganizationServiceFactory.getOrganizationService().removeGroup(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.users", path = "group-users/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<com.jasify.schedule.appengine.model.users.User> getGroupUsers(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        try {
            Group group = OrganizationServiceFactory.getOrganizationService().getGroup(id);
            return group.getUsers();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.addUser", path = "groups/{groupId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addUserToGroup(User caller, @Named("groupId") Key groupId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        try {
            OrganizationServiceFactory.getOrganizationService().addUserToGroup(groupId, userId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "groups.removeUser", path = "groups/{groupId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeUserFromGroup(User caller, @Named("groupId") Key groupId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        try {
            OrganizationServiceFactory.getOrganizationService().removeUserFromGroup(groupId, userId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
