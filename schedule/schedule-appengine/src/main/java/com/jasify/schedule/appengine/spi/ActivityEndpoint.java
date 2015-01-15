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
import com.jasify.schedule.appengine.model.activity.Activity;
import com.jasify.schedule.appengine.model.activity.ActivityServiceFactory;
import com.jasify.schedule.appengine.model.activity.ActivityType;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.common.OrganizationServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import com.jasify.schedule.appengine.spi.transform.*;

import java.util.Date;
import java.util.List;
import java.util.Random;

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
public class ActivityEndpoint {
    private static final Random random = new Random();

    @ApiMethod(name = "activityTypes.query", path = "activity-types", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityType> getActivityTypes(User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        try {
            return ActivityServiceFactory.getActivityService().getActivityTypes(organizationId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.get", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ActivityType getActivityType(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        checkFound(id);
        try {
            return ActivityServiceFactory.getActivityService().getActivityType(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.update", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public ActivityType updateActivityType(User caller, @Named("id") Key id, ActivityType activityType) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdmin(caller);
        checkFound(id);
        activityType.setId(id);
        try {
            return ActivityServiceFactory.getActivityService().updateActivityType(activityType);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        } catch (FieldValueException | UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.add", path = "activity-types", httpMethod = ApiMethod.HttpMethod.POST)
    public ActivityType addActivityType(User caller, JasAddActivityTypeRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdmin(caller);
        checkFound(request.getActivityType());
        checkFound(request.getOrganizationId());
        Key id;
        try {
            Organization organization = OrganizationServiceFactory.getOrganizationService().getOrganization(request.getOrganizationId());
            id = ActivityServiceFactory.getActivityService().addActivityType(organization, request.getActivityType());
        } catch (UniqueConstraintException | FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        try {
            return ActivityServiceFactory.getActivityService().getActivityType(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.remove", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityType(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        checkFound(id);
        try {
            ActivityServiceFactory.getActivityService().removeActivityType(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.query", path = "activities", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivities(User caller,
                                        @Nullable @Named("organizationId") Key organizationId,
                                        @Nullable @Named("activityTypeId") Key activityTypeId,
                                        @Nullable @Named("fromDate") Date fromDate,
                                        @Nullable @Named("toDate") Date toDate,
                                        @Nullable @Named("offset") Integer offset,
                                        @Nullable @Named("limit") Integer limit) {
        return null;
    }
}
