package com.jasify.schedule.appengine.besr;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 07/08/15.
 */
public class ReferenceCode {
    private static final Logger log = LoggerFactory.getLogger(ReferenceCode.class);
    private final String identificationNumber;
    private final String invoiceNumber;

    public ReferenceCode(String identificationNumber, String invoiceNumber) {
        if (StringUtils.isBlank(identificationNumber)) {
            log.warn("Generating referenceCode and identificationNumber is NULL (set it to \"\")");
            identificationNumber = "";
        }
        identificationNumber = CheckDigit.onlyDigits(identificationNumber);

        invoiceNumber = Preconditions.checkNotNull(invoiceNumber, "invoiceNumber == NULL -> cannot generate refCode");
        invoiceNumber = CheckDigit.onlyDigits(invoiceNumber);
        Preconditions.checkArgument(StringUtils.isNotBlank(invoiceNumber), "invoiceNumber is blank -> cannot generate refCode");

        this.identificationNumber = identificationNumber;
        this.invoiceNumber = invoiceNumber;
    }

    public String toReferenceCode() {
        int idLen = StringUtils.length(identificationNumber);
        int restLen = CodeLine.REFERENCE_LENGTH /* check digit */ - 1 - idLen;
        String code = identificationNumber + CodeLine.leftPad(invoiceNumber, restLen);
        String ret = CheckDigit.complete(code);

        Preconditions.checkArgument(StringUtils.length(ret) == CodeLine.REFERENCE_LENGTH,
                "ReferenceCode.LENGTH != %s (%s)", CodeLine.REFERENCE_LENGTH, code);

        return ret;
    }

    @Override
    public String toString() {
        return "ReferenceCode{" +
                "identificationNumber='" + identificationNumber + '\'' +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                '}';
    }
}
