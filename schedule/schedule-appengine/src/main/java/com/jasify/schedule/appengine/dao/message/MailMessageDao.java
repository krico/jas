package com.jasify.schedule.appengine.dao.message;

import com.jasify.schedule.appengine.dao.BaseDao;
import com.jasify.schedule.appengine.meta.message.MailMessageMeta;
import com.jasify.schedule.appengine.model.message.MailMessage;

/**
 * @author krico
 * @since 28/05/15.
 */
public class MailMessageDao extends BaseDao<MailMessage> {
    public static final MailMessageDao INSTANCE = new MailMessageDao();

    public MailMessageDao() {
        super(MailMessageMeta.get());
    }
}
