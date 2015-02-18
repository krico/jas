package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Link;
import com.google.common.base.Preconditions;
import com.google.gson.internal.StringMap;
import com.jasify.schedule.appengine.oauth2.OAuth2Util;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.paypal.api.payments.*;
import com.paypal.base.Constants;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.base.rest.PayPalResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.jasify.schedule.appengine.util.CurrencyUtil.formatCurrencyNumber;

/**
 * @author krico
 * @since 05/02/15.
 */
public class PayPalPaymentProvider implements PaymentProvider<PayPalPayment> {
    private static final long MIN_REMAINING_LIFETIME = 120;
    private static final Logger log = LoggerFactory.getLogger(PayPalPaymentProvider.class);
    private OAuthTokenCredential oAuthTokenCredential;
    private String profileId;

    private PayPalPaymentProvider() {
        Properties properties = new Properties();
        properties.setProperty(Constants.HTTP_CONNECTION_TIMEOUT, "5000");
        properties.setProperty(Constants.HTTP_CONNECTION_RETRY, "1");
        properties.setProperty(Constants.HTTP_CONNECTION_READ_TIMEOUT, "30000");
        properties.setProperty(Constants.HTTP_CONNECTION_MAX_CONNECTION, "100");
        properties.setProperty(Constants.CLIENT_ID, "AT7appfb0qGgJa6iXV2MWTOglyRPfxcBAHsd5amklFv_2X46UIxnwqxitk0dTFdJBazitOfmKDlyDA33");
        properties.setProperty(Constants.CLIENT_SECRET, "EI4_bm74PNvG5O7VapNbdJzLeP7E4_0IBQ_wwV06oD9yYAIKe7eD9RSiRWJ6QW9h9IutMtuOTyPMY5P3");
        if (EnvironmentUtil.isProduction()) {
            properties.setProperty(Constants.ENDPOINT, Constants.REST_LIVE_ENDPOINT);
        } else {
            properties.setProperty(Constants.ENDPOINT, Constants.REST_SANDBOX_ENDPOINT);
        }
        properties.setProperty(Constants.GOOGLE_APP_ENGINE, "true");
        log.info("Initializing PayPal with endpoint={}", properties.getProperty(Constants.ENDPOINT));
        oAuthTokenCredential = PayPalResource.initConfig(properties);

        setupWebProfile();
    }

    public static PayPalPaymentProvider instance() {
        return Singleton.INSTANCE;
    }

    private void setupWebProfile() {
        try {
            String accessToken = oAuthTokenCredential.getAccessToken();
//            WebProfile wp = new WebProfile().setId("XP-YCBZ-LGUJ-PR7B-QLYL");
//            wp.delete(accessToken);
            List<WebProfile> list = WebProfile.getList(accessToken);
            if (list == null || list.isEmpty()) {
                log.info("Creating new WebProfile");
                WebProfile profile = new WebProfile("Jasify BookIT");
//                profile.setFlowConfig(new FlowConfig().setLandingPageType("Billing"));
                profile.setInputFields(new InputFields().setAllowNote(false).setNoShipping(1));
//                profile.setPresentation(new Presentation().setBrandName("MyWayFit"));
                CreateProfileResponse response = profile.create(accessToken);
                profileId = response.getId();
                log.info("Created profile id={}, data={}", profileId, response);
            } else {
                Object payPalSUX = list.get(0);
                if (payPalSUX instanceof StringMap) {
                    StringMap yesItReallySux = (StringMap) payPalSUX;
                    Object payPalSuxSoBadItsNotEvenFunny = yesItReallySux.get("id");
                    profileId = Objects.toString(payPalSuxSoBadItsNotEvenFunny);
                } else {
                    profileId = list.get(0).getId();
                }
                log.info("Retrieved profile id={}", profileId);
            }
        } catch (Exception e) {
            log.warn("Failed to setup WebProfile", e);
        }
    }

    private OAuthTokenCredential getCredential() {
        if (oAuthTokenCredential.expiresIn() < MIN_REMAINING_LIFETIME) {
            log.debug("oAuthTokenCredential will be re-initialized...");
            oAuthTokenCredential = PayPalResource.getOAuthTokenCredential();
        }
        return oAuthTokenCredential;
    }

