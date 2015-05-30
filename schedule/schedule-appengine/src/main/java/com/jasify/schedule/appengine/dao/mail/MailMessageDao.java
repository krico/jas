package com.jasify.schedule.appengine.dao.mail;

import com.jasify.schedule.appengine.dao.BaseDao;
import com.jasify.schedule.appengine.meta.mail.MailMessageMeta;
import com.jasify.schedule.appengine.model.mail.MailMessage;

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
