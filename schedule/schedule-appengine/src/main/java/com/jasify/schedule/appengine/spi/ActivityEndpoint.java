package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.model.*;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.consistency.ConsistencyGuard;
import com.jasify.schedule.appengine.model.consistency.InconsistentModelStateException;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.model.history.HistoryHelper;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.*;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.BeanUtil;

import java.util.*;

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
                JasHistoryTransformer.class,
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
                public Void execute(Transaction tx) throws EntityNotFoundException {
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

        return new ActivityFilter().filter(all, fromDate, toDate, offset, limit);
    }

    // The hardest part is to get the name right!
    @ApiMethod(name = "activities.listQuery", path = "activities-list", httpMethod = ApiMethod.HttpMethod.POST)
    public List<Activity> getActivitiesByIds(@SuppressWarnings("unused") User caller,
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

        return new ActivityFilter().filter(all, request.getFromDate(), request.getToDate(), request.getOffset(), request.getLimit());
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
                    activityDao.save(dbActivity);
                    tx.commit();
                    return dbActivity;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Activity not found");
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
                        Key organizationId = activityType.getOrganizationRef().getKey();
                        repeatDetailsDao.save(repeatDetails, organizationId);
                    }

                    ActivityCreator activityCreator = new ActivityCreator(request.getActivity(), repeatDetails, activityType);
                    List<Activity> activities = activityCreator.create();
                    activityDao.save(activities, activityTypeId);
                    tx.commit();
                    return activities;
                }
            });
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
            Subscription subscription = ActivityServiceFactory.getActivityService().subscribe(userId, activityId);
            HistoryHelper.addSubscriptionCreated(subscription.getId());
            return subscription;
        } catch (EntityNotFoundException e) {
            HistoryHelper.addSubscriptionCreationFailed(userId, activityId);
            throw new NotFoundException(e.getMessage());
        } catch (OperationException | FieldValueException e) {
            HistoryHelper.addSubscriptionCreationFailed(userId, activityId);
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

    /**
     * Get list of subscriptions, activities, and payment details for a userId that fall within the fromDate and toDate range inclusive.
     * Note that userId is required which means that Admin can not view all subscriptions and orgAdmin can not view all organization related
     * subscriptions.
     *
     * @param caller:   Filter criteria. Admin user or caller matching userId can view all subscriptions related to userId.
     *                  OrgAdmin can view subscriptions related to userId that also match orgAdmins organizations.
     * @param userId:   Filter criteria. Only show subscriptions that match this userId. Null not allowed.
     * @param fromDate: Filter criteria. Only show subscriptions that occur on or after fromDate. Null value means ignore.
     * @param toDate:   Filter criteria. Only show subscriptions that occur on or before toDate. Null value means ignore.
     * @return JasUserSubscription list of subscriptions, activities, and payment details.
     * @throws UnauthorizedException
     * @throws NotFoundException
     */
    @ApiMethod(name = "activitySubscriptions.getForUser", path = "user-subscriptions", httpMethod = ApiMethod.HttpMethod.GET)
    public List<JasUserSubscription> getSubscribedActivities(User caller,
                                                             @Named("userId") Key userId,
                                                             @Nullable @Named("fromDate") final Date fromDate,
                                                             @Nullable @Named("toDate") final Date toDate) throws UnauthorizedException, NotFoundException {
        JasifyEndpointUser endpointUser = mustBeLoggedIn(caller);
        checkFound(userId, "userId == null");

        boolean showAll = endpointUser.getUserId() == userId.getId() || endpointUser.isAdmin();
        boolean showOrganization = !showAll && endpointUser.isOrgMember();

        if (!showAll && !showOrganization) {
            return Collections.emptyList();
        }

        try {
            Set<Key> relatedOrganizations = new HashSet<>();

            if (showOrganization) {
                // TODO: If I got the keys I would not need to use organizationDao to get the organization later
                relatedOrganizations.addAll(organizationDao.getByMemberUserIdAsKeys(endpointUser.getUserId()));
            }

            List<JasUserSubscription> result = new ArrayList<>();
            List<Subscription> subscriptions = subscriptionDao.getByUser(userId);
            for (Subscription subscription : subscriptions) {
                Activity activity = activityDao.get(subscription.getActivityRef().getKey());

                // Only add if activity falls within the filtered time
                boolean add = ActivityFilter.filtered(activity, fromDate, toDate);
                // If caller is orgAdmin check if this activity belongs to callers organisation
                if (add && showOrganization) {
                    ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
                    add = relatedOrganizations.contains(activityType.getOrganizationRef().getKey());
                }

                if (add) {
                    JasUserSubscription userSubscription = new JasUserSubscription(subscription);
                    Key transferId = subscription.getTransferRef().getKey();
                    // This will only be set if the subscription was paid for in some way
                    userSubscription.setPaid(transferId != null);
                    result.add(userSubscription);
                }
            }
            return result;
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activitySubscriptions.cancel", path = "activities/{id}/subscribers", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void cancelSubscription(User caller, @Named("subscriptionId") final Key subscriptionId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException, InternalServerErrorException {
        checkFound(subscriptionId, "subscriptionId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromSubscriptionId(subscriptionId));
        try {
            History history = HistoryHelper.createTacticalSubscriptionCancelled(subscriptionId);
            ActivityServiceFactory.getActivityService().cancelSubscription(subscriptionId);
            HistoryHelper.addTacticalSubscriptionCancelled(history);
        } catch (EntityNotFoundException e) {
            HistoryHelper.addSubscriptionCancellationFailed(subscriptionId);
            throw new NotFoundException(e.getMessage());
        } catch (FieldValueException e) {
            HistoryHelper.addSubscriptionCancellationFailed(subscriptionId);
            throw new BadRequestException(e.getMessage());
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
    public ActivityPackage updateActivityPackage(User caller, @Named("id") Key id, JasActivityPackageRequest request) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(id, "id == null");
        checkFound(request, "request == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(id));
        ActivityPackage activityPackage = checkFound(request.getActivityPackage(), "request.activityPackage == null");
        List<Activity> activities = checkFound(request.getActivities(), "request.activities == null");
        activityPackage.setId(id);
        try {
            activityPackageDao.get(id);
            return ActivityServiceFactory.getActivityService().updateActivityPackage(activityPackage, activities);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (FieldValueException e) {
            throw new BadRequestException(e.getMessage());
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
                    activityPackageDao.get(id);
                    // Delete all the attached activities
                    List<Key> toDelete = activityPackageActivityDao.getKeysByActivityPackageId(id);
                    activityPackageActivityDao.delete(toDelete);
                    activityPackageDao.delete(id);
                    tx.commit();
                    return null;
                }
            });
        } catch (InconsistentModelStateException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ModelException me) {
            throw new InternalServerErrorException(me.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.getActivities", path = "activity-packages-activity/{activityPackageId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Activity> getActivityPackageActivities(@SuppressWarnings("unused") User caller, @Named("activityPackageId") Key activityPackageId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException {
        checkFound(activityPackageId, "activityPackageId == null");
        try {
            List<ActivityPackageActivity> activityPackageActivities = activityPackageActivityDao.getByActivityPackageId(activityPackageId);
            List<Activity> activities = new ArrayList<>();
            for (ActivityPackageActivity activityPackageActivity : activityPackageActivities) {
                Activity activity = activityDao.get(activityPackageActivity.getActivityRef().getKey());
                activities.add(activity);
            }

            Collections.sort(activities, new Comparator<Activity>() {
                @Override
                public int compare(Activity o1, Activity o2) {
                    Date start1 = o1.getStart();
                    if (start1 == null) {
                        start1 = o1.getCreated() == null ? new Date(Long.MAX_VALUE) : o1.getCreated();
                    }
                    Date start2 = o2.getStart();
                    if (start2 == null) {
                        start2 = o2.getCreated() == null ? new Date(Long.MAX_VALUE) : o2.getCreated();
                    }

                    return start1.compareTo(start2);
                }
            });
            return activities;
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.addActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.POST)
    public void addActivityToActivityPackage(User caller, @Named("activityPackageId") final Key activityPackageId, @Named("activityId") final Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        checkFound(activityPackageId, "activityPackageId == null");
        checkFound(activityId, "activityId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));

        try {
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws ModelException {
                    activityDao.get(activityId);
                    activityPackageDao.get(activityPackageId);
                    ActivityPackageActivity activityPackageActivity = activityPackageActivityDao.getByActivityPackageIdAndActivityId(activityPackageId, activityId);

                    if (activityPackageActivity != null) {
                        throw new UniqueConstraintException("ActivityPackage " + activityPackageId + " already contains Activity " + activityId);
                    }

                    activityPackageActivity = new ActivityPackageActivity();
                    activityPackageActivity.getActivityRef().setKey(activityId);
                    activityPackageActivityDao.save(activityPackageActivity, activityPackageId);
                    tx.commit();
                    return null;
                }
            });
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ModelException me) {
            throw new InternalServerErrorException(me.getMessage());
        }
    }

    @ApiMethod(name = "activityPackages.removeActivity", path = "activity-packages-activity/{activityPackageId}/{activityId}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeActivityFromActivityPackage(User caller, @Named("activityPackageId") final Key activityPackageId, @Named("activityId") final Key activityId) throws NotFoundException, UnauthorizedException, ForbiddenException, BadRequestException, InternalServerErrorException {
        checkFound(activityPackageId, "activityPackageId == null");
        checkFound(activityId, "activityId == null");
        mustBeAdminOrOrgMember(caller, OrgMemberChecker.createFromActivityPackageId(activityPackageId));

        try {
            activityPackageDao.get(activityPackageId);
            activityDao.get(activityId);
            final Key activityPackageActivityId = activityPackageActivityDao.getKeyByActivityPackageIdAndActivityId(activityPackageId, activityId);
            ConsistencyGuard.beforeDelete(ActivityPackageActivity.class, activityPackageActivityId);
            TransactionOperator.execute(new ModelOperation<Void>() {
                @Override
                public Void execute(Transaction tx) throws EntityNotFoundException, InconsistentModelStateException {
                    activityPackageActivityDao.delete(activityPackageActivityId);
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
