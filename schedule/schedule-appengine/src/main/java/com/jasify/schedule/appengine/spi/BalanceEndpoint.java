package com.jasify.schedule.appengine.spi;

import com.google.api.client.http.GenericUrl;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.model.payment.*;
import com.jasify.schedule.appengine.spi.auth.JasifyAuthenticator;
import com.jasify.schedule.appengine.spi.auth.JasifyEndpointUser;
import com.jasify.schedule.appengine.spi.dm.JasPaymentRequest;
import com.jasify.schedule.appengine.spi.dm.JasPaymentResponse;
import com.jasify.schedule.appengine.spi.transform.*;
import com.jasify.schedule.appengine.util.TypeUtil;

import static com.jasify.schedule.appengine.spi.JasifyEndpoint.mustBeLoggedIn;

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
        transformers = {JasUserLoginTransformer.class, JasUserTransformer.class, JasKeyTransformer.class, JasActivityTypeTransformer.class, JasActivityTransformer.class, JasOrganizationTransformer.class, JasGroupTransformer.class, JasSubscriptionTransformer.class},
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
}
