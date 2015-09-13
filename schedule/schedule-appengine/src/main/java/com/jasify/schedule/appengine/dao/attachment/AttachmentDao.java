package com.jasify.schedule.appengine.dao.attachment;

import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.meta.attachment.AttachmentMeta;
import com.jasify.schedule.appengine.model.attachment.Attachment;

import java.util.concurrent.TimeUnit;

/**
 * @author krico
 * @since 31/07/15.
 */
public class AttachmentDao extends BaseCachingDao<Attachment> {
    /**
     * We cache attachments for a short time to avoid filling up memcache
     */
    private static final int EXPIRY_SECONDS = (int) TimeUnit.MINUTES.toSeconds(5);

    public AttachmentDao() {
        super(AttachmentMeta.get(), EXPIRY_SECONDS);
    }
}
