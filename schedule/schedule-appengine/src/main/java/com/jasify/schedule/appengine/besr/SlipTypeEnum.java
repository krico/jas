package com.jasify.schedule.appengine.besr;

/**
 * <pre>
 * 01 = ISR in CHF
 * 03 = COD-ISR in CHF (cash-on-delivery)
 * 04 = ISR+ in CHF
 * 11 = ISR in CHF for credit to own account (section 4.3.3.3)
 * 14 = ISR+ in CHF for credit to own account (section 4.3.3.3)
 * 21 = ISR in EUR
 * 23 = ISR in EUR for credit to own account (section 4.3.3.3)
 * 31 = ISR+ in EUR
 * 33 = ISR+ in EUR for credit to own account (section 4.3.3.3)
 * </pre>
 *
 * @author krico
 * @since 22/07/15.
 */
public enum SlipTypeEnum {
    ISR_CHF("01", "CHF", true),
    Cash_On_Delivery_ISR_CHF("03", "CHF", true),
    ISR_Plus_CHF("04", "CHF", false),
    ISR_CHF_Credit_To_Own_Account("11", "CHF", true),
    ISR_Plus_CHF_Credit_To_Own_Account("14", "CHF", false),
    ISR_EUR("21", "EUR", true),
    ISR_EUR_Credit_To_Own_Account("23", "EUR", true),
    ISR_Plus_EUR("31", "EUR", false),
    ISR_Plus_EUR_Credit_To_Own_Account("33", "EUR", false);
    private final String code;
    private final String currency;
    private final boolean withAmount;

    SlipTypeEnum(String code, String currency, boolean withAmount) {
        this.code = code;
        this.currency = currency;
        this.withAmount = withAmount;
    }

    public String getCode() {
        return code;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isWithAmount() {
        return withAmount;
    }
}
