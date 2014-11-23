package com.jasify.schedule.appengine.model.application;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.jasify.schedule.appengine.Constants;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;
import java.util.List;

/**
 * @author krico
 * @since 09/11/14.
 */
@Model(kind = "AppProp", schemaVersionName = Constants.SCHEMA_VERSION_NAME, schemaVersion = 0)
public class ApplicationProperty {
    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;
    private TypeEnum type;
    private String stringValue;
    private Boolean booleanValue;
    private Long longValue;
    private Text textValue;
    private Blob blobValue;
    private List<String> listValue;

    public <T> T getValue() {
        if (type == null) return null;
        Object ret = null;
        switch (type) {
            case String:
                ret = stringValue;
                break;
            case Boolean:
                ret = booleanValue;
                break;
            case Long:
                ret = longValue;
                break;
            case Text:
                ret = textValue;
                break;
            case Blob:
                ret = blobValue;
                break;
            case List:
                ret = listValue;
                break;
        }
        return (T) ret;
    }

    @SuppressWarnings("unchecked")
    public <T> void setValue(T value) {
        stringValue = null;
        booleanValue = null;
        longValue = null;
        textValue = null;
        blobValue = null;
        listValue = null;
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
        } else if (value instanceof List) {
            type = TypeEnum.List;
            listValue = (List<String>) value;
        }
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

    public List<String> getListValue() {
        return listValue;
    }

    public void setListValue(List<String> listValue) {
        this.listValue = listValue;
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

    public static enum TypeEnum {
        String, Boolean, Long, Text, Blob, List
    }
}
