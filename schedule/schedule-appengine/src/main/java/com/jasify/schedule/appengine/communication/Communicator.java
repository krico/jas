package com.jasify.schedule.appengine.communication;

import com.jasify.schedule.appengine.Version;
import com.jasify.schedule.appengine.mail.MailServiceFactory;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.template.TemplateEngine;
import com.jasify.schedule.appengine.template.TemplateEngineBuilder;
import com.jasify.schedule.appengine.template.TemplateEngineException;
import com.jasify.schedule.appengine.template.TemplateNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * This class handles all external communication, to subscribers, publishers and admins
 *
 * @author krico
 * @since 19/08/15.
 */
public class Communicator {
    private static final Logger log = LoggerFactory.getLogger(Communicator.class);

    private static final TemplateEngine templateEngine = new TemplateEngineBuilder().build();
    private static final ThreadLocal<Context> GLOBAL_CONTEXT = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new ApplicationContextImpl();
        }
    };

    public static void notifyOfNewVersion() throws TemplateEngineException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("version", Version.INSTANCE);
        String html = templateEngine.render(TemplateNames.JASIFY_NEW_VERSION_HTML, context);
        String text = templateEngine.render(TemplateNames.JASIFY_NEW_VERSION_TXT, context);

        String subject = String.format("[Jasify] New Version In Prod (%s) [%s]", Version.getDeployVersion(), Version.toShortVersionString());
        MailServiceFactory.getMailService().sendToApplicationOwners(subject, html, text);
    }

    public static void notifyOfNewUser(User user) throws TemplateEngineException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        String html = templateEngine.render(TemplateNames.JASIFY_NEW_USER_HTML, context);
        String text = templateEngine.render(TemplateNames.JASIFY_NEW_USER_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] New User [%s]", nonBlankName);
        MailServiceFactory.getMailService().sendToApplicationOwners(subject, html, text);
    }

    public static void notifyOfPasswordRecovery(User user, PasswordRecovery recovery) throws TemplateEngineException, UnsupportedEncodingException {
        VelocityContext context = new VelocityContext(GLOBAL_CONTEXT.get());
        context.put("user", user);
        context.put("recovery", recovery);
        String html = templateEngine.render(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_HTML, context);
        String text = templateEngine.render(TemplateNames.SUBSCRIBER_PASSWORD_RECOVERY_TXT, context);

        String nonBlankName = StringUtils.isNoneBlank(user.getRealName()) ? user.getRealName() : user.getName();
        String subject = String.format("[Jasify] Password Assistance [%s]", nonBlankName);
        MailServiceFactory.getMailService().send(new InternetAddress(user.getEmail(), user.getName()), subject, html, text);
    }
}
