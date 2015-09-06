package com.jasify.schedule.appengine.dao;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ExampleChildMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.dao.ExampleChild> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild> childField = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild>(this, "childField", "childField");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild> data = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild>(this, "data", "data");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild> dataType = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild>(this, "dataType", "dataType");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Short> nativeShort = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Short>(this, "nativeShort", "nativeShort", short.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Integer> nativeInt = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Integer>(this, "nativeInt", "nativeInt", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Long> nativeLong = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Long>(this, "nativeLong", "nativeLong", long.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Boolean> nativeBoolean = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Boolean>(this, "nativeBoolean", "nativeBoolean", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Double> nativeDouble = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Double>(this, "nativeDouble", "nativeDouble", double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Float> nativeFloat = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Float>(this, "nativeFloat", "nativeFloat", float.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Short> shortObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Short>(this, "shortObject", "shortObject", java.lang.Short.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Integer> integerObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Integer>(this, "integerObject", "integerObject", java.lang.Integer.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Long> longObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Long>(this, "longObject", "longObject", java.lang.Long.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Boolean> booleanObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Boolean>(this, "booleanObject", "booleanObject", java.lang.Boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Double> doubleObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Double>(this, "doubleObject", "doubleObject", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Float> floatObject = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.lang.Float>(this, "floatObject", "floatObject", java.lang.Float.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.jasify.schedule.appengine.dao.ExampleEnum> exampleEnum = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.jasify.schedule.appengine.dao.ExampleEnum>(this, "exampleEnum", "exampleEnum", com.jasify.schedule.appengine.dao.ExampleEnum.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date> date = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, java.util.Date>(this, "date", "date", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.ShortBlob> shortBlob = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.ShortBlob>(this, "shortBlob", "shortBlob", com.google.appengine.api.datastore.ShortBlob.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Text> text = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Text>(this, "text", "text", com.google.appengine.api.datastore.Text.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Blob> blob = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Blob>(this, "blob", "blob", com.google.appengine.api.datastore.Blob.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Key>(this, "key", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Category> category = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Category>(this, "category", "category", com.google.appengine.api.datastore.Category.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Email> email = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Email>(this, "email", "email", com.google.appengine.api.datastore.Email.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.GeoPt> geoPt = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.GeoPt>(this, "geoPt", "geoPt", com.google.appengine.api.datastore.GeoPt.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.IMHandle> imHandle = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.IMHandle>(this, "imHandle", "imHandle", com.google.appengine.api.datastore.IMHandle.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Link> link = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Link>(this, "link", "link", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.PhoneNumber> phoneNumber = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.PhoneNumber>(this, "phoneNumber", "phoneNumber", com.google.appengine.api.datastore.PhoneNumber.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.PostalAddress> postalAddress = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.PostalAddress>(this, "postalAddress", "postalAddress", com.google.appengine.api.datastore.PostalAddress.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Rating> rating = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.dao.ExampleChild, com.google.appengine.api.datastore.Rating>(this, "rating", "rating", com.google.appengine.api.datastore.Rating.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ExampleChildMeta slim3_singleton = new ExampleChildMeta();

    /**
     * @return the singleton
     */
    public static ExampleChildMeta get() {
       return slim3_singleton;
    }

    /** */
    public ExampleChildMeta() {
        super("Example", com.jasify.schedule.appengine.dao.ExampleChild.class, java.util.Arrays.asList("com.jasify.schedule.appengine.dao.ExampleChild"));
    }

    @Override
    public com.jasify.schedule.appengine.dao.ExampleChild entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.dao.ExampleChild model = new com.jasify.schedule.appengine.dao.ExampleChild();
        model.setChildField((java.lang.String) entity.getProperty("childField"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setData((java.lang.String) entity.getProperty("data"));
        model.setDataType((java.lang.String) entity.getProperty("dataType"));
        model.setNativeShort(longToPrimitiveShort((java.lang.Long) entity.getProperty("nativeShort")));
        model.setNativeInt(longToPrimitiveInt((java.lang.Long) entity.getProperty("nativeInt")));
        model.setNativeLong(longToPrimitiveLong((java.lang.Long) entity.getProperty("nativeLong")));
        model.setNativeBoolean(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("nativeBoolean")));
        model.setNativeDouble(doubleToPrimitiveDouble((java.lang.Double) entity.getProperty("nativeDouble")));
        model.setNativeFloat(doubleToPrimitiveFloat((java.lang.Double) entity.getProperty("nativeFloat")));
        model.setShortObject(longToShort((java.lang.Long) entity.getProperty("shortObject")));
        model.setIntegerObject(longToInteger((java.lang.Long) entity.getProperty("integerObject")));
        model.setLongObject((java.lang.Long) entity.getProperty("longObject"));
        model.setBooleanObject((java.lang.Boolean) entity.getProperty("booleanObject"));
        model.setDoubleObject((java.lang.Double) entity.getProperty("doubleObject"));
        model.setFloatObject(doubleToFloat((java.lang.Double) entity.getProperty("floatObject")));
        model.setExampleEnum(stringToEnum(com.jasify.schedule.appengine.dao.ExampleEnum.class, (java.lang.String) entity.getProperty("exampleEnum")));
        model.setDate((java.util.Date) entity.getProperty("date"));
        model.setShortBlob((com.google.appengine.api.datastore.ShortBlob) entity.getProperty("shortBlob"));
        model.setText((com.google.appengine.api.datastore.Text) entity.getProperty("text"));
        model.setBlob((com.google.appengine.api.datastore.Blob) entity.getProperty("blob"));
        model.setKey((com.google.appengine.api.datastore.Key) entity.getProperty("key"));
        model.setCategory((com.google.appengine.api.datastore.Category) entity.getProperty("category"));
        model.setEmail((com.google.appengine.api.datastore.Email) entity.getProperty("email"));
        model.setGeoPt((com.google.appengine.api.datastore.GeoPt) entity.getProperty("geoPt"));
        model.setImHandle((com.google.appengine.api.datastore.IMHandle) entity.getProperty("imHandle"));
        model.setLink((com.google.appengine.api.datastore.Link) entity.getProperty("link"));
        model.setPhoneNumber((com.google.appengine.api.datastore.PhoneNumber) entity.getProperty("phoneNumber"));
        model.setPostalAddress((com.google.appengine.api.datastore.PostalAddress) entity.getProperty("postalAddress"));
        model.setRating((com.google.appengine.api.datastore.Rating) entity.getProperty("rating"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.dao.ExampleChild m = (com.jasify.schedule.appengine.dao.ExampleChild) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("childField", m.getChildField());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("data", m.getData());
        entity.setProperty("dataType", m.getDataType());
        entity.setProperty("nativeShort", m.getNativeShort());
        entity.setProperty("nativeInt", m.getNativeInt());
        entity.setProperty("nativeLong", m.getNativeLong());
        entity.setProperty("nativeBoolean", m.isNativeBoolean());
        entity.setProperty("nativeDouble", m.getNativeDouble());
        entity.setProperty("nativeFloat", m.getNativeFloat());
        entity.setProperty("shortObject", m.getShortObject());
        entity.setProperty("integerObject", m.getIntegerObject());
        entity.setProperty("longObject", m.getLongObject());
        entity.setProperty("booleanObject", m.getBooleanObject());
        entity.setProperty("doubleObject", m.getDoubleObject());
        entity.setProperty("floatObject", m.getFloatObject());
        entity.setProperty("exampleEnum", enumToString(m.getExampleEnum()));
        entity.setProperty("date", m.getDate());
        entity.setProperty("shortBlob", m.getShortBlob());
        entity.setUnindexedProperty("text", m.getText());
        entity.setProperty("blob", m.getBlob());
        entity.setProperty("key", m.getKey());
        entity.setProperty("category", m.getCategory());
        entity.setProperty("email", m.getEmail());
        entity.setProperty("geoPt", m.getGeoPt());
        entity.setProperty("imHandle", m.getImHandle());
        entity.setProperty("link", m.getLink());
        entity.setProperty("phoneNumber", m.getPhoneNumber());
        entity.setProperty("postalAddress", m.getPostalAddress());
        entity.setProperty("rating", m.getRating());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.dao.ExampleChild m = (com.jasify.schedule.appengine.dao.ExampleChild) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.dao.ExampleChild m = (com.jasify.schedule.appengine.dao.ExampleChild) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.dao.ExampleChild) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.dao.ExampleChild m = (com.jasify.schedule.appengine.dao.ExampleChild) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
        m.setModified(slim3_modifiedAttributeListener.prePut(m.getModified()));
    }

    @Override
    protected void postGet(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

    @Override
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }

    @Override
    protected void modelToJson(org.slim3.datastore.json.JsonWriter writer, java.lang.Object model, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.dao.ExampleChild m = (com.jasify.schedule.appengine.dao.ExampleChild) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getChildField() != null){
            writer.setNextPropertyName("childField");
            encoder0.encode(writer, m.getChildField());
        }
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getCreated() != null){
            writer.setNextPropertyName("created");
            encoder0.encode(writer, m.getCreated());
        }
        if(m.getModified() != null){
            writer.setNextPropertyName("modified");
            encoder0.encode(writer, m.getModified());
        }
        if(m.getData() != null){
            writer.setNextPropertyName("data");
            encoder0.encode(writer, m.getData());
        }
        if(m.getDataType() != null){
            writer.setNextPropertyName("dataType");
            encoder0.encode(writer, m.getDataType());
        }
        writer.setNextPropertyName("nativeShort");
        encoder0.encode(writer, m.getNativeShort());
        writer.setNextPropertyName("nativeInt");
        encoder0.encode(writer, m.getNativeInt());
        writer.setNextPropertyName("nativeLong");
        encoder0.encode(writer, m.getNativeLong());
        writer.setNextPropertyName("nativeBoolean");
        encoder0.encode(writer, m.isNativeBoolean());
        writer.setNextPropertyName("nativeDouble");
        encoder0.encode(writer, m.getNativeDouble());
        writer.setNextPropertyName("nativeFloat");
        encoder0.encode(writer, m.getNativeFloat());
        if(m.getShortObject() != null){
            writer.setNextPropertyName("shortObject");
            encoder0.encode(writer, m.getShortObject());
        }
        if(m.getIntegerObject() != null){
            writer.setNextPropertyName("integerObject");
            encoder0.encode(writer, m.getIntegerObject());
        }
        if(m.getLongObject() != null){
            writer.setNextPropertyName("longObject");
            encoder0.encode(writer, m.getLongObject());
        }
        if(m.getBooleanObject() != null){
            writer.setNextPropertyName("booleanObject");
            encoder0.encode(writer, m.getBooleanObject());
        }
        if(m.getDoubleObject() != null){
            writer.setNextPropertyName("doubleObject");
            encoder0.encode(writer, m.getDoubleObject());
        }
        if(m.getFloatObject() != null){
            writer.setNextPropertyName("floatObject");
            encoder0.encode(writer, m.getFloatObject());
        }
        if(m.getExampleEnum() != null){
            writer.setNextPropertyName("exampleEnum");
            encoder0.encode(writer, m.getExampleEnum());
        }
        if(m.getDate() != null){
            writer.setNextPropertyName("date");
            encoder0.encode(writer, m.getDate());
        }
        if(m.getShortBlob() != null){
            writer.setNextPropertyName("shortBlob");
            encoder0.encode(writer, m.getShortBlob());
        }
        if(m.getText() != null && m.getText().getValue() != null){
            writer.setNextPropertyName("text");
            encoder0.encode(writer, m.getText());
        }
        if(m.getBlob() != null && m.getBlob().getBytes() != null){
            writer.setNextPropertyName("blob");
            encoder0.encode(writer, m.getBlob());
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getCategory() != null){
            writer.setNextPropertyName("category");
            encoder0.encode(writer, m.getCategory());
        }
        if(m.getEmail() != null){
            writer.setNextPropertyName("email");
            encoder0.encode(writer, m.getEmail());
        }
        if(m.getGeoPt() != null){
            writer.setNextPropertyName("geoPt");
            encoder0.encode(writer, m.getGeoPt());
        }
        if(m.getImHandle() != null){
            writer.setNextPropertyName("imHandle");
            encoder0.encode(writer, m.getImHandle());
        }
        if(m.getLink() != null){
            writer.setNextPropertyName("link");
            encoder0.encode(writer, m.getLink());
        }
        if(m.getPhoneNumber() != null){
            writer.setNextPropertyName("phoneNumber");
            encoder0.encode(writer, m.getPhoneNumber());
        }
        if(m.getPostalAddress() != null){
            writer.setNextPropertyName("postalAddress");
            encoder0.encode(writer, m.getPostalAddress());
        }
        if(m.getRating() != null){
            writer.setNextPropertyName("rating");
            encoder0.encode(writer, m.getRating());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.dao.ExampleChild jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.dao.ExampleChild m = new com.jasify.schedule.appengine.dao.ExampleChild();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("childField");
        m.setChildField(decoder0.decode(reader, m.getChildField()));
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("data");
        m.setData(decoder0.decode(reader, m.getData()));
        reader = rootReader.newObjectReader("dataType");
        m.setDataType(decoder0.decode(reader, m.getDataType()));
        reader = rootReader.newObjectReader("nativeShort");
        m.setNativeShort(decoder0.decode(reader, m.getNativeShort()));
        reader = rootReader.newObjectReader("nativeInt");
        m.setNativeInt(decoder0.decode(reader, m.getNativeInt()));
        reader = rootReader.newObjectReader("nativeLong");
        m.setNativeLong(decoder0.decode(reader, m.getNativeLong()));
        reader = rootReader.newObjectReader("nativeBoolean");
        m.setNativeBoolean(decoder0.decode(reader, m.isNativeBoolean()));
        reader = rootReader.newObjectReader("nativeDouble");
        m.setNativeDouble(decoder0.decode(reader, m.getNativeDouble()));
        reader = rootReader.newObjectReader("nativeFloat");
        m.setNativeFloat(decoder0.decode(reader, m.getNativeFloat()));
        reader = rootReader.newObjectReader("shortObject");
        m.setShortObject(decoder0.decode(reader, m.getShortObject()));
        reader = rootReader.newObjectReader("integerObject");
        m.setIntegerObject(decoder0.decode(reader, m.getIntegerObject()));
        reader = rootReader.newObjectReader("longObject");
        m.setLongObject(decoder0.decode(reader, m.getLongObject()));
        reader = rootReader.newObjectReader("booleanObject");
        m.setBooleanObject(decoder0.decode(reader, m.getBooleanObject()));
        reader = rootReader.newObjectReader("doubleObject");
        m.setDoubleObject(decoder0.decode(reader, m.getDoubleObject()));
        reader = rootReader.newObjectReader("floatObject");
        m.setFloatObject(decoder0.decode(reader, m.getFloatObject()));
        reader = rootReader.newObjectReader("exampleEnum");
        m.setExampleEnum(decoder0.decode(reader, m.getExampleEnum(), com.jasify.schedule.appengine.dao.ExampleEnum.class));
        reader = rootReader.newObjectReader("date");
        m.setDate(decoder0.decode(reader, m.getDate()));
        reader = rootReader.newObjectReader("shortBlob");
        m.setShortBlob(decoder0.decode(reader, m.getShortBlob()));
        reader = rootReader.newObjectReader("text");
        m.setText(decoder0.decode(reader, m.getText()));
        reader = rootReader.newObjectReader("blob");
        m.setBlob(decoder0.decode(reader, m.getBlob()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("category");
        m.setCategory(decoder0.decode(reader, m.getCategory()));
        reader = rootReader.newObjectReader("email");
        m.setEmail(decoder0.decode(reader, m.getEmail()));
        reader = rootReader.newObjectReader("geoPt");
        m.setGeoPt(decoder0.decode(reader, m.getGeoPt()));
        reader = rootReader.newObjectReader("imHandle");
        m.setImHandle(decoder0.decode(reader, m.getImHandle()));
        reader = rootReader.newObjectReader("link");
        m.setLink(decoder0.decode(reader, m.getLink()));
        reader = rootReader.newObjectReader("phoneNumber");
        m.setPhoneNumber(decoder0.decode(reader, m.getPhoneNumber()));
        reader = rootReader.newObjectReader("postalAddress");
        m.setPostalAddress(decoder0.decode(reader, m.getPostalAddress()));
        reader = rootReader.newObjectReader("rating");
        m.setRating(decoder0.decode(reader, m.getRating()));
        return m;
    }
}