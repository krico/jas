package com.jasify.schedule.appengine.communication;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.besr.CodeLine;
import com.jasify.schedule.appengine.besr.ReferenceCode;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.dao.common.*;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.activity.*;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.common.Organization;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.users.PasswordRecovery;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author krico
 * @since 20/08/15.
 */
public class ApplicationContextImpl extends VelocityContext {
    public static final String LOGO_PATH = "/build/img/jasify-logo-color.png";
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextImpl.class);

    public ApplicationContextImpl() {
        ApplicationContext.App app = createApp();
        put(ApplicationContext.APP_KEY, app);
        put(ApplicationContext.STRING_UTILS_KEY, new StringUtils());
        put(ApplicationContext.KEY_UTIL_KEY, KeyUtil.INSTANCE);
        put(ApplicationContext.FORMAT_UTIL_KEY, FormatUtil.INSTANCE);
        put(ApplicationContext.CURRENCY_UTIL_KEY, CurrencyUtil.INSTANCE);
        put(ApplicationContext.MODEL_UTIL_KEY, createModelUtil(app));
    }

    protected ApplicationContext.App createApp() {
        return new AppImpl();
    }

    protected ModelUtil createModelUtil(ApplicationContext.App app) {
        return new ModelUtil(app);
    }

    public static final class AppImpl implements ApplicationContext.App {
        public String getLogo() {
            return getUrl() + LOGO_PATH;
        }

        public String getUrl() {
            return EnvironmentUtil.defaultVersionUrl();
        }
    }

    public static class ModelUtil {
        private final ApplicationContext.App app;
        private final ActivityDao activityDao = new ActivityDao();
        private final ActivityPackageDao activityPackageDao = new ActivityPackageDao();
        private final ActivityTypeDao activityTypeDao = new ActivityTypeDao();
        private final OrganizationDao organizationDao = new OrganizationDao();
        private final AttachmentDao attachmentDao = new AttachmentDao();
        private final ActivityPackageSubscriptionDao activityPackageSubscriptionDao = new ActivityPackageSubscriptionDao();

        ModelUtil(ApplicationContext.App app) {
            this.app = app;
        }

        public String url(PasswordRecovery recovery) {
            String code = recovery.getCode().getName();
            GenericUrl recoverUrl = new GenericUrl(app.getUrl());
            recoverUrl.clear();
            recoverUrl.setRawPath("/");
            recoverUrl.setFragment("/recover-password/" + code);
            return recoverUrl.build();
        }

        public String name(User user) {
            if (user == null) return null;
            if (StringUtils.isNotBlank(user.getRealName()))
                return user.getRealName();
            if (StringUtils.isNotBlank(user.getName()))
                return user.getName();
            return KeyUtil.keyToString(user.getId());
        }

        public Activity activity(Subscription subscription) throws EntityNotFoundException {
            if (subscription.getActivityRef().getKey() == null) {
                log.warn("Subscription [{}] has no ActivityRef", subscription.getId());
                return null;
            }
            return activityDao.get(subscription.getActivityRef().getKey());
        }

        public ActivityPackage activityPackage(ActivityPackageExecution execution) throws EntityNotFoundException {
            if (execution.getActivityPackageRef().getKey() == null) {
                log.warn("ActivityPackageExecution [{}] has no ActivityPackageRef", execution.getId());
                return null;
            }
            return activityPackageDao.get(execution.getActivityPackageRef().getKey());
        }

        public String formatPeriodShort(Activity activity) {
            Date start = activity.getStart();
            Date finish = activity.getFinish();
            SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM/YY [HH:mm - ");
            startFormat.setTimeZone(InternationalizationUtil.getLocationTimeZone());
            SimpleDateFormat finishFormat = new SimpleDateFormat("HH:mm]");
            finishFormat.setTimeZone(InternationalizationUtil.getLocationTimeZone());

            return startFormat.format(start) + finishFormat.format(finish);
        }

        public String dateShort(Date date) {
            return dateShort(date, null);
        }

        public String dateShort(Date date, TimeZone timeZone) {
            if (date == null) return null;
            if (timeZone == null) timeZone = InternationalizationUtil.getLocationTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY");
            sdf.setTimeZone(timeZone);
            return sdf.format(date);
        }

        public Organization organization(Activity activity) throws EntityNotFoundException {
            if (activity.getActivityTypeRef().getKey() == null) {
                log.warn("Activity [{}] has no ActivityTypeRef", activity.getId());
                return null;
            }
            ActivityType activityType = activityTypeDao.get(activity.getActivityTypeRef().getKey());
            if (activityType.getOrganizationRef().getKey() == null) {
                log.warn("ActivityType [{}] has no OrganizationRef", activityType.getId());
                return null;
            }
            return organizationDao.get(activityType.getOrganizationRef().getKey());
        }

        public Attachment attachment(InvoicePayment payment) throws EntityNotFoundException {
            if (payment.getAttachmentRef().getKey() == null) {
                log.warn("Payment [{}] has no AttachmentRef", payment.getId());
                return null;
            }
            return attachmentDao.get(payment.getAttachmentRef().getKey());
        }

        public String formatReferenceCode(InvoicePayment payment) {
            String referenceCode = payment.getReferenceCode();
            if (StringUtils.length(referenceCode) != CodeLine.REFERENCE_LENGTH) {
                log.warn("Payment [{}] has invalid refCode [{}]", payment.getId(), referenceCode);
                return null;
            }
            return ReferenceCode.toHumanReadable(referenceCode);
        }

        public List<ActivityPackageSubscription> subscriptions(ActivityPackageExecution execution) {
            return activityPackageSubscriptionDao.getByActivityPackageExecutionId(execution.getId());
        }
    }
}
