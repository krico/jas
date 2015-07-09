package com.jasify.schedule.appengine.model.consistency;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.common.ActivityTypeDao;
import com.jasify.schedule.appengine.dao.common.OrganizationMemberDao;
import com.jasify.schedule.appengine.model.common.Organization;

import java.util.List;

/**
 * @author krico
 * @since 17/06/15.
 */
public class OrganizationConsistency implements EntityConsistency<Organization> {

    @BeforeDelete(entityClass = Organization.class)
    public void ensureOrganizationHasNoMembers(Key id) throws InconsistentModelStateException {
        List<Key> memberKeys = new OrganizationMemberDao().byOrganizationIdAsKeys(id);
        if (!memberKeys.isEmpty()) {
            throw new InconsistentModelStateException("Cannot delete organization with members! " +
                    "id=" + id + " (" + memberKeys.size() + " members).");
        }
    }

    @BeforeDelete(entityClass = Organization.class)
    public void ensureOrganizationHasNoActivityTypes(Key id) throws InconsistentModelStateException {
        List<Key> activityTypeKeys = new ActivityTypeDao().getKeysByOrganization(id);
        if (!activityTypeKeys.isEmpty()) {
            throw new InconsistentModelStateException("Cannot delete organization with activity types! " +
                    "id=" + id + " (" + activityTypeKeys.size() + " activity types).");
        }
    }
}
