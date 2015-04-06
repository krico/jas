package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.payment.PaymentTypeEnum;

/**
 * @author krico
 * @since 02/04/15.
 */
public class JasCheckoutPaymentRequest {
    private String baseUrl;

    private PaymentTypeEnum type;

    private String cartId;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public PaymentTypeEnum getType() {
        return type;
    }

    public void setType(PaymentTypeEnum type) {
        this.type = type;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
