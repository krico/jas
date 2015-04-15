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
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.transform.*;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.*;

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
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasGroupTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
                JasRepeatDetailsTransformer.class,
                JasSubscriptionTransformer.class,
                JasTransactionTransformer.class,
                JasUserLoginTransformer.class,
                JasUserTransformer.class
        },
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class OrganizationEndpoint {

    @ApiMethod(name = "organizations.query", path = "organizations", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Organization> getOrganizations(User caller) throws UnauthorizedException, ForbiddenException, EntityNotFoundException {
        JasifyEndpointUser jasUser = mustBeLoggedIn(caller);
        // Admin sees all
        if (jasUser.isAdmin()) return OrganizationServiceFactory.getOrganizationService().getOrganizations();
        if (jasUser.isOrgMember()) {
            com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().get(jasUser.getUserId());
            return OrganizationServiceFactory.getOrganizationService().getOrganizationsForMember(user);
        }
        throw new ForbiddenException("Must be admin");

    }

    @ApiMethod(name = "organizations.get", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Organization getOrganization(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(id));
        try {
            return checkFound(OrganizationServiceFactory.getOrganizationService().getOrganization(id));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.users", path = "organization-users/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<com.jasify.schedule.appengine.model.users.User> getOrganizationUsers(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(id));
        try {
            Organization organization = checkFound(OrganizationServiceFactory.getOrganizationService().getOrganization(id));
            return organization.getUsers();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.groups", path = "organization-groups/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Group> getOrganizationGroups(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(id));
        try {
            Organization organization = OrganizationServiceFactory.getOrganizationService().getOrganization(id);
            return organization.getGroups();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.update", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Organization updateOrganization(User caller, @Named("id") Key id, Organization organization) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(organization.getId()));
        checkFound(id);
        organization.setId(id);
        try {
            return OrganizationServiceFactory.getOrganizationService().updateOrganization(organization);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        } catch (UniqueConstraintException | FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.add", path = "organizations", httpMethod = ApiMethod.HttpMethod.POST)
    public Organization addOrganization(User caller, Organization organization) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        Key id;
        try {
            id = OrganizationServiceFactory.getOrganizationService().addOrganization(organization);
        } catch (UniqueConstraintException | FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }
        try {
            return OrganizationServiceFactory.getOrganizationService().getOrganization(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.addUser", path = "organizations/{organizationId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addUserToOrganization(User caller, @Named("organizationId") Key organizationId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        try {
            OrganizationServiceFactory.getOrganizationService().addUserToOrganization(organizationId, userId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.removeUser", path = "organizations/{organizationId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeUserFromOrganization(User caller, @Named("organizationId") Key organizationId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        try {
            OrganizationServiceFactory.getOrganizationService().removeUserFromOrganization(organizationId, userId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.addGroup", path = "organizations/{organizationId}/groups/{groupId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addGroupToOrganization(User caller, @Named("organizationId") Key organizationId, @Named("groupId") Key groupId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(organizationId));
        try {
            OrganizationServiceFactory.getOrganizationService().addGroupToOrganization(organizationId, groupId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.removeGroup", path = "organizations/{organizationId}/groups/{groupId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeGroupFromOrganization(User caller, @Named("organizationId") Key organizationId, @Named("groupId") Key groupId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdminOrOrgMember(caller, createFromOrganizationId(organizationId));
        try {
            OrganizationServiceFactory.getOrganizationService().removeGroupFromOrganization(organizationId, groupId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.remove", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeOrganization(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        checkFound(id);
        try {
            OrganizationServiceFactory.getOrganizationService().removeOrganization(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
