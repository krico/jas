package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.common.OrganizationDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.ModelOperation;
import com.jasify.schedule.appengine.model.TransactionOperator;
import com.jasify.schedule.appengine.model.common.Group;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.consistency.ConsistencyGuard;
import com.jasify.schedule.appengine.model.consistency.InconsistentModelStateException;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                JasActivityPackageTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasContactMessageTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasMultipassTransformer.class,
                JasOrganizationTransformer.class,
                JasPaymentTransformer.class,
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
    private static final Logger log = LoggerFactory.getLogger(OrganizationEndpoint.class);

    private final OrganizationDao organizationDao = new OrganizationDao();

    @ApiMethod(name = "organizations.queryPublic", path = "organizations-public", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Organization> getPublicOrganizations(User caller) throws UnauthorizedException, ForbiddenException, EntityNotFoundException {
        return organizationDao.getAll();
    }

    @ApiMethod(name = "organizations.query", path = "organizations", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Organization> getOrganizations(User caller) throws UnauthorizedException, ForbiddenException, EntityNotFoundException {
        JasifyEndpointUser jasUser = mustBeLoggedIn(caller);
        // Admin sees all
        if (jasUser.isAdmin()) {
            return organizationDao.getAll();
        }
        if (jasUser.isOrgMember()) {
            return organizationDao.getByMemberUserId(jasUser.getUserId());
        }
        throw new ForbiddenException("Must be admin");
    }

    @ApiMethod(name = "organizations.get", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Organization getOrganization(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(id));
        try {
            return organizationDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.add", path = "organizations", httpMethod = ApiMethod.HttpMethod.POST)
    public Organization addOrganization(User caller, final Organization organization) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        try {
            return TransactionOperator.execute(new ModelOperation<Organization>() {
                @Override
                public Organization execute(Transaction tx) throws ModelException {
                    organizationDao.save(organization);
                    tx.commit();
                    return organization;
                }
            });
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.users", path = "organization-users/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<com.jasify.schedule.appengine.model.users.User> getOrganizationUsers(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(id));
        try {
            return organizationDao.getUsersOfOrganization(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.groups", path = "organization-groups/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Group> getOrganizationGroups(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(id));
        try {
            return organizationDao.getGroupsOfOrganization(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.update", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Organization updateOrganization(User caller, @Named("id") final Key id, final Organization organization) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(organization.getId()));
        checkFound(id);
        organization.setId(id);

        try {
            return TransactionOperator.execute(new ModelOperation<Organization>() {
                @Override
                public Organization execute(Transaction tx) throws ModelException {
                    Organization org = organizationDao.get(id);
                    BeanUtil.copyPropertiesExcluding(org, organization,
                            "id", "created", "modified", "organizationMemberListRef", "lcName");
                    organizationDao.save(org);
                    tx.commit();
                    return org;
                }
            });
        } catch (EntityNotFoundException enfe) {
            throw new NotFoundException("Organization not found");
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.addUser", path = "organizations/{organizationId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addUserToOrganization(User caller, @Named("organizationId") Key organizationId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        mustBeAdmin(caller);
        try {
            if (!organizationDao.addUserToOrganization(organizationId, userId)) {
                log.info("Did not add (already member) user [{}] to organization [{}]", userId, organizationId);
            }
        } catch (ModelException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.removeUser", path = "organizations/{organizationId}/users/{userId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeUserFromOrganization(User caller, @Named("organizationId") Key organizationId, @Named("userId") Key userId) throws UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        mustBeAdmin(caller);
        try {
            if (!organizationDao.removeUserFromOrganization(organizationId, userId)) {
                log.info("Did not remove (not a member) user [{}] from organization [{}]", userId, organizationId);
            }
        } catch (ModelException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.addGroup", path = "organizations/{organizationId}/groups/{groupId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addGroupToOrganization(User caller, @Named("organizationId") Key organizationId, @Named("groupId") Key groupId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException, InternalServerErrorException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(organizationId));
        try {
            if (!organizationDao.addGroupToOrganization(organizationId, groupId)) {
                log.info("Did not add (already member) group [{}] to organization [{}]", groupId, organizationId);
            }
        } catch (ModelException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.removeGroup", path = "organizations/{organizationId}/groups/{groupId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeGroupFromOrganization(User caller, @Named("organizationId") Key organizationId, @Named("groupId") Key groupId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException, InternalServerErrorException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(organizationId));
        try {
            if (!organizationDao.removeGroupFromOrganization(organizationId, groupId)) {
                log.info("Did not remove (not a member) group [{}] from organization [{}]", groupId, organizationId);
            }
        } catch (ModelException e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @ApiMethod(name = "organizations.remove", path = "organizations/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeOrganization(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, InternalServerErrorException, BadRequestException {
        mustBeAdmin(caller);
        checkFound(id);
        //TODO: This method needs a lot more checks to determine if an organization can be deleted
        //TODO: Not sure where this should go

        try {
            ConsistencyGuard.beforeDelete(Organization.class, id);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    organizationDao.get(id); //Throws not found if this organization doesn't exist
                    organizationDao.delete(id);
                    tx.commit();
                    return null;
                }
            });
        } catch (InconsistentModelStateException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException me) {
            throw new InternalServerErrorException(me.getMessage());
        }
    }
}
