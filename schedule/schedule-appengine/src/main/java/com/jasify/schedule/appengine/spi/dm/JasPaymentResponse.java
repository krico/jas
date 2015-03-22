package com.jasify.schedule.appengine.spi.dm;

/**
 * @author krico
 * @since 24/02/15.
 */
public class JasPaymentResponse {
    private String approveUrl;

    public JasPaymentResponse() {
    }

    public JasPaymentResponse(String approveUrl) {
        this.approveUrl = approveUrl;
    }

    public String getApproveUrl() {
        return approveUrl;
    }

    public void setApproveUrl(String approveUrl) {
        this.approveUrl = approveUrl;
    }
}
