package com.jasify.schedule.appengine.mail;

import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.application.ApplicationData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Arrays;
import java.util.Properties;
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
                String senderAddressString = applicationData.getProperty(senderAddressKey);
                if (StringUtils.isBlank(senderAddressString)) {
                    senderAddressString = DEFAULT_SENDER;
                    log.warn("No senderAddress defined (key: {}) defaulting to: {}", senderAddressKey, senderAddressString);
                    applicationData.setProperty(senderAddressKey, senderAddressString);
                }

                String senderAddressNameKey = MailService.class.getName() + ".SenderAddressName";
                String senderAddressNameString = applicationData.getProperty(senderAddressNameKey);
                if (StringUtils.isBlank(senderAddressNameString)) {
                    senderAddressNameString = DEFAULT_SENDER_NAME;
                    log.warn("No senderAddress defined (key: {}) defaulting to: {}", senderAddressNameKey, senderAddressNameString);
                    applicationData.setProperty(senderAddressNameKey, senderAddressNameString);
                }

                String applicationOwnersKey = MailService.class.getName() + ".ApplicationOwners";
                String applicationOwnersString = applicationData.getProperty(applicationOwnersKey);
                if (StringUtils.isBlank(applicationOwnersString)) {
                    applicationOwnersString = DEFAULT_OWNER;
                    log.warn("No senderAddress defined (key: {}) defaulting to: {}", applicationOwnersKey, applicationOwnersString);
                    applicationData.setProperty(applicationOwnersKey, applicationOwnersString);
                }
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
    public boolean sendToApplicationOwners(String subject, String htmlBody) {
        initialize();
        Preconditions.checkNotNull(senderAddress);
        Preconditions.checkNotNull(applicationOwners);

        try {
            log.info("Sent e-mail [{}] as [{}] to {}", subject, senderAddress, Arrays.toString(applicationOwners));

            Message msg = new MimeMessage(session);
            msg.setFrom(senderAddress);
            for (InternetAddress owner : applicationOwners) {
                msg.addRecipient(Message.RecipientType.TO, owner);
            }
            msg.setSubject(subject);

            Multipart mp = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            mp.addBodyPart(htmlPart);

            String textBody = Jsoup.parse(htmlBody).text();
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(textBody, "text/plain");
            mp.addBodyPart(textPart);

            msg.setContent(mp);

            Transport.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("Failed to send e-mail", e);
            return false;
        }
    }

    void reset() {
        // Reinitialise
        initializationState.set(StateEnum.NEW);
    }

    private static enum StateEnum {
        NEW, INITIALIZING, INITIALIZED, FAILED
    }

    private static class Singleton {
        private static final MailService INSTANCE = new DefaultMailService();
    }
}
