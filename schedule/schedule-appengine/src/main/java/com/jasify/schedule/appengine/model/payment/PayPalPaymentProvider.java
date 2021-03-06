package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.appengine.api.datastore.Link;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderConfig;
import com.jasify.schedule.appengine.oauth2.OAuth2ProviderEnum;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import com.paypal.api.payments.*;
import com.paypal.base.Constants;
import com.paypal.base.SDKVersion;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.base.rest.PayPalResource;
import com.paypal.base.sdk.info.SDKVersionImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static com.jasify.schedule.appengine.util.CurrencyUtil.formatCurrencyNumber;

/**
 * @author krico
 * @since 05/02/15.
 */
public class PayPalPaymentProvider implements PaymentProvider<PayPalPayment> {
    public static final BigDecimal PAY_PAL_FEE_MULTIPLIER = new BigDecimal("0.030");
    public static final BigDecimal ONE_MINUS_PAY_PAL_FEE_MULTIPLIER = BigDecimal.ONE.subtract(PAY_PAL_FEE_MULTIPLIER);
    public static final BigDecimal PAY_PAL_FEE_FLAT = new BigDecimal("0.55");
    private static final long MIN_REMAINING_LIFETIME = 120;
    private static final Logger log = LoggerFactory.getLogger(PayPalPaymentProvider.class);
    private PayPalInterface payPalInterface;
    private String profileId;

    private PayPalPaymentProvider() {
        Properties properties = new Properties();
        properties.setProperty(Constants.HTTP_CONNECTION_TIMEOUT, "5000");
        properties.setProperty(Constants.HTTP_CONNECTION_RETRY, "1");
        properties.setProperty(Constants.HTTP_CONNECTION_READ_TIMEOUT, "30000");
        properties.setProperty(Constants.HTTP_CONNECTION_MAX_CONNECTION, "100");
        OAuth2ProviderConfig providerConfig = OAuth2ProviderEnum.PayPal.config();
        properties.setProperty(Constants.CLIENT_ID, providerConfig.getClientId());
        properties.setProperty(Constants.CLIENT_SECRET, providerConfig.getClientSecret());
        if (EnvironmentUtil.isProduction()) {
            properties.setProperty(Constants.ENDPOINT, Constants.REST_LIVE_ENDPOINT);
        } else {
            properties.setProperty(Constants.ENDPOINT, Constants.REST_SANDBOX_ENDPOINT);
        }
        properties.setProperty(Constants.GOOGLE_APP_ENGINE, "true");
        log.info("Initializing PayPal with endpoint={}", properties.getProperty(Constants.ENDPOINT));
        PayPalResource.initConfig(properties);
    }

    public static PayPalPaymentProvider instance() {
        return Singleton.INSTANCE;
    }

    /**
     * From: https://www.paypal.com/ch/webapps/mpp/paypal-fees
     * <p/>
     * 3.4% + 0.55CHF
     * <p/>
     * So,
     * p = amount paid to PayPal
     * m = cash received in jasify
     * <p/>
     * m = p - "fee" = p - (0.034p + 0.55) = p - 0.034p - 0.55
     * <p/>
     * I want to isolate "p"
     * <p/>
     * m  = p - 0.034p - 0.55
     * p - 0.034p  = m + 0.55
     * (1 - 0.034)p  = m + 0.55
     * 0.966p  = m + 0.55
     * p  = (m + 0.55)/0.966
     * <p/>
     * So, to make a payment of CHF 20.-
     * p = (20 + 0.55)/0.966 = 20.55/0.966 = 21.27329192546584
     */
    public static double calculateHandlingFee(double amount) {
        BigDecimal paymentAmount = new BigDecimal(amount).setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal amountToBePaid = paymentAmount.add(PAY_PAL_FEE_FLAT);
        amountToBePaid = amountToBePaid.divide(ONE_MINUS_PAY_PAL_FEE_MULTIPLIER, BigDecimal.ROUND_CEILING);
        amountToBePaid = amountToBePaid.setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal fee = amountToBePaid.subtract(paymentAmount);
        return fee.doubleValue();
    }

    @Override
    public PayPalPayment newPayment() {
        return new PayPalPayment();
    }

    private void needWebProfile() {
        if (StringUtils.isNotBlank(profileId)) return;

        PayPalInterface payPal = getPayPalInterface();
        try {
//            WebProfile wp = new WebProfile().setId("XP-YCBZ-LGUJ-PR7B-QLYL");
//            wp.delete(accessToken);
            List<WebProfile> list = payPal.getWebProfiles();
            if (list == null || list.isEmpty()) {
                log.info("Creating new WebProfile");
                WebProfile webProfile = new WebProfile("Jasify BookIT");
//                webProfile.setFlowConfig(new FlowConfig().setLandingPageType("Billing"));
//                webProfile.setPresentation(new Presentation().setBrandName("Jasify"));
                webProfile.setInputFields(new InputFields().setAllowNote(false).setNoShipping(1));
                profileId = payPal.create(webProfile);
            } else {
                profileId = list.get(0).getId();
                // TODO: Remove this once its been run in prod
                updateWebProfile(list.get(0), "Jasify");
                log.info("Retrieved profile id={}", profileId);
            }
        } catch (Exception e) {
            log.warn("Failed to setup WebProfile", e);
        }
    }

