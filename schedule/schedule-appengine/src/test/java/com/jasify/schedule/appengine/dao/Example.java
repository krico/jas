package com.jasify.schedule.appengine.dao;

import com.google.appengine.api.datastore.*;
import com.jasify.schedule.appengine.util.BeanUtil;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import java.util.Date;

/**
 * @author krico
 * @since 23/05/15.
 */
@Model
public class Example {
    @Attribute(primaryKey = true)
    private Key id;

    @Attribute(listener = CreationDate.class)
    private Date created;

    @Attribute(listener = ModificationDate.class)
    private Date modified;

    private String data;

    private String dataType;

    private short nativeShort;

    private int nativeInt;

    private long nativeLong;

    private boolean nativeBoolean;

    private double nativeDouble;

    private float nativeFloat;

    private Short shortObject;

    private Integer integerObject;

    private Long longObject;

    private Boolean booleanObject;

    private Double doubleObject;

    private Float floatObject;

    private ExampleEnum exampleEnum;

    private Date date;

    private ShortBlob shortBlob;

    private Text text;

    private Blob blob;

    private Key key;

    private Category category;

    private Email email;

    private GeoPt geoPt;

    private IMHandle imHandle;

    private Link link;

    private PhoneNumber phoneNumber;

    private PostalAddress postalAddress;

    private Rating rating;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getNativeInt() {
        return nativeInt;
    }

    public void setNativeInt(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    public short getNativeShort() {
        return nativeShort;
    }

    public void setNativeShort(short nativeShort) {
        this.nativeShort = nativeShort;
    }

    public long getNativeLong() {
        return nativeLong;
    }

    public void setNativeLong(long nativeLong) {
        this.nativeLong = nativeLong;
    }

    public boolean isNativeBoolean() {
        return nativeBoolean;
    }

    public void setNativeBoolean(boolean nativeBoolean) {
        this.nativeBoolean = nativeBoolean;
    }

    public double getNativeDouble() {
        return nativeDouble;
    }

    public void setNativeDouble(double nativeDouble) {
        this.nativeDouble = nativeDouble;
    }

    public float getNativeFloat() {
        return nativeFloat;
    }

    public void setNativeFloat(float nativeFloat) {
        this.nativeFloat = nativeFloat;
    }

    public Short getShortObject() {
        return shortObject;
    }

    public void setShortObject(Short shortObject) {
        this.shortObject = shortObject;
    }

    public Integer getIntegerObject() {
        return integerObject;
    }

    public void setIntegerObject(Integer integerObject) {
        this.integerObject = integerObject;
    }

    public Long getLongObject() {
        return longObject;
    }

    public void setLongObject(Long longObject) {
        this.longObject = longObject;
    }

    public Boolean getBooleanObject() {
        return booleanObject;
    }

    public void setBooleanObject(Boolean booleanObject) {
        this.booleanObject = booleanObject;
    }

    public Double getDoubleObject() {
        return doubleObject;
    }

    public void setDoubleObject(Double doubleObject) {
        this.doubleObject = doubleObject;
    }

    public Float getFloatObject() {
        return floatObject;
    }

    public void setFloatObject(Float floatObject) {
        this.floatObject = floatObject;
    }

    public ExampleEnum getExampleEnum() {
        return exampleEnum;
    }

    public void setExampleEnum(ExampleEnum exampleEnum) {
        this.exampleEnum = exampleEnum;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ShortBlob getShortBlob() {
        return shortBlob;
    }

    public void setShortBlob(ShortBlob shortBlob) {
        this.shortBlob = shortBlob;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Blob getBlob() {
        return blob;
    }

    public void setBlob(Blob blob) {
        this.blob = blob;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public GeoPt getGeoPt() {
        return geoPt;
    }

    public void setGeoPt(GeoPt geoPt) {
        this.geoPt = geoPt;
    }

    public IMHandle getImHandle() {
        return imHandle;
    }

    public void setImHandle(IMHandle imHandle) {
        this.imHandle = imHandle;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return BeanUtil.beanMap(this).equals(BeanUtil.beanMap(o));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Example{" +
                "id=" + id +
                ", created=" + created +
                ", modified=" + modified +
                ", data='" + data + '\'' +
                ", dataType='" + dataType + '\'' +
                ", nativeShort=" + nativeShort +
                ", nativeInt=" + nativeInt +
                ", nativeLong=" + nativeLong +
                ", nativeBoolean=" + nativeBoolean +
                ", nativeDouble=" + nativeDouble +
                ", nativeFloat=" + nativeFloat +
                ", shortObject=" + shortObject +
                ", integerObject=" + integerObject +
                ", longObject=" + longObject +
                ", booleanObject=" + booleanObject +
                ", doubleObject=" + doubleObject +
                ", floatObject=" + floatObject +
                ", exampleEnum=" + exampleEnum +
                ", date=" + date +
                ", shortBlob=" + shortBlob +
                ", text=" + text +
                ", blob=" + blob +
                ", key=" + key +
                ", category=" + category +
                ", email=" + email +
                ", geoPt=" + geoPt +
                ", imHandle=" + imHandle +
                ", link=" + link +
                ", phoneNumber=" + phoneNumber +
                ", postalAddress=" + postalAddress +
                ", rating=" + rating +
                '}';
    }

}
