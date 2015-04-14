package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.jasify.schedule.appengine.TestHelper;
import com.jasify.schedule.appengine.util.CurrencyUtil;
import com.paypal.api.payments.*;
import com.paypal.api.payments.Payment;
import org.apache.commons.lang3.StringUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.*;
import org.slim3.datastore.Datastore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.*;

public class PayPalPaymentProviderTest {
    public static final String PROFILE_ID = "PID";
    public static final String APPROVAL_URL = "https://approval";
    public static final String SELF_URL = "http://self";
    public static final String EXECUTE_URL = "https://execute";
    public static final String PAYMENT_ID = "PAY-ID";
    public static final String BASE_URL = "http://localhost:8080/";
    public static final String CREATED_STATE = "BEFORE";
    public static final String PAYER_ID = "Payer123";
    public static final String STATE_EXECUTED = "AFTER";
    public static final String PAYER_EMAIL = "payer@email";
    public static final String PAYER_FIRST_NAME = "Fn";
    public static final String PAYER_LAST_NAME = "Ln";
    private TestPayPalInterface testPayPalInterface = new TestPayPalInterface();
    private PayPalPaymentProvider provider;

    @BeforeClass
    public static void setup() {
        TestHelper.initializeJasifyWithOAuthProviderData();
    }

    @AfterClass
    public static void cleanup() {
        TestHelper.cleanupDatastore();
        PayPalPaymentProvider.instance().setPayPalInterface(null);
    }

    @Before
    public void create() {
        testPayPalInterface.setUp();
        provider = PayPalPaymentProvider.instance();
    }

    @After
    public void tearDown() {
        testPayPalInterface.tearDown();
    }

    @Test
    public void testExecutePayment() throws Exception {
        final double fee = 1.23;

        PayPalPaymentProvider.PayPalInterface mock = testPayPalInterface.getPayPalMock();

        final Capture<Payment> paymentCapture = EasyMock.newCapture();
        final Capture<PaymentExecution> paymentExecutionCapture = EasyMock.newCapture();
        mock.execute(EasyMock.capture(paymentCapture), EasyMock.capture(paymentExecutionCapture));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Payment>() {
            @Override
            public Payment answer() throws Throwable {
                Payment payment = paymentCapture.getValue();
                assertEquals(PAYMENT_ID, payment.getId());
                PaymentExecution execution = paymentExecutionCapture.getValue();
                assertEquals(PAYER_ID, execution.getPayerId());
                payment.setState(STATE_EXECUTED);
                Transaction transaction = new Transaction();
                transaction.setAmount(new Amount());
                transaction.setRelatedResources(Collections.singletonList(new RelatedResources().setSale(new Sale().setTransactionFee(new Currency("CHF", Double.toString(fee))))));
                payment.setTransactions(Collections.singletonList(transaction));

                payment.setPayer(new Payer().setPayerInfo(new PayerInfo().setEmail(PAYER_EMAIL).setFirstName(PAYER_FIRST_NAME).setLastName(PAYER_LAST_NAME)));

                return payment;
            }
        });

        testPayPalInterface.replay();

        PayPalPayment jasPayment = new PayPalPayment();
        jasPayment.setExternalId(PAYMENT_ID);
        jasPayment.setPayerId(PAYER_ID);

        provider.executePayment(jasPayment);

        assertEquals(PaymentStateEnum.Completed, jasPayment.getState());
        assertEquals(STATE_EXECUTED, jasPayment.getExternalState());
        assertNotNull(jasPayment.getRealFee());
        assertEquals(fee, jasPayment.getRealFee());
        assertEquals(PAYER_EMAIL, jasPayment.getPayerEmail());
        assertEquals(PAYER_FIRST_NAME, jasPayment.getPayerFirstName());
        assertEquals(PAYER_LAST_NAME, jasPayment.getPayerLastName());

    }

    @Test
    public void testCreatePayment() throws Exception {

        final String currency = "CHF";
        final double price = 20.5d;


        PayPalPaymentProvider.PayPalInterface mock = testPayPalInterface.getPayPalMock();
        EasyMock.expect(mock.getWebProfiles()).andReturn(Collections.singletonList(new WebProfile().setId(PROFILE_ID)));

        final Capture<Payment> paymentCapture = EasyMock.newCapture();
        EasyMock.expect(mock.create(EasyMock.capture(paymentCapture))).andAnswer(new IAnswer<Payment>() {
            @Override
            public Payment answer() throws Throwable {
                Payment payment = paymentCapture.getValue();

                assertNotNull(payment);
                assertNotNull(payment.getPayer());
                assertEquals("paypal", payment.getPayer().getPaymentMethod());

                assertNotNull(payment.getTransactions());
                assertEquals(1, payment.getTransactions().size());
                Transaction transaction = payment.getTransactions().get(0);
                String formattedAmount = CurrencyUtil.formatCurrencyNumber(currency, price);
                assertEquals(formattedAmount, transaction.getAmount().getTotal());
                assertEquals(currency, transaction.getAmount().getCurrency());
                assertEquals(PROFILE_ID, payment.getExperienceProfileId());

                assertNotNull(transaction.getItemList());
                List<Item> items = transaction.getItemList().getItems();
                assertNotNull(items);
                assertEquals(1, items.size());
                assertEquals(formattedAmount, items.get(0).getPrice());
                assertEquals("1", items.get(0).getQuantity());
                assertEquals(currency, items.get(0).getCurrency());

                RedirectUrls redirectUrls = payment.getRedirectUrls();
                assertNotNull(redirectUrls);
                assertNotNull(redirectUrls.getReturnUrl());
                assertNotNull(redirectUrls.getCancelUrl());

                assertTrue(StringUtils.startsWith(redirectUrls.getReturnUrl(), BASE_URL + "#" + PayPalPaymentProvider.ACCEPT_PATH));
                assertTrue(StringUtils.startsWith(redirectUrls.getCancelUrl(), BASE_URL + "#" + PayPalPaymentProvider.CANCEL_PATH));

                payment.setId(PAYMENT_ID);
                payment.setState(CREATED_STATE);

                payment.setLinks(Arrays.asList(
                        new Links().setRel("approval_url").setHref(APPROVAL_URL),
                        new Links().setRel("self").setHref(SELF_URL),
                        new Links().setRel("execute").setHref(EXECUTE_URL)
                ));

                return payment;
            }
        });

        testPayPalInterface.replay();

        PayPalPayment jasPayment = new PayPalPayment();
        jasPayment.setId(Datastore.allocateId(PayPalPayment.class));
        jasPayment.setCurrency(currency);
        jasPayment.addItem("Item", 1, price);

        jasPayment.setFee(0d); //force fee to be 0

        provider.createPayment(jasPayment, new GenericUrl(BASE_URL));

        assertEquals(PaymentStateEnum.Created, jasPayment.getState());

        assertEquals(jasPayment.getExternalId(), PAYMENT_ID);
        assertEquals(jasPayment.getExternalState(), CREATED_STATE);

        assertNotNull(jasPayment.getApproveUrl());
        assertEquals(jasPayment.getApproveUrl().getValue(), APPROVAL_URL + "&useraction=commit");

        assertNotNull(jasPayment.getSelfUrl());
        assertEquals(jasPayment.getSelfUrl().getValue(), SELF_URL);

        assertNotNull(jasPayment.getExecuteUrl());
        assertEquals(jasPayment.getExecuteUrl().getValue(), EXECUTE_URL);
    }
}