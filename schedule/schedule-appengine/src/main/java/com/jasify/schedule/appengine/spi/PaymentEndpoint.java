package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.history.HistoryDao;
import com.jasify.schedule.appengine.dao.payment.PaymentDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.Navigate;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.history.History;
import com.jasify.schedule.appengine.model.payment.InvoicePayment;
import com.jasify.schedule.appengine.model.payment.Payment;
import com.jasify.schedule.appengine.model.payment.PaymentService;
import com.jasify.schedule.appengine.model.payment.PaymentServiceFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasInvoice;
import com.jasify.schedule.appengine.spi.transform.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.*;

/**
 * @author krico
 * @since 03/09/15.
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
public class PaymentEndpoint {

    public static final long DEFAULT_TIME_WINDOW_MILLIS = TimeUnit.DAYS.toMillis(7);
    private final PaymentDao paymentDao = new PaymentDao();

    @ApiMethod(name = "payments.query", path = "payments", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Payment> getPayments(User caller, @Nullable @Named("fromDate") Date fromDate, @Nullable @Named("toDate") Date toDate) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        if (fromDate == null && toDate == null) {
            // No date specified, we default to latest in time window
            fromDate = new Date(System.currentTimeMillis() - DEFAULT_TIME_WINDOW_MILLIS);
            return paymentDao.listSince(fromDate);
        } else if (toDate == null) {
            // Only fromDate
            return paymentDao.listSince(fromDate);
        } else if (fromDate == null) {
            // No start specified, default window until toDate
            fromDate = new Date(toDate.getTime() - DEFAULT_TIME_WINDOW_MILLIS);
        }
        return paymentDao.listBetween(fromDate, toDate);
    }

    @ApiMethod(name = "payments.getPaymentInvoice", path = "payment-invoices/{paymentId}", httpMethod = ApiMethod.HttpMethod.GET)
    public JasInvoice getPaymentInvoice(User caller, @Named("paymentId") Key paymentId) throws UnauthorizedException, NotFoundException, ForbiddenException, BadRequestException {
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        Payment payment = getPaymentCheckUser(paymentId, jasCaller);
        Preconditions.checkNotNull(payment.getType(), "No PaymentType");
        switch (payment.getType()) {
            case Invoice: {
                InvoicePayment invoicePayment = (InvoicePayment) payment;
                Attachment attachment = Navigate.attachment(invoicePayment);
                if (attachment == null)
                    throw new NotFoundException("No attachment on payment");
                return new JasInvoice(attachment);
            }
            default:
                throw new BadRequestException("Unsupported payment type: " + payment.getType());
        }
    }

    private Payment getPaymentCheckUser(Key paymentId, JasifyEndpointUser jasCaller) throws NotFoundException, UnauthorizedException, ForbiddenException {
        try {
            Payment payment = paymentDao.get(paymentId);
            // Ensure the payment belongs to this user!
            mustBeSameUserOrAdmin(jasCaller, payment.getUserRef().getKey());
            return payment;
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Payment not found");
        }
    }

}
