package com.jasify.schedule.appengine.spi;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.users.User;

import java.util.List;

/**
 * @author krico
 * @since 11/06/15.
 */
abstract class OrgMemberChecker {
    private static final OrgMemberChecker FALSE = new OrgMemberChecker() {
        @Override
        Organization getOrganization() throws EntityNotFoundException {
            return null;
        }

        @Override
        public boolean isOrgMember(long userId) throws EntityNotFoundException {
            return false;
        }
    };
    private static final OrgMemberChecker NOT_FOUND = new OrgMemberChecker() {
        @Override
        Organization getOrganization() throws EntityNotFoundException {
            throw new EntityNotFoundException();
        }
    };
    private final OrganizationDao organizationDao = new OrganizationDao();
    private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
    private final ActivityDao activityDao = new ActivityDao();
    private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
    private final SubscriptionDao subscriptionDao = new SubscriptionDao();

    protected Key id;
    private static final ThreadLocal<OrgMemberChecker> ACTIVITY = new ThreadLocal<OrgMemberChecker>() {
        @Override
        protected OrgMemberChecker initialValue() {
            return new OrgMemberChecker() {
                @Override
                Organization getOrganization() throws EntityNotFoundException {
                    return getOrganizationFromActivity(id);
                }
            };
        }
    };

    private static final ThreadLocal<OrgMemberChecker> ACTIVITY_TYPE = new ThreadLocal<OrgMemberChecker>() {
        @Override
        protected OrgMemberChecker initialValue() {
            return new OrgMemberChecker() {
                @Override
                Organization getOrganization() throws EntityNotFoundException {
                    return getOrganizationFromActivityType(id);
                }
            };
        }
    };

    private static final ThreadLocal<OrgMemberChecker> ACTIVITY_PACKAGE = new ThreadLocal<OrgMemberChecker>() {
        @Override
        protected OrgMemberChecker initialValue() {
            return new OrgMemberChecker() {
                @Override
                Organization getOrganization() throws EntityNotFoundException {
                    return getOrganizationFromActivityPackage(id);
                }
            };
        }
    };

    private static final ThreadLocal<OrgMemberChecker> SUBSCRIPTION = new ThreadLocal<OrgMemberChecker>() {
        @Override
        protected OrgMemberChecker initialValue() {
            return new OrgMemberChecker() {
                @Override
                Organization getOrganization() throws EntityNotFoundException {
                    return getOrganizationFromASubscription(id);
                }
            };
        }
    };

    private static final ThreadLocal<OrgMemberChecker> ORGANIZATION = new ThreadLocal<OrgMemberChecker>() {
        @Override
        protected OrgMemberChecker initialValue() {
            return new OrgMemberChecker() {
                @Override
                Organization getOrganization() throws EntityNotFoundException {
                    return getOrganization(id);
                }
            };
        }
    };

    private OrgMemberChecker() {
    }

    public static OrgMemberChecker createFromActivityId(Key id) {
        return ACTIVITY.get().withId(id);
    }

    public static OrgMemberChecker createFromActivityTypeId(Key id) {
        return ACTIVITY_TYPE.get().withId(id);
    }

    public static OrgMemberChecker createFromActivityPackageId(Key id) {
        return ACTIVITY_PACKAGE.get().withId(id);
    }

    public static OrgMemberChecker createFromSubscriptionId(Key id) {
        return SUBSCRIPTION.get().withId(id);
    }

    public static OrgMemberChecker createFromOrganizationId(Key id) {
        return ORGANIZATION.get().withId(id);
    }

    public static OrgMemberChecker createFalse() {
        return FALSE;
    }

    public static OrgMemberChecker createNotFound() {
        return NOT_FOUND;
    }

    public OrgMemberChecker withId(Key id) {
        this.id = id;
        return this;
    }

    public boolean isOrgMember(long userId) throws EntityNotFoundException {
        Organization organization = getOrganization();
        List<User> users = organizationDao.getUsersOfOrganization(organization.getId());
        for (User user : users) {
            if (user.getId().getId() == userId) return true;
        }
        return false;
    }

    protected Organization getOrganization(Key id) throws EntityNotFoundException {
        if (id == null) return null;
        return organizationDao.get(id);
    }

    protected ActivityType getActivityType(Key id) throws EntityNotFoundException {
        if (id == null) return null;
        return activityTypeDao.get(id);
    }

    protected Activity getActivity(Key id) throws EntityNotFoundException {
        if (id == null) return null;
        return activityDao.get(id);
    }

    protected ActivityPackage getActivityPackage(Key id) throws EntityNotFoundException {
        if (id == null) return null;
        return activityPackageDao.get(id);
    }

    protected Subscription getSubscription(Key id) throws EntityNotFoundException {
        if (id == null) return null;
        return subscriptionDao.get(id);
    }

    protected Organization getOrganizationFromActivityType(Key id) throws EntityNotFoundException {
        ActivityType activityType = getActivityType(id);
        if (activityType != null) {
            return getOrganization(activityType.getOrganizationRef().getKey());
        }
        return null;
    }

    protected Organization getOrganizationFromActivity(Key id) throws EntityNotFoundException {
        Activity activity = getActivity(id);
        if (activity != null) {
            return getOrganizationFromActivityType(activity.getActivityTypeRef().getKey());
        }
        return null;
    }

    protected Organization getOrganizationFromActivityPackage(Key id) throws EntityNotFoundException {
        ActivityPackage activityPackage = getActivityPackage(id);
        if (activityPackage != null) {
            return getOrganizationFromActivityType(activityPackage.getOrganizationRef().getKey());
        }
        return null;
    }

    protected Organization getOrganizationFromASubscription(Key id) throws EntityNotFoundException {
        Subscription subscription = getSubscription(id);
        if (subscription != null) {
            return getOrganizationFromActivity(subscription.getActivityRef().getKey());
        }
        return null;
    }

    abstract Organization getOrganization() throws EntityNotFoundException;
}
