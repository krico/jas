package com.jasify.schedule.appengine.mail;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author krico
 * @since 26/11/14.
 */
public final class DefaultMailService implements MailService {
    public static final String DEFAULT_SENDER_NAME = "Jasify (Do Not Reply)";
    public static final String DEFAULT_SENDER = "DoNotReply@jasify-schedule.appspotmail.com";
    public static final String DEFAULT_OWNER = "github@cwa.to";
    private static final Logger log = LoggerFactory.getLogger(DefaultMailService.class);

    private final AtomicReference<StateEnum> initializationState = new AtomicReference<>(StateEnum.NEW);
    private Session session;
    private InternetAddress senderAddress;
    private InternetAddress[] applicationOwners;

    private DefaultMailService() {
    }

    public static MailService instance() {
        return Singleton.INSTANCE;
    }

    private boolean stateTransition(StateEnum from, StateEnum to) {
        if (!initializationState.compareAndSet(from, to)) {
            return false;
        }
        log.info("transition {} -> {}", from, to);
        return true;
    }

    private void initialize() {
        if (stateTransition(StateEnum.NEW, StateEnum.INITIALIZING)) {
            try {
                log.debug("Initializing {}...", getClass().getSimpleName());

                ApplicationData applicationData = ApplicationData.instance();

                String senderAddressKey = MailService.class.getName() + ".SenderAddress";
                String senderAddressString = applicationData.getPropertyWithDefaultValue(senderAddressKey, DEFAULT_SENDER);

                String senderAddressNameKey = MailService.class.getName() + ".SenderAddressName";
                String senderAddressNameString = applicationData.getPropertyWithDefaultValue(senderAddressNameKey, DEFAULT_SENDER_NAME);

                String applicationOwnersKey = MailService.class.getName() + ".ApplicationOwners";
                String applicationOwnersString = applicationData.getPropertyWithDefaultValue(applicationOwnersKey, DEFAULT_OWNER);

                for (String owner : StringUtils.split(applicationOwnersString, ',')) {
                    applicationOwners = ArrayUtils.add(applicationOwners, new InternetAddress(owner));
                }

                senderAddress = new InternetAddress(senderAddressString, senderAddressNameString);
                session = Session.getDefaultInstance(new Properties(), null);

                log.debug("Initialized {}", getClass().getSimpleName());
                Preconditions.checkState(stateTransition(StateEnum.INITIALIZING, StateEnum.INITIALIZED), "State changed during initialization");
            } catch (Exception e) {
                log.debug("Initialization failed!", e);
                if (!stateTransition(StateEnum.INITIALIZING, StateEnum.FAILED))
                    initializationState.set(StateEnum.FAILED);
            }
        }

        Preconditions.checkState(initializationState.get() == StateEnum.INITIALIZED,
                "MailService is in state: %s when it should be in state: %s", initializationState.get(), StateEnum.INITIALIZED);
    }

    @Override
    public boolean sendToApplicationOwners(String subject, String htmlBody, String textBody) {
        initialize();
        return send(senderAddress, applicationOwners, new InternetAddress[0], subject, htmlBody, textBody);
    }

    @Override
    public boolean send(String toEmail, String subject, String htmlBody, String textBody) throws Exception {
        return send(new InternetAddress(toEmail), subject, htmlBody, textBody);
    }

    @Override
    public boolean send(InternetAddress toAddress, String subject, String htmlBody, String textBody, Attachment... attachments) {
        return send(new InternetAddress[]{toAddress}, subject, htmlBody, textBody, attachments);
    }

    @Override
    public boolean send(InternetAddress[] toAddresses, String subject, String htmlBody, String textBody, Attachment... attachments) {
        initialize();
        return send(senderAddress, toAddresses, applicationOwners, subject, htmlBody, textBody, attachments);
    }

    private boolean send(InternetAddress fromAddress, InternetAddress[] toAddress, InternetAddress[] bccAddress, String subject, String htmlBody, String textBody, Attachment... attachments) {
        Preconditions.checkNotNull(fromAddress);
        Preconditions.checkNotNull(toAddress);
        Preconditions.checkNotNull(bccAddress);
        Preconditions.checkNotNull(subject);
        if (htmlBody == null && textBody == null) {
            throw new NullPointerException();
        }
        try {
            log.info("Sending e-mail [{}] as [{}] to {}", subject, fromAddress, Arrays.toString(toAddress));
            Message message = createMessage(fromAddress, toAddress, bccAddress, subject, htmlBody, textBody, attachments);
            Transport.send(message);

            MailDebug.writeDebug(message);

            return true;
        } catch (Exception e) {
            log.warn("Failed to send e-mail", e);
            return false;
        }
    }

    private Message createMessage(InternetAddress fromAddress, InternetAddress[] toAddress, InternetAddress[] bccAddress, String subject, String htmlBody, String textBody, Attachment... attachments) throws MessagingException {

        MimeMessage message = new MimeMessage(session);

        message.setFrom(fromAddress);

        for (InternetAddress owner : toAddress) {
            message.addRecipient(Message.RecipientType.TO, owner);
        }

        for (InternetAddress owner : bccAddress) {
            message.addRecipient(Message.RecipientType.BCC, owner);
        }

        message.setSubject(subject);
        message.setSentDate(new Date());

        /*
         * The section 5.1.4 of RFC 2046 defines multipart/alternative
         * http://tools.ietf.org/html/rfc2046#section-5.1.4
         * ... in general the LAST part is the best choice ...
         */
        MimeMultipart alternative = new MimeMultipart("alternative");

        if (textBody != null) {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(textBody, "text/plain");
            alternative.addBodyPart(textPart);
        }

        if (htmlBody != null) {
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            alternative.addBodyPart(htmlPart);
        }


        if (attachments != null && attachments.length > 0) {

            for (Attachment attachment : attachments) {
                byte[] data = TypeUtil.toBytes(attachment.getData());
                String mimeType = attachment.getMimeType();
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(data, mimeType)));
                attachmentPart.setDisposition(Part.ATTACHMENT);
                attachmentPart.setHeader("Content-ID", "<" + UUID.randomUUID().toString() + ">");
                attachmentPart.setFileName(attachment.getName());

                alternative.addBodyPart(attachmentPart);
            }
        }

        message.setContent(alternative);
        return message;
    }

    void reset() {
        // Reinitialise
        initializationState.set(StateEnum.NEW);
    }

    private enum StateEnum {
        NEW, INITIALIZING, INITIALIZED, FAILED
    }

    private static class Singleton {
        private static final MailService INSTANCE = new DefaultMailService();
    }
}