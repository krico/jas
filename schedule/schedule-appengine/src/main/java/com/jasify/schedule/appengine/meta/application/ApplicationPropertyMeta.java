package com.jasify.schedule.appengine.meta.application;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2014-12-23 00:43:01")
/** */
public final class ApplicationPropertyMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.jasify.schedule.appengine.model.application.ApplicationProperty.TypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.jasify.schedule.appengine.model.application.ApplicationProperty.TypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.application.ApplicationProperty.TypeEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty> stringValue = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty>(this, "stringValue", "stringValue");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.lang.Boolean> booleanValue = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.lang.Boolean>(this, "booleanValue", "booleanValue", java.lang.Boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.lang.Long> longValue = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.lang.Long>(this, "longValue", "longValue", java.lang.Long.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Text> textValue = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Text>(this, "textValue", "textValue", com.google.appengine.api.datastore.Text.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Blob> blobValue = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, com.google.appengine.api.datastore.Blob>(this, "blobValue", "blobValue", com.google.appengine.api.datastore.Blob.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.List<java.lang.String>> listValue = new org.slim3.datastore.StringCollectionAttributeMeta<com.jasify.schedule.appengine.model.application.ApplicationProperty, java.util.List<java.lang.String>>(this, "listValue", "listValue", java.util.List.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ApplicationPropertyMeta slim3_singleton = new ApplicationPropertyMeta();

    /**
     * @return the singleton
     */
    public static ApplicationPropertyMeta get() {
       return slim3_singleton;
    }

    /** */
    public ApplicationPropertyMeta() {
        super("AppProp", com.jasify.schedule.appengine.model.application.ApplicationProperty.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.application.ApplicationProperty entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.application.ApplicationProperty model = new com.jasify.schedule.appengine.model.application.ApplicationProperty();
        model.setKey(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.application.ApplicationProperty.TypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setStringValue((java.lang.String) entity.getProperty("stringValue"));
        model.setBooleanValue((java.lang.Boolean) entity.getProperty("booleanValue"));
        model.setLongValue((java.lang.Long) entity.getProperty("longValue"));
        model.setTextValue((com.google.appengine.api.datastore.Text) entity.getProperty("textValue"));
        model.setBlobValue((com.google.appengine.api.datastore.Blob) entity.getProperty("blobValue"));
        model.setListValue(toList(java.lang.String.class, entity.getProperty("listValue")));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = (com.jasify.schedule.appengine.model.application.ApplicationProperty) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("stringValue", m.getStringValue());
        entity.setProperty("booleanValue", m.getBooleanValue());
        entity.setProperty("longValue", m.getLongValue());
        entity.setUnindexedProperty("textValue", m.getTextValue());
        entity.setProperty("blobValue", m.getBlobValue());
        entity.setProperty("listValue", m.getListValue());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = (com.jasify.schedule.appengine.model.application.ApplicationProperty) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = (com.jasify.schedule.appengine.model.application.ApplicationProperty) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.application.ApplicationProperty) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = (com.jasify.schedule.appengine.model.application.ApplicationProperty) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
        m.setModified(slim3_modifiedAttributeListener.prePut(m.getModified()));
    }

    @Override
    protected void postGet(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "SV";
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
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = (com.jasify.schedule.appengine.model.application.ApplicationProperty) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getCreated() != null){
            writer.setNextPropertyName("created");
            encoder0.encode(writer, m.getCreated());
        }
        if(m.getModified() != null){
            writer.setNextPropertyName("modified");
            encoder0.encode(writer, m.getModified());
        }
        if(m.getType() != null){
            writer.setNextPropertyName("type");
            encoder0.encode(writer, m.getType());
        }
        if(m.getStringValue() != null){
            writer.setNextPropertyName("stringValue");
            encoder0.encode(writer, m.getStringValue());
        }
        if(m.getBooleanValue() != null){
            writer.setNextPropertyName("booleanValue");
            encoder0.encode(writer, m.getBooleanValue());
        }
        if(m.getLongValue() != null){
            writer.setNextPropertyName("longValue");
            encoder0.encode(writer, m.getLongValue());
        }
        if(m.getTextValue() != null && m.getTextValue().getValue() != null){
            writer.setNextPropertyName("textValue");
            encoder0.encode(writer, m.getTextValue());
        }
        if(m.getBlobValue() != null && m.getBlobValue().getBytes() != null){
            writer.setNextPropertyName("blobValue");
            encoder0.encode(writer, m.getBlobValue());
        }
        if(m.getListValue() != null){
            writer.setNextPropertyName("listValue");
            writer.beginArray();
            for(java.lang.String v : m.getListValue()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.application.ApplicationProperty jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.application.ApplicationProperty m = new com.jasify.schedule.appengine.model.application.ApplicationProperty();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType(), com.jasify.schedule.appengine.model.application.ApplicationProperty.TypeEnum.class));
        reader = rootReader.newObjectReader("stringValue");
        m.setStringValue(decoder0.decode(reader, m.getStringValue()));
        reader = rootReader.newObjectReader("booleanValue");
        m.setBooleanValue(decoder0.decode(reader, m.getBooleanValue()));
        reader = rootReader.newObjectReader("longValue");
        m.setLongValue(decoder0.decode(reader, m.getLongValue()));
        reader = rootReader.newObjectReader("textValue");
        m.setTextValue(decoder0.decode(reader, m.getTextValue()));
        reader = rootReader.newObjectReader("blobValue");
        m.setBlobValue(decoder0.decode(reader, m.getBlobValue()));
        reader = rootReader.newObjectReader("listValue");
        {
            java.util.ArrayList<java.lang.String> elements = new java.util.ArrayList<java.lang.String>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("listValue");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    java.lang.String v = decoder0.decode(reader, (java.lang.String)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setListValue(elements);
            }
        }
        return m;
    }
}