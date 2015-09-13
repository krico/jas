package com.jasify.schedule.appengine.spi.dm;

import com.jasify.schedule.appengine.model.attachment.Attachment;
import com.jasify.schedule.appengine.model.attachment.AttachmentHelper;

/**
 * @author krico
 * @since 08/08/15.
 */
public class JasInvoice {
    private String fileName;
    private String viewUrl;
    private String downloadUrl;

    public JasInvoice() {
    }

    public JasInvoice(Attachment attachment) {
        fileName = attachment.getName();
        viewUrl = AttachmentHelper.makeViewUrl(attachment).build();
        downloadUrl = AttachmentHelper.makeDownloadUrl(attachment).build();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
