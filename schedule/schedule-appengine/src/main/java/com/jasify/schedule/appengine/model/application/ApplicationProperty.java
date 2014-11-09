package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;

/**
 * Created by krico on 09/11/14.
 */
@Model(kind = "AppProp")
public class ApplicationProperty {
    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    public static enum TypeEnum {
        String, Boolean, Long, Text, Blob
    }

    private TypeEnum type;

    private String stringValue;

    private Boolean booleanValue;

    private Long longValue;

    private Text textValue;

    private Blob blobValue;

    public <T> void setValue(T value) {
        stringValue = null;
        booleanValue = null;
        longValue = null;
        textValue = null;
        blobValue = null;
        type = null;
        if (value instanceof String) {
            type = TypeEnum.String;
            stringValue = (String) value;
        } else if (value instanceof Boolean) {
            type = TypeEnum.Boolean;
            booleanValue = (Boolean) value;
        } else if (value instanceof Long) {
            type = TypeEnum.Long;
            longValue = (Long) value;
        } else if (value instanceof Text) {
            type = TypeEnum.Text;
            textValue = (Text) value;
        } else if (value instanceof Blob) {
            type = TypeEnum.Blob;
            blobValue = (Blob) value;
        }
    }

    public Object getValue() {
        if (type == null) return null;

        switch (type) {
            case String:
                return stringValue;
            case Boolean:
                return booleanValue;
            case Long:
                return longValue;
            case Text:
                return textValue;
            case Blob:
                return blobValue;
        }
        return null;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
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

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Text getTextValue() {
        return textValue;
    }

    public void setTextValue(Text textValue) {
        this.textValue = textValue;
    }

    public Blob getBlobValue() {
        return blobValue;
    }

    public void setBlobValue(Blob blobValue) {
        this.blobValue = blobValue;
    }

    @Override
    public String toString() {
        return "ApplicationProperty{" +
                "key=" + key +
                ", created=" + created +
                ", modified=" + modified +
                ", type=" + type +
                ", value=" + getValue() +
                '}';
    }
}
