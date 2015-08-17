package com.jasify.schedule.appengine.mail;

import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author krico
 * @since 17/08/15.
 */
public final class MailDebug {
    public static final String JASIFY_EMAIL_DEBUG_PROP = "jasify.emailDebug";
    private static final Logger log = LoggerFactory.getLogger(MailDebug.class);

    private MailDebug() {
    }

    static void writeDebug(Message message) {
        if (!EnvironmentUtil.isDevelopment()) return;
        if (!Boolean.parseBoolean(System.getProperty(JASIFY_EMAIL_DEBUG_PROP))) {
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String filename = sdf.format(new Date()) + ".eml";
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            message.writeTo(os);
            Attachment attachment = AttachmentHelper.create(filename, "message/rfc822", os.toByteArray());
            new AttachmentDao().save(attachment);
            log.info("E-mail available at: {}", AttachmentHelper.makeDownloadUrl(attachment));
        } catch (Exception e) {
            log.warn("Failed to write debug email", e);
        }
    }
}
