package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.model.balance.Account;
import com.jasify.schedule.appengine.model.balance.AccountUtil;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.balance.Transaction;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentResponse;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.TypeUtil;

import java.util.List;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeLoggedIn;
import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeSameUserOrAdmin;

/**
 * @author krico
 * @since 24/02/15.
 */
@Api(name = "jasify", /* WARN: Its LAME but you have to copy & paste this section to all *Endpoint classes in this package */
        version = "v1",
        defaultVersion = AnnotationBoolean.TRUE,
        description = "Jasify Schedule",
        authenticators = {JasifyAuthenticator.class},
        authLevel = AuthLevel.NONE,
        transformers = {JasAccountTransformer.class, JasTransactionTransformer.class, JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class, JasSubscriptionTransformer.class},
        auth = @ApiAuth(allowCookieAuth = AnnotationBoolean.TRUE /* todo: I don't know another way :-( */),
        namespace = @ApiNamespace(ownerDomain = "jasify.com",
                ownerName = "Jasify",
                packagePath = ""))
public class BalanceEndpoint {

    @ApiMethod(name = "balance.createPayment", path = "balance/create-payment", httpMethod = ApiMethod.HttpMethod.POST)
    public JasPaymentResponse createPayment(User caller, JasPaymentRequest paymentRequest) throws UnauthorizedException, PaymentException, BadRequestException {
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
        GenericUrl baseUrl = new GenericUrl(Preconditions.checkNotNull(paymentRequest.getBaseUrl()));
        if (paymentRequest.getType() != PaymentTypeEnum.PayPal) {
            throw new BadRequestException("Only PayPal payment is supported at this time.");
        }
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        PayPalPayment payment = new PayPalPayment();
        payment.setCurrency(paymentRequest.getCurrency());
        payment.addItem("Jasify Credits", 1, paymentRequest.getAmount());
        paymentService.newPayment(jasCaller.getUserId(), payment);
        paymentService.createPayment(PayPalPaymentProvider.instance(), payment, baseUrl);
        return new JasPaymentResponse(TypeUtil.toString(payment.getApproveUrl()));
    }

    @ApiMethod(name = "balance.cancelPayment", path = "balance/cancel-payment/{id}", httpMethod = ApiMethod.HttpMethod.DELETE)
    public void cancelPayment(User caller, @Named("id") Key paymentId) throws UnauthorizedException, PaymentException, BadRequestException, NotFoundException, ForbiddenException {
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        Payment payment = getPaymentCheckUser(paymentId, jasCaller, paymentService);
        try {
            paymentService.cancelPayment(payment); //TODO: implement
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Payment (deleted after get?)");
        }
    }

    @ApiMethod(name = "balance.executePayment", path = "balance/execute-payment/{id}", httpMethod = ApiMethod.HttpMethod.PUT)
    public void executePayment(User caller, @Named("id") Key paymentId, @Named("payerId") String payerId) throws UnauthorizedException, PaymentException, BadRequestException, NotFoundException, ForbiddenException {
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        Payment payment = getPaymentCheckUser(paymentId, jasCaller, paymentService);
        Preconditions.checkArgument(payment.getType() == PaymentTypeEnum.PayPal, "Only PayPal payments supported");
        PayPalPayment payPalPayment = (PayPalPayment) payment;
        payPalPayment.setPayerId(payerId);
        paymentService.executePayment(PayPalPaymentProvider.instance(), payPalPayment);
        BalanceServiceFactory.getBalanceService().payment(payPalPayment);
    }

    @ApiMethod(name = "balance.getAccount", path = "balance/account", httpMethod = ApiMethod.HttpMethod.GET)
    public Account getAccount(User caller) throws NotFoundException, UnauthorizedException {
        JasifyEndpointUser jasUser = mustBeLoggedIn(caller);
        try {
            return BalanceServiceFactory.getBalanceService().getUserAccount(jasUser.getUserId());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Account");
        }
    }

    @ApiMethod(name = "balance.listTransactions", path = "balance/transactions/{accountId}", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Transaction> listTransactions(User caller, @Named("accountId") Key accountId,
                                              @Nullable @Named("limit") Integer limit,
                                              @Nullable @Named("offset") Integer offset)
            throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeSameUserOrAdmin(caller, AccountUtil.accountIdToMemberId(accountId));

        if (limit == null) limit = 0;
        if (offset == null) offset = 0;

        try {


            return BalanceServiceFactory.getBalanceService().listTransactions(accountId, offset, limit);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Account");
        }
    }

    private Payment getPaymentCheckUser(Key paymentId, JasifyEndpointUser jasCaller, PaymentService paymentService) throws UnauthorizedException, ForbiddenException, NotFoundException {
        Payment payment;
        try {
            payment = paymentService.getPayment(paymentId);
            // Ensure the payment belongs to this user!
            mustBeSameUserOrAdmin(jasCaller, payment.getUserRef().getKey());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Payment");
        }
        return payment;
    }
}