    private void updateWebProfile(WebProfile webProfile, String brandName) {
        PayPalInterface payPal = getPayPalInterface();
        try {
            boolean updated = false;
            String oldBrandName = webProfile.getPresentation() != null ? webProfile.getPresentation().getBrandName() : null;
            if (!StringUtils.equals(brandName, oldBrandName)) {
                if (brandName == null) {
                    webProfile.setPresentation(null);
                } else {
                    webProfile.setPresentation(new Presentation().setBrandName(brandName));
                }
                updated = true;
            }

            if (updated) {
                payPal.update(webProfile);
                log.info("WebProfile updated id={}, brandName={}", webProfile.getId(), brandName);
            }
        } catch (Exception e) {
            log.warn("Failed to update WebProfile", e);
        }
    }
    @Override
    public void createPayment(PayPalPayment payment, GenericUrl baseUrl) throws PaymentException {
        payment.validate();
        needWebProfile();

        String currency = payment.getCurrency();

        /*
         * amountToBePaid = (paymentAmount + FLAT_FEE)/(1 - FEE_MULTIPLIER)
         */
        if (payment.getFee() == null) {
            double fee = calculateHandlingFee(payment.getAmount());
            payment.setFee(fee);
            payment.setAmount(payment.getAmount() + fee);
        }
        payment.validate(); //re-validate fees


        List<Item> items = new ArrayList<>();

        int itemCount = payment.getItemCount();

        if (itemCount == 0) {
            log.warn("Payment with zero items");
            items.add(new Item("1", "Credits", formatCurrencyNumber(currency, payment.getAmount()), currency));
        } else {
            for (int i = 0; i < itemCount; ++i) {
                Payment.Item item = payment.getItem(i);
                items.add(new Item(Integer.toString(item.getUnits()),
                        item.getDescription(),
                        formatCurrencyNumber(currency, item.getPrice()),
                        currency));
            }
        }
        if (payment.getFee() > 0d) {
            items.add(new Item("1",
                    "Handling fee",
                    formatCurrencyNumber(currency, payment.getFee()),
                    currency));
        }


        Transaction transaction = new Transaction();
        transaction.setAmount(new Amount(currency, formatCurrencyNumber(currency, payment.getAmount())));
        transaction.setItemList(new ItemList().setItems(items));

        com.paypal.api.payments.Payment paymentToCreate = new com.paypal.api.payments.Payment()
                .setIntent("sale")
                .setPayer(new Payer("paypal"))
                .setRedirectUrls(createRedirectUrls(baseUrl, KeyUtil.keyToString(payment.getId())))
                .setTransactions(Collections.singletonList(transaction));

        paymentToCreate.setExperienceProfileId(profileId);

        PayPalInterface payPal = getPayPalInterface();
        com.paypal.api.payments.Payment createdPayment = payPal.create(paymentToCreate);

        payment.setExternalId(createdPayment.getId());
        payment.setExternalState(createdPayment.getState());

        Link approveUrl = extractUrl(createdPayment, "approval_url");
        approveUrl = TypeUtil.toLink(approveUrl.getValue() + "&useraction=commit");
        payment.setApproveUrl(approveUrl);

        payment.setSelfUrl(extractUrl(createdPayment, "self"));

        payment.setExecuteUrl(extractUrl(createdPayment, "execute"));

        payment.setState(PaymentStateEnum.Created);
    }

    @Override
    public void executePayment(PayPalPayment payment) throws PaymentException {
        String payerId = Preconditions.checkNotNull(payment.getPayerId(), "payment.PayerId");

        PayPalInterface payPal = getPayPalInterface();
        com.paypal.api.payments.Payment executedPayment = payPal.execute(new com.paypal.api.payments.Payment().setId(payment.getExternalId()), new PaymentExecution(payerId));

        payment.setExternalState(executedPayment.getState());
        setPayerInfo(payment, executedPayment);
        List<Transaction> transactions = executedPayment.getTransactions();
        if (transactions != null) {
            double realFee = 0;
            for (Transaction transaction : transactions) {
                Amount amount = transaction.getAmount();
                if (amount == null) continue;
                List<RelatedResources> relatedResources = transaction.getRelatedResources();
                if (relatedResources != null) {
                    for (RelatedResources relatedResource : relatedResources) {
                        Sale sale = relatedResource.getSale();
                        if (sale == null) continue;
                        Currency transactionFee = sale.getTransactionFee();
                        if (transactionFee == null) continue;
                        String currency = transactionFee.getCurrency();
                        String value = transactionFee.getValue();
                        try {
                            realFee += Double.parseDouble(value);
                        } catch (Exception e) {
                            log.debug("Failed to parse realFee:[{}]", value, e);
                        }
                        if (!StringUtils.equalsIgnoreCase(payment.getCurrency(), currency)) {
                            log.warn("Currency on transaction realFee ({}) differs from payment ({})", currency, payment.getCurrency());
                        }
                    }
                }
            }
            Double fee = payment.getFee();
            fee = fee == null ? 0d : fee;
            if (fee != realFee) {
                log.warn("Payment[{}] realFee:{} fee: {} error: {}", executedPayment.getId(), realFee, fee, fee - realFee);
            } else {
                log.debug("Payment[{}] realFee:{} fee: {}", executedPayment.getId(), realFee, fee);
            }
            payment.setRealFee(realFee);
        }
        payment.setState(PaymentStateEnum.Completed);

        log.debug("Executed Payment: {}", executedPayment);
    }

