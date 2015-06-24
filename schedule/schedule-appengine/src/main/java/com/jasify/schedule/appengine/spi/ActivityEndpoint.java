package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.OperationException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import com.jasify.schedule.appengine.spi.dm.JasListQueryActivitiesRequest;
import com.jasify.schedule.appengine.spi.transform.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class ActivityEndpoint {

    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final ActivityPackageActivityDao activityPackageActivityDao = new ActivityPackageActivityDao();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final OrganizationDao organizationDao = new OrganizationDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();
    private final UserDao userDao = new UserDao();

    @ApiMethod(name = "activityTypes.query", path = "activity-types", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityType> getActivityTypes(User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId);
        return activityTypeDao.getByOrganization(organizationId);

    }

    @ApiMethod(name = "activityTypes.get", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ActivityType getActivityType(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        checkFound(id);
        try {
            return activityTypeDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.update", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public ActivityType updateActivityType(User caller, @Named("id") Key id, ActivityType activityType) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        checkFound(id);
        activityType.setId(id);
        try {
            final ActivityType dbActivityType = activityTypeDao.get(activityType.getId());
            final Key organizationId = dbActivityType.getOrganizationRef().getKey();
            if (!StringUtils.equalsIgnoreCase(dbActivityType.getName(), activityType.getName())) {
                if (activityTypeDao.exists(activityType.getName(), organizationId)) {
                    throw new BadRequestException("ActivityType.name=" + activityType.getName() + ", Organization.id=" + organizationId);
                }
            }
            return ActivityServiceFactory.getActivityService().updateActivityType(activityType);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (FieldValueException | UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.add", path = "activity-types", httpMethod = ApiMethod.HttpMethod.POST)
    public ActivityType addActivityType(User caller, JasAddActivityTypeRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(request.getOrganizationId()));
        checkFound(request.getActivityType());
        Key organizationId = checkFound(request.getOrganizationId());
        Key id;
        try {
            final String name = StringUtils.trimToNull(request.getActivityType().getName());
            if (name == null) {
                throw new BadRequestException("ActivityType.name");
            }

            if (activityTypeDao.exists(name, organizationId)) {
                throw new BadRequestException("ActivityType.name=" + name + ", Organization.id=" + organizationId);
            }
            Organization organization = organizationDao.get(organizationId);
            id = ActivityServiceFactory.getActivityService().addActivityType(organization, request.getActivityType());
            return activityTypeDao.get(id);
        } catch (UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.remove", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityType(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        checkFound(id);
        try {
            // TODO: Possible race condition? Need to protect
            if (!activityDao.getByActivityTypeId(id).isEmpty()) {
                throw new BadRequestException("ActivityType has activities");
            }
            ActivityType activityType = activityTypeDao.get(id);
            ActivityServiceFactory.getActivityService().removeActivityType(activityType);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    private List<Activity> filterActivities(List<Activity> activities, final Date fromDate,
                                            final Date toDate,
                                            Integer offset,
                                            Integer limit) {
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<Activity> filtered = new ArrayList<>();
        filtered.addAll(Collections2.filter(activities, new Predicate<Activity>() {
            @Override
            public boolean apply(@Nullable Activity input) {
                if (fromDate != null && input.getStart() != null && fromDate.after(input.getStart())) {
                    return false;
                }
                if (toDate != null && input.getFinish() != null && toDate.before(input.getFinish())) {
                    return false;
                }
                return true;
            }
        }));

        if (offset == null) offset = 0;
        if (limit == null) limit = 0;

        if (offset > 0 || limit > 0) {
            if (offset < filtered.size()) {
                if (limit <= 0) limit = filtered.size();
                return new ArrayList<>(filtered.subList(offset, Math.min(offset + limit, filtered.size())));
            } else {
                return Collections.emptyList();
            }
        }

        return filtered;
    }

    @ApiMethod(name = "activities.query", path = "activities", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivities(User caller,
                                        @Nullable @Named("organizationId") Key organizationId,
                                        @Nullable @Named("activityTypeId") Key activityTypeId,
                                        @Nullable @Named("fromDate") final Date fromDate,
                                        @Nullable @Named("toDate") final Date toDate,
                                        @Nullable @Named("offset") Integer offset,
                                        @Nullable @Named("limit") Integer limit) throws BadRequestException, NotFoundException {

        if (activityTypeId != null && organizationId != null) {
            throw new BadRequestException("Must choose one: activityTypeId or organizationId");
        } else if (activityTypeId == null && organizationId == null) {
            throw new BadRequestException("Must choose one: activityTypeIds or organizationIds");
        }

        final List<Activity> all = new ArrayList<>();

        if (activityTypeId != null) {
            checkFound(activityTypeId);
            all.addAll(activityDao.getByActivityTypeId(activityTypeId));
        } else {
            checkFound(organizationId);
            all.addAll(activityDao.getByOrganizationId(organizationId));
        }


        //TODO: I'm pretty sure this should be done on the Service implementation, but I'm in the bus and lazy and sleepy
        return filterActivities(all, fromDate, toDate, offset, limit);
    }

    // The hardest part is to get the name right!
    @ApiMethod(name = "activities.listQuery", path = "activities-list", httpMethod = ApiMethod.HttpMethod.POST)
    public List<Activity> getActivitiesByIds(User caller,
                                             JasListQueryActivitiesRequest request) throws BadRequestException, NotFoundException {

        if (request == null) {
            throw new BadRequestException("Must choose one: activityTypeIds or organizationIds");
        } else if (request.getActivityTypeIds().isEmpty() && request.getOrganizationIds().isEmpty()) {
            throw new BadRequestException("Must choose one: activityTypeIds or organizationIds");
        } else if (!request.getActivityTypeIds().isEmpty() && !request.getOrganizationIds().isEmpty()) {
            throw new BadRequestException("Must choose one: activityTypeIds or organizationIds");
        }

        final List<Activity> all = new ArrayList<>();

        if (!request.getActivityTypeIds().isEmpty()) {
            for (Key activityTypeId : request.getActivityTypeIds()) {
                checkFound(activityTypeId);
                all.addAll(activityDao.getByActivityTypeId(activityTypeId));
            }
        } else if (!request.getOrganizationIds().isEmpty()) {
            for (Key organizationId : request.getOrganizationIds()) {
                checkFound(organizationId);
                all.addAll(activityDao.getByOrganizationId(organizationId));
            }
        }

        return filterActivities(all, request.getFromDate(), request.getToDate(), request.getOffset(), request.getLimit());
    }

    @ApiMethod(name = "activities.get", path = "activities/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Activity getActivity(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeLoggedIn(caller);
        checkFound(id);
        try {
            return activityDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.update", path = "activities/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Activity updateActivity(User caller, @Named("id") Key id, Activity activity) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(activity.getId()));
        checkFound(id);
        activity.setId(id);

        try {
            // In case client does not set the Name field we force the set here
            if (activity.getName() == null) {
                ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
                activity.setName(activityType.getName());
            }
            return ActivityServiceFactory.getActivityService().updateActivity(activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Activity not found");
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.add", path = "activities", httpMethod = ApiMethod.HttpMethod.POST)
    public List<Activity> addActivity(User caller, JasAddActivityRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request.getActivity());
        checkFound(request.getActivity().getActivityTypeRef().getKey());
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(request.getActivity().getActivityTypeRef().getKey()));

        try {
            ActivityService activityService = ActivityServiceFactory.getActivityService();
            ActivityType activityType = activityTypeDao.get(request.getActivity().getActivityTypeRef().getKey());
            // In case client does not set the Name field we force the set here
            if (request.getActivity().getName() == null) {
                request.getActivity().setName(activityType.getName());
            }
            List<Key> keys = activityService.addActivity(activityType, request.getActivity(), request.getRepeatDetails());
            List<Activity> result = new ArrayList<>();
            for (Key key : keys) {
                result.add(activityDao.get(key));
            }
            return result;
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.remove", path = "activities/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivity(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(id));
        checkFound(id);
        try {
            ActivityService activityService = ActivityServiceFactory.getActivityService();
            Activity activity = activityDao.get(id);
            // TODO: Possible race condition? Need to protect
            List<Subscription> subscriptions = subscriptionDao.getByActivity(id);
            if (!subscriptions.isEmpty()) {
                throw new BadRequestException("Activity has subscriptions");
            }
            if (!activityPackageActivityDao.getByActivityId(id).isEmpty()) {
                throw new BadRequestException("Activity is linked to Activity Package");
            }
            activityService.removeActivity(activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activitySubscriptions.add", path = "activity-subscriptions", httpMethod = ApiMethod.HttpMethod.POST)
    public Subscription addSubscription(User caller, @Named("userId") Key userId, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        mustBeSameUserOrAdminOrOrgMember(caller, userId, OrgMemberChecker.createFromActivityId(activityId));
        checkFound(userId);
        checkFound(activityId);
        try {
            com.jasify.schedule.appengine.model.users.User user = userDao.get(userId);
            Activity activity = activityDao.get(activityId);
            return ActivityServiceFactory.getActivityService().subscribe(user, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        } catch (OperationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activitySubscriptions.query", path = "activity-subscriptions", httpMethod = ApiMethod.HttpMethod.GET)
    public Subscription getSubscription(User caller, @Named("userId") Key userId, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        mustBeSameUserOrAdminOrOrgMember(caller, userId, OrgMemberChecker.createFromActivityId(activityId));
        checkFound(userId);
        checkFound(activityId);

        List<Subscription> subscriptions = subscriptionDao.getByActivity(activityId);
        for (Subscription subscription : subscriptions) {
            if (userId.equals(subscription.getUserRef().getKey())) {
                return subscription;
            }
        }

        throw new NotFoundException("No such subscription");
    }

    @ApiMethod(name = "activitySubscriptions.subscribers", path = "activities/{id}/subscribers", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Subscription> getSubscriptions(User caller, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(activityId));
        checkFound(activityId);
        return subscriptionDao.getByActivity(activityId);

    }

    @ApiMethod(name = "activitySubscriptions.cancel", path = "activities/{id}/subscribers", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void cancelSubscription(User caller, @Named("subscriptionId") Key subscriptionId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromSubscriptionId(subscriptionId));
        checkFound(subscriptionId);
        try {
            ActivityServiceFactory.getActivityService().cancelSubscription(subscriptionId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.query", path = "activity-packages", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityPackage> getActivityPackages(User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId);
        return activityPackageDao.getByOrganization(organizationId);
    }

    @ApiMethod(name = "activityPackages.get", path = "activity-packages/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ActivityPackage getActivityPackage(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id);
        try {
            return activityPackageDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.update", path = "activity-packages/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public ActivityPackage updateActivityPackage(User caller, @Named("id") Key id, JasActivityPackageRequest request) throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(id));
        checkFound(id);
        ActivityPackage activityPackage = Preconditions.checkNotNull(request.getActivityPackage(), "request.ActivityPackage is NULL");
        List<Activity> activities = Preconditions.checkNotNull(request.getActivities(), "request.Activities is NULL");
        activityPackage.setId(id);
        try {
            return ActivityServiceFactory.getActivityService().updateActivityPackage(activityPackage, activities);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Not found");
        }
    }

    @ApiMethod(name = "activityPackages.add", path = "activity-packages", httpMethod = ApiMethod.HttpMethod.POST)
    public ActivityPackage addActivityPackage(User caller, JasActivityPackageRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        ActivityPackage activityPackage = checkFound(request.getActivityPackage(), "request.activityPackage == NULL");
        Key organizationId = checkFound(activityPackage.getOrganizationRef().getKey(), "request.activityPackage.organization == NULL");
        List<Activity> activities = checkFound(request.getActivities(), "request.activities == NULL");

        if (activities.isEmpty()) {
            throw new BadRequestException("request.activities.isEmpty");
        }

        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(organizationId));

        try {
            organizationDao.get(activityPackage.getOrganizationRef().getKey());
            Key id = ActivityServiceFactory.getActivityService().addActivityPackage(activityPackage, activities);
            return activityPackageDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @ApiMethod(name = "activityPackages.remove", path = "activity-packages/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityPackage(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        checkFound(id);
        try {
            ActivityServiceFactory.getActivityService().removeActivityPackage(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (OperationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.getActivities", path = "activity-packages-activity/{activityPackageId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivityPackageActivities(User caller, @Named("activityPackageId") Key activityPackageId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activityPackageId);
        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            return activityPackage.getActivities();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    @ApiMethod(name = "activityPackages.addActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addActivityToActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, @Named("activityId") Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));
        checkFound(activityPackageId);
        checkFound(activityId);
        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            Activity activity = activityDao.get(activityId);
            ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }

    @ApiMethod(name = "activityPackages.removeActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityFromActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, @Named("activityId") Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));
        checkFound(activityPackageId);
        checkFound(activityId);
        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            Activity activity = activityDao.get(activityId);
            ActivityServiceFactory.getActivityService().removeActivityFromActivityPackage(activityPackage, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("User not found");
        }
    }
}
