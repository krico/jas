package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.dao.users.UserDao;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageActivityMeta;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.consistency.ConsistencyGuard;
import com.jasify.schedule.appengine.model.consistency.InconsistentModelStateException;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.dm.JasActivityPackageRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityRequest;
import com.jasify.schedule.appengine.spi.dm.JasAddActivityTypeRequest;
import com.jasify.schedule.appengine.spi.dm.JasListQueryActivitiesRequest;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slim3.datastore.Datastore;

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
    private final RepeatDetailsDao repeatDetailsDao = new RepeatDetailsDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();
    private final UserDao userDao = new UserDao();

    @ApiMethod(name = "activityTypes.query", path = "activity-types", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityType> getActivityTypes(@SuppressWarnings("unused") User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId, "organizationId == null");
        return activityTypeDao.getByOrganization(organizationId);
    }

    @ApiMethod(name = "activityTypes.get", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ActivityType getActivityType(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        try {
            return activityTypeDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.update", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public ActivityType updateActivityType(User caller, @Named("id") final Key id, final ActivityType activityType) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(id, "id == null");
        checkFound(activityType, "activityType == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        activityType.setId(id);

        try {
            return TransactionOperator.execute(new ModelOperation<ActivityType>() {
                @Override
                public ActivityType execute(Transaction tx) throws ModelException {
                    ActivityType dbActivityType = activityTypeDao.get(id);
                    BeanUtil.copyPropertiesExcluding(dbActivityType, activityType, "created", "modified", "id", "organizationRef");
                    activityTypeDao.save(dbActivityType, dbActivityType.getOrganizationRef().getKey());
                    tx.commit();
                    return dbActivityType;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (FieldValueException | UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.add", path = "activity-types", httpMethod = ApiMethod.HttpMethod.POST)
    public ActivityType addActivityType(User caller, JasAddActivityTypeRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request, "request == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromOrganizationId(request.getOrganizationId()));
        final ActivityType activityType = checkFound(request.getActivityType(), "request.activityType == null");
        final Key organizationId = checkFound(request.getOrganizationId(), "request.organizationId == null");

        try {
            return TransactionOperator.execute(new ModelOperation<ActivityType>() {
                @Override
                public ActivityType execute(Transaction tx) throws ModelException {
                    organizationDao.get(organizationId);
                    activityTypeDao.save(activityType, organizationId);
                    tx.commit();
                    return activityType;
                }
            });
        } catch (FieldValueException | UniqueConstraintException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityTypes.remove", path = "activity-types/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityType(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, InternalServerErrorException, BadRequestException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));

        try {
            ConsistencyGuard.beforeDelete(ActivityType.class, id);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    activityTypeDao.get(id); //Throws not found if this activityType doesn't exist
                    activityTypeDao.delete(id);
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

    private List<Activity> filterByDate(List<Activity> activities, final Date fromDate, final Date toDate) {
        if (fromDate == null && toDate == null) {
            return activities;
        }

        ArrayList<Activity> result = new ArrayList<>();
        result.addAll(Collections2.filter(activities, new Predicate<Activity>() {
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
        return result;
    }

    private List<Activity> filterActivities(List<Activity> activities, Date fromDate,
                                            Date toDate,
                                            Integer offset,
                                            Integer limit) {
        if (activities.isEmpty()) {
            return Collections.emptyList();
        }

        List<Activity> filtered = filterByDate(activities, fromDate, toDate);

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
    public List<Activity> getActivities(@SuppressWarnings("unused") User caller,
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
        checkFound(request, "request == null");
        if (request.getActivityTypeIds().isEmpty() && request.getOrganizationIds().isEmpty()) {
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
        checkFound(id, "id == null");
        try {
            return activityDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.update", path = "activities/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public Activity updateActivity(User caller, @Named("id") final Key id, final Activity activity) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activity, "activity == null");
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(id));
        activity.setId(id);

        try {
            return TransactionOperator.execute(new ModelOperation<Activity>() {
                @Override
                public Activity execute(Transaction tx) throws ModelException {
                    Activity dbActivity = activityDao.get(id);
                    BeanUtil.copyPropertiesExcluding(dbActivity, activity, "created", "modified", "id", "activityTypeRef");
                    activityDao.save(dbActivity, dbActivity.getActivityTypeRef().getKey());
                    tx.commit();
                    return dbActivity;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Activity not found");
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.add", path = "activities", httpMethod = ApiMethod.HttpMethod.POST)
    public List<Activity> addActivity(User caller, final JasAddActivityRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request, "request == null");
        checkFound(request.getActivity(), "request.activity == null");
        final Key activityTypeId = checkFound(request.getActivity().getActivityTypeRef().getKey(), "request.activity.activityType == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(activityTypeId));

        try {
            return TransactionOperator.execute(new ModelOperation<List<Activity>>() {
                @Override
                public List<Activity> execute(Transaction tx) throws ModelException {
                    RepeatDetails repeatDetails = request.getRepeatDetails();

                    ActivityType activityType = activityTypeDao.get(activityTypeId);
                    if (repeatDetails != null) {
                        repeatDetailsDao.save(repeatDetails, activityTypeId);
                    }

                    ActivityCreator activityCreator = new ActivityCreator(request.getActivity(), repeatDetails, activityType);
                    List<Activity> activities = activityCreator.create();
                    activityDao.save(activities, activityTypeId);
                    tx.commit();
                    return activities;
//                    return Lists.transform(keys, new Function<Key, Activity>() {
//                        @javax.annotation.Nullable
//                        @Override
//                        public Activity apply(Key input) {
//                            return Datastore.get(Activity.class, input);
//                        }
//                    });
                }
            });

//
//            ActivityService activityService = ActivityServiceFactory.getActivityService();
//            ActivityType activityType = activityTypeDao.get(request.getActivity().getActivityTypeRef().getKey());
//
//            List<Key> keys = activityService.addActivity(activityType, request.getActivity(), request.getRepeatDetails());
//            List<Activity> result = new ArrayList<>();
//            for (Key key : keys) {
//                result.add(activityDao.get(key));
//            }
//            return result;
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activities.remove", path = "activities/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivity(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(id));
        try {
            ConsistencyGuard.beforeDelete(Activity.class, id);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    activityDao.get(id); //Throws not found if this activityType doesn't exist
                    activityDao.delete(id);
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

    @ApiMethod(name = "activitySubscriptions.add", path = "activity-subscriptions", httpMethod = ApiMethod.HttpMethod.POST)
    public Subscription addSubscription(User caller, @Named("userId") Key userId, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException {
        checkFound(userId, "userId == null");
        checkFound(activityId, "activityId == null");
        mustBeSameUserOrAdminOrOrgMember(caller, userId, OrgMemberChecker.createFromActivityId(activityId));
        try {
            com.jasify.schedule.appengine.model.users.User user = userDao.get(userId);
            Activity activity = activityDao.get(activityId);
            return ActivityServiceFactory.getActivityService().subscribe(user, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (OperationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiMethod(name = "activitySubscriptions.query", path = "activity-subscriptions", httpMethod = ApiMethod.HttpMethod.GET)
    public Subscription getSubscription(User caller, @Named("userId") Key userId, @Named("activityId") Key activityId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkFound(userId, "userId == null");
        checkFound(activityId, "activityId == null");
        mustBeSameUserOrAdminOrOrgMember(caller, userId, OrgMemberChecker.createFromActivityId(activityId));

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
        checkFound(activityId, "activityId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityId(activityId));
        return subscriptionDao.getByActivity(activityId);

    }

    @ApiMethod(name = "activitySubscriptions.cancel", path = "activities/{id}/subscribers", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void cancelSubscription(User caller, @Named("subscriptionId") Key subscriptionId) throws UnauthorizedException, ForbiddenException, NotFoundException {
        checkFound(subscriptionId, "subscriptionId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromSubscriptionId(subscriptionId));
        try {
            ActivityServiceFactory.getActivityService().cancelSubscription(subscriptionId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.query", path = "activity-packages", httpMethod = ApiMethod.HttpMethod.GET)
    public List<ActivityPackage> getActivityPackages(@SuppressWarnings("unused") User caller, @Named("organizationId") Key organizationId) throws NotFoundException {
        checkFound(organizationId, "organizationId == null");
        return activityPackageDao.getByOrganization(organizationId);
    }

    @ApiMethod(name = "activityPackages.get", path = "activity-packages/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public ActivityPackage getActivityPackage(@SuppressWarnings("unused") User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        try {
            return activityPackageDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.update", path = "activity-packages/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public ActivityPackage updateActivityPackage(User caller, @Named("id") Key id, JasActivityPackageRequest request) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        checkFound(request, "request == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(id));
        ActivityPackage activityPackage = checkFound(request.getActivityPackage(), "request.activityPackage == null");
        List<Activity> activities = checkFound(request.getActivities(), "request.activities == null");
        activityPackage.setId(id);
        try {
            return ActivityServiceFactory.getActivityService().updateActivityPackage(activityPackage, activities);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.add", path = "activity-packages", httpMethod = ApiMethod.HttpMethod.POST)
    public ActivityPackage addActivityPackage(User caller, JasActivityPackageRequest request) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException {
        checkFound(request, "request == null");
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
    public void removeActivityPackage(User caller, @Named("id") final Key id) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        checkFound(id, "id == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityTypeId(id));
        try {
            ConsistencyGuard.beforeDelete(ActivityPackage.class, id);

            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    ActivityPackage activityPackage = activityPackageDao.get(id);
                    // Delete all the attached activities
                    List<Key> toDelete = new ArrayList<>();
                    toDelete.add(id);
                    ActivityPackageActivityMeta activityPackageActivityMeta = ActivityPackageActivityMeta.get();
                    toDelete.addAll(Datastore
                            .query(tx, activityPackageActivityMeta, activityPackage.getOrganizationRef().getKey())
                            .filter(activityPackageActivityMeta.activityPackageRef.equal(id))
                            .asKeyList());
                    Datastore.delete(tx, toDelete);
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

    @ApiMethod(name = "activityPackages.getActivities", path = "activity-packages-activity/{activityPackageId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivityPackageActivities(@SuppressWarnings("unused") User caller, @Named("activityPackageId") Key activityPackageId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activityPackageId, "activityPackageId == null");
        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            return activityPackage.getActivities();
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.addActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addActivityToActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, @Named("activityId") Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activityPackageId, "activityPackageId == null");
        checkFound(activityId, "activityId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));

        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            Activity activity = activityDao.get(activityId);
            ActivityServiceFactory.getActivityService().addActivityToActivityPackage(activityPackage, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.removeActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityFromActivityPackage(User caller, @Named("activityPackageId") Key activityPackageId, @Named("activityId") Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activityPackageId, "activityPackageId == null");
        checkFound(activityId, "activityId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));

        try {
            ActivityPackage activityPackage = activityPackageDao.get(activityPackageId);
            Activity activity = activityDao.get(activityId);
            ActivityServiceFactory.getActivityService().removeActivityFromActivityPackage(activityPackage, activity);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