    @Override
    public void createPayment(PayPalPayment payment, GenericUrl baseUrl) throws PaymentException {
        payment.validate();

        String currency = payment.getCurrency();


        List<Item> items = new ArrayList<>();

        int itemCount = payment.getItemCount();
        if (itemCount == 0) {
            log.warn("Payment with zero items");
            items.add(new Item("1", "Deposit", formatCurrencyNumber(currency, payment.getAmount()), currency));
        } else {
            for (int i = 0; i < itemCount; ++i) {
                Payment.Item item = payment.getItem(i);
                items.add(new Item(Integer.toString(item.getUnits()),
                        item.getDescription(),
                        formatCurrencyNumber(currency, item.getPrice()),
                        currency));
            }
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(new Amount(currency, formatCurrencyNumber(currency, payment.getAmount())));
        transaction.setItemList(new ItemList().setItems(items));

        try {

            com.paypal.api.payments.Payment paymentToCreate = new com.paypal.api.payments.Payment();
            paymentToCreate.setExperienceProfileId(profileId);
            com.paypal.api.payments.Payment createdPayment = paymentToCreate
                    .setIntent("sale")
                    .setPayer(new Payer("paypal"))
                    .setRedirectUrls(createRedirectUrls(baseUrl))
                    .setTransactions(Collections.singletonList(transaction))
                    .create(getCredential().getAccessToken());


            payment.setExternalId(createdPayment.getId());
            payment.setExternalState(createdPayment.getState());
            Link approveUrl = extractUrl(createdPayment, "approval_url");
            approveUrl = TypeUtil.toLink(approveUrl.getValue() + "&useraction=commit");
            payment.setApproveUrl(approveUrl);
            payment.setSelfUrl(extractUrl(createdPayment, "self"));
            payment.setExecuteUrl(extractUrl(createdPayment, "execute"));
            payment.setState(PaymentStateEnum.Created);
            log.info("APPROVAL URL:\n\t{}", payment.getApproveUrl());

        } catch (PayPalRESTException e) {
            throw new PaymentException(e);
        }

    }

    @Override
    public void executePayment(PayPalPayment payment) throws PaymentException {
        String payerId = Preconditions.checkNotNull(payment.getPayerId(), "payment.PayerId");
        com.paypal.api.payments.Payment paymentToExecute = new com.paypal.api.payments.Payment().setId(payment.getExternalId());
        try {
            com.paypal.api.payments.Payment executedPayment = paymentToExecute.execute(getCredential().getAccessToken(), new PaymentExecution(payerId));
            payment.setExternalState(executedPayment.getState());
            setPayerInfo(payment, executedPayment);
            List<Transaction> transactions = executedPayment.getTransactions();
            if (transactions != null) {
                double fee = 0;
                for (Transaction transaction : transactions) {
                    Amount amount = transaction.getAmount();
                    if (amount == null) continue;
                    Details details = amount.getDetails();
                    if (details == null || details.getFee() == null) continue;
                    try {
                        fee += Double.parseDouble(details.getFee());
                    } catch (Exception e) {
                        log.debug("Failed to parse fee:[{}]", details.getFee(), e);
                    }
                }
                payment.setFee(fee);
            }
            payment.setState(PaymentStateEnum.Completed);

            log.debug("Executed Payment: {}", executedPayment);
        } catch (PayPalRESTException e) {
            throw new PaymentException(e);
        }
    }

    private void setPayerInfo(PayPalPayment payment, com.paypal.api.payments.Payment executedPayment) {
        Payer payer = executedPayment.getPayer();
        if (payer != null) {
            PayerInfo payerInfo = payer.getPayerInfo();
            if (payerInfo != null) {
                payment.setPayerEmail(payerInfo.getEmail());
                payment.setPayerFirstName(payerInfo.getFirstName());
                payment.setPayerLastName(payerInfo.getLastName());
            }
        }
    }

    private RedirectUrls createRedirectUrls(GenericUrl baseUrl) {
        String stateKey = OAuth2Util.createStateKey(baseUrl);

        GenericUrl cancelUrl = new GenericUrl(baseUrl.toURI());
        cancelUrl.setFragment("/paypal-cancel/" + stateKey);

        GenericUrl returnUrl = new GenericUrl(baseUrl.toURI());
        returnUrl.setFragment("/paypal-accept/" + stateKey);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl.build());
        redirectUrls.setReturnUrl(returnUrl.build());
        return redirectUrls;
    }

    private Link extractUrl(com.paypal.api.payments.Payment payPalPayment, String rel) throws PaymentException {
        for (Links link : payPalPayment.getLinks()) {
            if (StringUtils.equalsIgnoreCase(link.getRel(), rel)) {
                return TypeUtil.toLink(link.getHref());
            }
        }
        throw new PaymentException("PayPal payment has no \"" + rel + "\": " + payPalPayment);
    }


    private static final class Singleton {
        private static final PayPalPaymentProvider INSTANCE = new PayPalPaymentProvider();
    }
}
