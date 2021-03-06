package com.jasify.schedule.appengine.spi;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.communication.Communicator;
import com.jasify.schedule.appengine.dao.payment.PaymentDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.Navigate;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.history.HistoryHelper;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasInvoice;
import com.jasify.schedule.appengine.spi.transform.*;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
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
                JasContactMessageTransformer.class,
                JasGroupTransformer.class,
                JasHistoryTransformer.class,
                JasKeyTransformer.class,
                JasMultipassTransformer.class,
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
    private static final Logger log = LoggerFactory.getLogger(PaymentEndpoint.class);
    private final PaymentDao paymentDao = new PaymentDao();

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

    @ApiMethod(name = "payments.query", path = "payments", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Payment> getPayments(User caller,
                                     @Nullable @Named("fromDate") Date fromDate,
                                     @Nullable @Named("toDate") Date toDate,
                                     @Nullable @Named("state") PaymentStateEnum state) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        LoggerFactory.getLogger(PaymentEndpoint.class).info("getPayment({}, {}, {})", fromDate, toDate, state);
        if (fromDate == null && toDate == null) {
            if (state == null) {
                // No date specified, we default to latest in time window
                fromDate = DateUtils.truncate(new Date(System.currentTimeMillis() - DEFAULT_TIME_WINDOW_MILLIS), Calendar.HOUR);
                return paymentDao.list(fromDate);
            } else {
                return paymentDao.list(state);
            }
        } else if (toDate == null) {
            if (state == null) {
                // Only fromDate
                return paymentDao.list(fromDate);
            } else {
                return paymentDao.list(fromDate, state);
            }
        } else if (fromDate == null) {
            // No start specified, default window until toDate
            fromDate = DateUtils.truncate(new Date(toDate.getTime() - DEFAULT_TIME_WINDOW_MILLIS), Calendar.HOUR);
        }
        if (state == null) {
            return paymentDao.list(fromDate, toDate);
        } else {
            return paymentDao.list(fromDate, toDate, state);
        }
    }

    @ApiMethod(name = "payments.executePayment", path = "payments-execute/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Payment executePayment(User caller, @Named("id") Key paymentId) throws UnauthorizedException, ForbiddenException, BadRequestException, NotFoundException, InternalServerErrorException {
        mustBeAdmin(caller);

        Payment payment;
        try {
            payment = paymentDao.get(paymentId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("PaymentId=" + paymentId);
        }

        if (payment.getType() != PaymentTypeEnum.Invoice) {
            throw new BadRequestException("Only Payment.Type Invoice");
        }

        InvoicePayment invoicePayment = (InvoicePayment) payment;

        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        try {
            paymentService.executePayment(InvoicePaymentProvider.instance(), invoicePayment);
            HistoryHelper.addPaymentExecuted(invoicePayment);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("PaymentId=" + paymentId);
        } catch (PaymentException e) {
            log.error("PaymentException executing PaymentId={}", paymentId, e);
            throw new InternalServerErrorException("PaymentException executing PaymentId=" + paymentId, e);
        }

        try {
            Communicator.notifyOfPaymentExecuted(invoicePayment);
        } catch (Exception e) {
            log.error("Failed to notify of payment executed", e);
        }

        return invoicePayment;
    }

    @ApiMethod(name = "payments.cancelPayment", path = "payments-cancel/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Payment cancelPayment(User caller, @Named("id") Key paymentId) throws UnauthorizedException, ForbiddenException, NotFoundException, BadRequestException, InternalServerErrorException {
        mustBeAdmin(caller);
        Payment payment;
        try {
            payment = paymentDao.get(paymentId);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("PaymentId=" + paymentId);
        }

        if (payment.getType() != PaymentTypeEnum.Invoice) {
            throw new BadRequestException("Only Payment.Type Invoice");
        }

        InvoicePayment invoicePayment = (InvoicePayment) payment;

        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        try {
            paymentService.cancelPayment(invoicePayment);
            HistoryHelper.addPaymentCancelled(invoicePayment);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("PaymentId=" + paymentId);
        } catch (PaymentException e) {
            log.error("PaymentException cancelling PaymentId={}", paymentId, e);
            throw new InternalServerErrorException("PaymentException cancelling PaymentId=" + paymentId, e);
        }

        try {
            Communicator.notifyOfPaymentCancelled(invoicePayment);
        } catch (Exception e) {
            log.error("Failed to notify of payment cancelled", e);
        }

        return invoicePayment;
    }

    @ApiMethod(name = "payments.queryByReferenceCode", path = "payments-reference-code/{referenceCode}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Payment> getPaymentsByReferenceCode(User caller, @Named("referenceCode") String referenceCode) throws UnauthorizedException, ForbiddenException {
        mustBeAdmin(caller);
        return paymentDao.list(referenceCode);
    }

    @ApiMethod(name = "payments.get", path = "payments/{id}", httpMethod = ApiMethod.HttpMethod.GET)
    public Payment getPayment(User caller, @Named("id") Key id) throws NotFoundException, UnauthorizedException, ForbiddenException {
        checkFound(id, "id == null");
        mustBeAdmin(caller);
        try {
            return paymentDao.get(id);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @ApiMethod(name = "payments.getPaymentInvoice", path = "payment-invoices/{paymentId}", httpMethod = ApiMethod.HttpMethod.GET)
    public JasInvoice getPaymentInvoice(User caller, @Named("paymentId") Key paymentId) throws UnauthorizedException, NotFoundException, ForbiddenException, BadRequestException {
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
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

}
