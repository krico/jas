package com.jasify.schedule.appengine.dao.common;

import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.common.GroupMeta;
import com.jasify.schedule.appengine.model.common.Group;

/**
 * @author krico
 * @since 14/06/15.
 */
public class GroupDao extends BaseCachingDao<Group> {
    public GroupDao() {
        super(GroupMeta.get());
    }
}
