package com.jasify.schedule.appengine.model.attachment;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.HasId;
import com.jasify.schedule.appengine.util.TypeUtil;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;

/**
 * This is a generic representation of a "file" that needs to be persisted as an attachment to a datastore entity.
 * According to
 * <a href="https://cloud.google.com/appengine/docs/java/datastore/#Java_Quotas_and_limits">java datastore documentation</a>
 * an entity is limited to 1 megabyte.  So at a later point, if we need to support attachments larger than that, we can
 * add logic to integrate it to use <a href="https://cloud.google.com/appengine/docs/java/blobstore/">Blobstore</a>.
 *
 * @author krico
 * @since 31/07/15.
 */
@Model
public class Attachment implements HasId {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String name;

    /**
     * @see <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">http://www.iana.org/assignments/media-types/media-types.xhtml</a>
     */
    private String mimeType;

    private Blob data;

    public Attachment() {
    }

    public static Attachment create(String name, String mimeType, byte[] data) {
        Attachment attachment = new Attachment();
        attachment.setName(name);
        attachment.setMimeType(mimeType);
        attachment.setData(TypeUtil.toBlob(data));
        return attachment;
    }

    @Override
    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Blob getData() {
        return data;
    }

    public void setData(Blob data) {
        this.data = data;
    }
}
