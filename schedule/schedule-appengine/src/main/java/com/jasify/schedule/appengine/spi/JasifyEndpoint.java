package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.model.users.UserServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasApiInfo;
import com.jasify.schedule.appengine.spi.transform.*;

/**
 * @author krico
 * @since 27/12/14.
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
public class JasifyEndpoint {
    abstract static class OrgMemberChecker {
        protected final Key id;
        protected OrgMemberChecker(Key id) {
            this.id = id;
        }
        public boolean isOrgMember(long userId) throws EntityNotFoundException {
            Organization organization = getOrganization();
            com.jasify.schedule.appengine.model.users.User user = UserServiceFactory.getUserService().get(userId);
            return organization.getUsers().contains(user);
        }
        abstract Organization getOrganization() throws EntityNotFoundException;
    }

    static OrgMemberChecker createFromActivityId(Key id) {
        return new OrgMemberChecker(id) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                Activity activity = ActivityServiceFactory.getActivityService().getActivity(this.id);
                ActivityType activityType = activity.getActivityTypeRef().getModel();
                return activityType.getOrganizationRef().getModel();
            }
        };
    }

    static OrgMemberChecker createFromActivityTypeId(Key id) {
        return new OrgMemberChecker(id) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                ActivityType activityType = ActivityServiceFactory.getActivityService().getActivityType(this.id);
                return activityType.getOrganizationRef().getModel();
            }
        };
    }

    static OrgMemberChecker createFromActivityPackageId(Key id) {
        return new OrgMemberChecker(id) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                ActivityPackage activityPackage = ActivityServiceFactory.getActivityService().getActivityPackage(this.id);
                return activityPackage.getOrganizationRef().getModel();
            }
        };
    }

    static OrgMemberChecker createFromSubscriptionId(Key id) {
        return new OrgMemberChecker(id) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                Subscription subscription = ActivityServiceFactory.getActivityService().getSubscription(this.id);
                Activity activity = subscription.getActivityRef().getModel();
                ActivityType activityType = activity.getActivityTypeRef().getModel();
                return activityType.getOrganizationRef().getModel();
            }
        };
    }

    static OrgMemberChecker createFromOrganizationId(Key id) {
        return new OrgMemberChecker(id) {
            @Override
            Organization getOrganization() throws EntityNotFoundException {
                return OrganizationServiceFactory.getOrganizationService().getOrganization(this.id);
            }
        };
    }

    static JasifyEndpointUser mustBeLoggedIn(User caller) throws UnauthorizedException {
        if (caller instanceof JasifyEndpointUser) return (JasifyEndpointUser) caller;
        throw new UnauthorizedException("Only authenticated users can call this method");
    }

    static JasifyEndpointUser mustBeAdmin(User caller) throws ForbiddenException, UnauthorizedException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.isAdmin()) return jasifyEndpointUser;
        throw new ForbiddenException("Must be admin");
    }

    static JasifyEndpointUser mustBeAdminOrOrgMember(User caller, OrgMemberChecker orgMemberChecker) throws ForbiddenException, UnauthorizedException, NotFoundException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.isAdmin()) return jasifyEndpointUser;
        try {
            if (jasifyEndpointUser.isOrgMember() && orgMemberChecker.isOrgMember(jasifyEndpointUser.getUserId())) return jasifyEndpointUser;
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        throw new ForbiddenException("Must be admin");
    }

    static JasifyEndpointUser mustBeSameUserOrAdmin(User caller, Key userId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkFound(userId);
        return mustBeSameUserOrAdmin(caller, userId.getId());
    }

    static JasifyEndpointUser mustBeSameUserOrAdminOrOrgMember(User caller, Key userId, OrgMemberChecker orgMemberChecker) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkFound(userId);
        return mustBeSameUserOrAdminOrOrgMember(caller, userId.getId(), orgMemberChecker);
    }

    static JasifyEndpointUser mustBeSameUserOrAdmin(User caller, long userId) throws UnauthorizedException, ForbiddenException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.getUserId() != userId) return mustBeAdmin(caller);
        return jasifyEndpointUser;
    }

    static JasifyEndpointUser mustBeSameUserOrAdminOrOrgMember(User caller, long userId, OrgMemberChecker orgMemberChecker) throws UnauthorizedException, ForbiddenException, NotFoundException {
        JasifyEndpointUser jasifyEndpointUser = mustBeLoggedIn(caller);
        if (jasifyEndpointUser.getUserId() != userId) return mustBeAdminOrOrgMember(caller, orgMemberChecker);
        return jasifyEndpointUser;
    }

    static <T> T checkFound(T e) throws NotFoundException {
        return checkFound(e, "Not found");
    }

    static <T> T checkFound(T e, String message) throws NotFoundException {
        if (e == null) throw new NotFoundException(message);
        return e;
    }

    @ApiMethod(name = "apiInfo", path = "api-info", httpMethod = ApiMethod.HttpMethod.GET)
    public JasApiInfo getApiInfo(User caller) {
        JasApiInfo info = new JasApiInfo(this);
        if (caller instanceof JasifyEndpointUser) {
            info.setAuthenticated(true);
            info.setAdmin(((JasifyEndpointUser) caller).isAdmin());
            info.setOrgMember(((JasifyEndpointUser) caller).isOrgMember());
        }
        return info;
    }
}
