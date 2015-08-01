package com.jasify.schedule.appengine.model.attachment;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.google.appengine.api.datastore.Key;
import com.google.common.net.MediaType;
import com.jasify.schedule.appengine.dao.attachment.AttachmentDao;
import com.jasify.schedule.appengine.model.EntityNotFoundException;
import com.jasify.schedule.appengine.util.EnvironmentUtil;
import com.jasify.schedule.appengine.util.KeyUtil;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author krico
 * @since 01/08/15.
 */
public final class AttachmentHelper {
    private AttachmentHelper() {
    }

    public static Attachment create(String name, MediaType mediaType, File source) throws IOException {
        return create(name, mediaType, FileUtils.readFileToByteArray(source));
    }

    public static Attachment create(String name, MediaType mediaType, byte[] data) {
        return create(name, mediaType.type() + "/" + mediaType.subtype(), data);
    }

    public static Attachment create(String name, String mimeType, byte[] data) {
        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setMimeType(mimeType);
        attachment.setData(TypeUtil.toBlob(data));
        return attachment;
    }

    public static GenericUrl makeViewUrl(Key attachmentId) throws EntityNotFoundException {
        AttachmentDao dao = new AttachmentDao();
        return makeViewUrl(dao.get(attachmentId));
    }

    public static GenericUrl makeViewUrl(Attachment attachment) {
        return makeUrl(attachment, false);
    }

    public static GenericUrl makeDownloadUrl(Key attachmentId) throws EntityNotFoundException {
        AttachmentDao dao = new AttachmentDao();
        return makeDownloadUrl(dao.get(attachmentId));
    }

    public static GenericUrl makeDownloadUrl(Attachment attachment) {
        return makeUrl(attachment, true);
    }

    private static GenericUrl makeUrl(Attachment attachment, boolean download) {
        GenericUrl retUrl = new GenericUrl(EnvironmentUtil.defaultVersionUrl());
        if (download) {
            retUrl.appendRawPath("/download/");
        } else {
            retUrl.appendRawPath("/view/");
        }
        retUrl.appendRawPath(KeyUtil.toHumanReadableString(attachment.getId()));

        if (StringUtils.isNotBlank(attachment.getName())) {
            try {
                retUrl.appendRawPath("/" + URLEncoder.encode(attachment.getName(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw Throwables.propagate(e); //TThis would mean UTF-8 not supported...
            }
        }
        return retUrl;
    }
}
