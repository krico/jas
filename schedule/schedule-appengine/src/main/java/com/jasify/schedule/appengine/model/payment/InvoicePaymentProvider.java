package com.jasify.schedule.appengine.model.payment;

import com.google.api.client.http.GenericUrl;
import com.google.common.base.Preconditions;
import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.besr.PaymentSlip;
import com.jasify.schedule.appengine.besr.PaymentSlipBuilder;
import com.jasify.schedule.appengine.besr.ReferenceCode;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.SequenceGenerator;
import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;
import com.jasify.schedule.appengine.util.KeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author krico
 * @since 05/08/15.
 */
public class InvoicePaymentProvider implements PaymentProvider<InvoicePayment> {
    public static final int INVOICE_EXPIRES_DAYS = 3;
    public static final BigDecimal INVOICE_FEE_FLAT = BigDecimal.ONE;
    private static final Logger log = LoggerFactory.getLogger(InvoicePaymentProvider.class);

    private final AttachmentDao attachmentDao = new AttachmentDao();
    private final SequenceGenerator invoiceNumberGenerator = new SequenceGenerator("BESR Invoice Number");

    private InvoicePaymentProvider() {
    }

    public static InvoicePaymentProvider instance() {
        return Singleton.INSTANCE;
    }

    @Override
    public InvoicePayment newPayment() {
        //TODO: Fetch these values from ApplicationData
        String invoiceRecipient = "Waldemar Arkadiusz Szarmach\nZuerichstrasse 77D\n8134 Adliswil";
        String invoiceAccount = "01-145-6";
        String invoiceSubscriber = "010001456";
        String invoiceIdentificationNumber = "30292600493040";
        String invoiceNumber = invoiceNumberGenerator.nextAsString();

        InvoicePayment payment = new InvoicePayment();
        payment.setRecipient(invoiceRecipient);
        payment.setAccount(invoiceAccount);
        payment.setSubscriber(invoiceSubscriber);
        payment.setReferenceCode(new ReferenceCode(invoiceIdentificationNumber, invoiceNumber).toReferenceCode());
        return payment;
    }

    @Override
    public void createPayment(InvoicePayment payment, GenericUrl baseUrl) throws PaymentException {
        Preconditions.checkArgument(payment.getId() != null, "Payment.Id == NULL");
        Preconditions.checkArgument(payment.getAttachmentRef().getKey() == null, "Payment.Attachment != NULL");
        payment.validate();

        if (payment.getFee() == null) {
            payment.setFee(INVOICE_FEE_FLAT.doubleValue());
            payment.setAmount(payment.getAmount() + payment.getFee());
        }

        PaymentSlip slip = PaymentSlipBuilder.isrChf()
                .account(payment.getAccount())
                .subscriber(payment.getSubscriber())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .recipient(payment.getRecipient())
                .referenceCode(payment.getReferenceCode())
                .build();

        byte[] data;
        try {
            data = slip.renderToBytes();
        } catch (Exception e) {
            log.error("Failed to create payment slip for payment: {}", payment.getId(), e);
            throw new PaymentException("Payment.CreateSlip");
        }
        Attachment attachment = AttachmentHelper.create(KeyUtil.keyToString(payment.getId()) + ".pdf", MediaType.PDF, data);
        try {
            attachmentDao.save(attachment);
        } catch (ModelException e) {
            log.error("Failed to save attachment for payment: {}", payment.getId(), e);
            throw new PaymentException("Attachment.Save");
        }
        payment.getAttachmentRef().setKey(attachment.getId());
        log.info("Generated invoice [{}] attachment [{}] for payment [{}]", payment.getReferenceCode(), attachment.getId(), payment.getId());
        payment.setState(PaymentStateEnum.Created);
    }

    @Override
    public void executePayment(InvoicePayment payment) throws PaymentException {
        payment.setState(PaymentStateEnum.Completed);
    }

    private static final class Singleton {
        private static final InvoicePaymentProvider INSTANCE = new InvoicePaymentProvider();
    }
}