    public String getPayerId(PayPalPayment payment) throws PaymentException {
        String paymentId = Preconditions.checkNotNull(payment.getExternalId(), "payment.externalId");

        PayPalInterface payPal = getPayPalInterface();
        com.paypal.api.payments.Payment executedPayment = payPal.get(paymentId);
        if (executedPayment != null && executedPayment.getPayer() != null && executedPayment.getPayer().getPayerInfo() != null) {
            return executedPayment.getPayer().getPayerInfo().getPayerId();
        }
        return null;
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

    private RedirectUrls createRedirectUrls(GenericUrl baseUrl, String stateKey) {

        GenericUrl cancelUrl = new GenericUrl(baseUrl.toURI());
        cancelUrl.setFragment(CANCEL_PATH + stateKey);

        GenericUrl returnUrl = new GenericUrl(baseUrl.toURI());
        returnUrl.setFragment(ACCEPT_PATH + stateKey);

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

    PayPalInterface getPayPalInterface() {
        if (payPalInterface == null)
            payPalInterface = new DefaultPayPalInterface();
        return payPalInterface;
    }

    void setPayPalInterface(PayPalInterface payPalInterface) {
        this.payPalInterface = payPalInterface;
        this.profileId = null;
    }

    /**
     * This interface is used to allow testing with the PayPal SDK
     * It is used to "delegate" every call that requires the PayPal services
     */
    static interface PayPalInterface {
        List<WebProfile> getWebProfiles() throws PaymentException;

        String create(WebProfile profile) throws PaymentException;

        void update(WebProfile profile) throws PaymentException;

        com.paypal.api.payments.Payment create(com.paypal.api.payments.Payment payment) throws PaymentException;

        com.paypal.api.payments.Payment execute(com.paypal.api.payments.Payment payment, PaymentExecution execution) throws PaymentException;

        com.paypal.api.payments.Payment get(String externalId) throws PaymentException;
    }

    private static final class Singleton {
        private static final PayPalPaymentProvider INSTANCE = new PayPalPaymentProvider();
    }

    private static class DefaultPayPalInterface implements PayPalInterface {
        private OAuthTokenCredential oAuthTokenCredential;

        private DefaultPayPalInterface() {
            SDKVersion version = new SDKVersionImpl();
            log.info("Running with PayPal SDK id={} (version {})", version.getSDKId(), version.getSDKVersion());
        }

        private OAuthTokenCredential getCredential() {
            if (oAuthTokenCredential == null || oAuthTokenCredential.expiresIn() < MIN_REMAINING_LIFETIME) {
                log.debug("oAuthTokenCredential will be re-initialized...");
                oAuthTokenCredential = PayPalResource.getOAuthTokenCredential();
            }
            return oAuthTokenCredential;
        }

        private String getAccessToken() throws PaymentException {
            try {
                return getCredential().getAccessToken();
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public List<WebProfile> getWebProfiles() throws PaymentException {
            try {
                return WebProfile.getList(getAccessToken());
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public String create(WebProfile profile) throws PaymentException {
            try {
                CreateProfileResponse response = profile.create(getAccessToken());
                String profileId = response.getId();
                log.info("Created profile id={}, data={}", profileId, response);
                return profileId;
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public void update(WebProfile webProfile) throws PaymentException {
            try {
                webProfile.update(getAccessToken());
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public com.paypal.api.payments.Payment create(com.paypal.api.payments.Payment payment) throws PaymentException {
            try {
                return payment.create(getAccessToken());
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public com.paypal.api.payments.Payment execute(com.paypal.api.payments.Payment payment, PaymentExecution execution) throws PaymentException {
            try {
                return payment.execute(getAccessToken(), execution);
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }

        @Override
        public com.paypal.api.payments.Payment get(String paymentId) throws PaymentException {
            try {
                return com.paypal.api.payments.Payment.get(getAccessToken(), paymentId);
            } catch (PayPalRESTException e) {
                throw new PaymentException(e);
            }
        }
    }
}
