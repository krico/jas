package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.transform.*;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeAdmin;

/**
 * @author krico
 * @since 10/08/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityPackageTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasContactMessageTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
                JasPaymentTransformer.class,
                JasRepeatDetailsTransformer.class,
                JasSubscriptionTransformer.class,
                JasTransactionTransformer.class,
                JasUserLoginTransformer.class,
                JasUserTransformer.class
        },
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class HistoryEndpoint {
    public static final long DEFAULT_TIME_WINDOW_MILLIS = TimeUnit.DAYS.toMillis(7);
    private final HistoryDao historyDao = new HistoryDao();

    @ApiMethod(name = "histories.query", path = "histories", httpMethod = ApiMethod.HttpMethod.GET)
    public List<History> getHistories(User caller, @Nullable @Named("fromDate") Date fromDate, @Nullable @Named("toDate") Date toDate) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        if (fromDate == null && toDate == null) {
            // No date specified, we default to latest in time window
            fromDate = DateUtils.truncate(new Date(System.currentTimeMillis() - DEFAULT_TIME_WINDOW_MILLIS), Calendar.HOUR);
            return historyDao.listSince(fromDate);
        } else if (toDate == null) {
            // Only fromDate
            return historyDao.listSince(fromDate);
        } else if (fromDate == null) {
            // No start specified, default window until toDate
            fromDate = DateUtils.truncate(new Date(toDate.getTime() - DEFAULT_TIME_WINDOW_MILLIS), Calendar.HOUR);
        }
        return historyDao.listBetween(fromDate, toDate);
    }
}
