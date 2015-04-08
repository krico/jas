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
import com.jasify.schedule.appengine.model.balance.BalanceService;
import com.jasify.schedule.appengine.model.balance.BalanceServiceFactory;
import com.jasify.schedule.appengine.model.cart.ShoppingCart;
import com.jasify.schedule.appengine.model.cart.ShoppingCartService;
import com.jasify.schedule.appengine.model.cart.ShoppingCartServiceFactory;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow;
import com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflowFactory;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasCheckoutPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentResponse;
import com.jasify.schedule.appengine.spi.dm.JasTransactionList;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.KeyUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        transformers = {
                /* one per line in alphabetical order to avoid merge conflicts */
                JasAccountTransformer.class,
                JasActivityTransformer.class,
                JasActivityTypeTransformer.class,
                JasGroupTransformer.class,
                JasKeyTransformer.class,
                JasOrganizationTransformer.class,
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
        paymentService.newPayment(jasCaller.getUserId(), payment, Collections.<PaymentWorkflow>emptyList());
        paymentService.createPayment(PayPalPaymentProvider.instance(), payment, baseUrl);
        return new JasPaymentResponse(TypeUtil.toString(payment.getApproveUrl()));
    }

    @ApiMethod(name = "balance.createCheckoutPayment", path = "balance/create-checkout-payment", httpMethod = ApiMethod.HttpMethod.POST)
    public JasPaymentResponse createCheckoutPayment(User caller, JasCheckoutPaymentRequest paymentRequest) throws UnauthorizedException, PaymentException, BadRequestException, NotFoundException {
        //TODO: I had a real hard time (and gave up) writing a test for this method.  Indicates it's too complex?
        JasifyEndpointUser jasCaller = mustBeLoggedIn(caller);
        Preconditions.checkNotNull(paymentRequest.getType());

        switch (paymentRequest.getType()) {
            case PayPal: {
                PayPalPayment payment = createPaymentInternal(jasCaller, PayPalPaymentProvider.instance(), paymentRequest);
                return new JasPaymentResponse(TypeUtil.toString(payment.getApproveUrl()));
            }
            case Cash: {
                CashPayment payment = createPaymentInternal(jasCaller, CashPaymentProvider.instance(), paymentRequest);
                GenericUrl url = new GenericUrl(paymentRequest.getBaseUrl());
                url.setFragment(CashPaymentProvider.ACCEPT_PATH + KeyUtil.keyToString(payment.getId()));
                return new JasPaymentResponse(url.build());
            }
            default:
                throw new BadRequestException("Unsupported payment type: " + paymentRequest.getType());
        }

    }

    private <T extends Payment> T createPaymentInternal(JasifyEndpointUser jasCaller, PaymentProvider<T> provider,
                                                        JasCheckoutPaymentRequest request) throws PaymentException, NotFoundException {
        GenericUrl baseUrl = new GenericUrl(Preconditions.checkNotNull(request.getBaseUrl()));
        String cartId = Preconditions.checkNotNull(StringUtils.trimToNull(request.getCartId()));

        ShoppingCartService cartService = ShoppingCartServiceFactory.getShoppingCartService();
        ShoppingCart cart = cartService.getCart(cartId);
        if (cart == null) {
            throw new NotFoundException("Cart id: [" + cartId + "] not found.");
        }

        String currency = Preconditions.checkNotNull(StringUtils.trimToNull(cart.getCurrency()));
        List<ShoppingCart.Item> items = Preconditions.checkNotNull(cart.getItems());
        Preconditions.checkState(!items.isEmpty());

        T payment = provider.newPayment();

        PaymentService paymentService = PaymentServiceFactory.getPaymentService();
        payment.setCurrency(currency);
        List<PaymentWorkflow> workflowList = new ArrayList<>();
        for (ShoppingCart.Item item : items) {
            payment.addItem(item.getDescription(), item.getUnits(), item.getPrice());
            if (item.getItemId() != null) {
                workflowList.add(PaymentWorkflowFactory.workflowFor(item.getItemId()));
            }
        }

        workflowList.add(PaymentWorkflowFactory.workflowForCartId(cartId));

        paymentService.newPayment(jasCaller.getUserId(), payment, workflowList);
        paymentService.createPayment(provider, payment, baseUrl);
        return payment;
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
        Preconditions.checkNotNull(payment.getType(), "No PaymentType");
        switch (payment.getType()) {
            case PayPal: {
                PayPalPayment payPalPayment = (PayPalPayment) payment;
                payPalPayment.setPayerId(payerId);
                paymentService.executePayment(PayPalPaymentProvider.instance(), payPalPayment);
                BalanceServiceFactory.getBalanceService().payment(payPalPayment);
            }
            break;
            case Cash: {
                CashPayment cashPayment = (CashPayment) payment;
                paymentService.executePayment(CashPaymentProvider.instance(), cashPayment);
                //TODO: how do we handle this now?
                BalanceServiceFactory.getBalanceService().payment(cashPayment);
            }
            break;
            default:
                throw new BadRequestException("Unsupported payment type: " + payment.getType());
        }
    }

    @ApiMethod(name = "balance.getAccount", path = "balance/account", httpMethod = ApiMethod.HttpMethod.GET)
    public Account getAccount(User caller) throws NotFoundException, UnauthorizedException {
        JasifyEndpointUser jasUser = mustBeLoggedIn(caller);
        try {
            return BalanceServiceFactory.getBalanceService().getUserAccount(jasUser.getUserId());
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    @ApiMethod(name = "balance.getTransactions", path = "balance/transactions/{accountId}", httpMethod = ApiMethod.HttpMethod.GET)
    public JasTransactionList getTransactions(User caller, @Named("accountId") Key accountId,
                                              @Nullable @Named("limit") Integer limit,
                                              @Nullable @Named("offset") Integer offset)
            throws NotFoundException, UnauthorizedException, ForbiddenException {
        mustBeSameUserOrAdmin(caller, AccountUtil.accountIdToMemberId(accountId));

        if (limit == null) limit = 0;
        if (offset == null) offset = 0;

        try {
            JasTransactionList transactions = new JasTransactionList();

            BalanceService balanceService = BalanceServiceFactory.getBalanceService();

            transactions.addAll(balanceService.listTransactions(accountId, offset, limit));

            transactions.setTotal(balanceService.getTransactionCount(accountId));

            return transactions;
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
