package com.jasify.schedule.appengine.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-08 21:36:28")
/** */
public final class MailMessageMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.MailMessage> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage> pathInfo = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage>(this, "pathInfo", "pathInfo");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage> from = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage>(this, "from", "from");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage> subject = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.MailMessage>(this, "subject", "subject");

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, com.google.appengine.api.datastore.Blob> messageData = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.MailMessage, com.google.appengine.api.datastore.Blob>(this, "messageData", "messageData", com.google.appengine.api.datastore.Blob.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final MailMessageMeta slim3_singleton = new MailMessageMeta();

    /**
     * @return the singleton
     */
    public static MailMessageMeta get() {
       return slim3_singleton;
    }

    /** */
    public MailMessageMeta() {
        super("Msg", com.jasify.schedule.appengine.model.MailMessage.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.MailMessage entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.MailMessage model = new com.jasify.schedule.appengine.model.MailMessage();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setPathInfo((java.lang.String) entity.getProperty("pathInfo"));
        model.setFrom((java.lang.String) entity.getProperty("from"));
        model.setSubject((java.lang.String) entity.getProperty("subject"));
        model.setMessageData((com.google.appengine.api.datastore.Blob) entity.getProperty("messageData"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.MailMessage m = (com.jasify.schedule.appengine.model.MailMessage) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("pathInfo", m.getPathInfo());
        entity.setProperty("from", m.getFrom());
        entity.setProperty("subject", m.getSubject());
        entity.setProperty("messageData", m.getMessageData());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.MailMessage m = (com.jasify.schedule.appengine.model.MailMessage) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.MailMessage m = (com.jasify.schedule.appengine.model.MailMessage) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.MailMessage) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.MailMessage m = (com.jasify.schedule.appengine.model.MailMessage) model;
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
        com.jasify.schedule.appengine.model.MailMessage m = (com.jasify.schedule.appengine.model.MailMessage) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
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
        if(m.getPathInfo() != null){
            writer.setNextPropertyName("pathInfo");
            encoder0.encode(writer, m.getPathInfo());
        }
        if(m.getFrom() != null){
            writer.setNextPropertyName("from");
            encoder0.encode(writer, m.getFrom());
        }
        if(m.getSubject() != null){
            writer.setNextPropertyName("subject");
            encoder0.encode(writer, m.getSubject());
        }
        if(m.getMessageData() != null && m.getMessageData().getBytes() != null){
            writer.setNextPropertyName("messageData");
            encoder0.encode(writer, m.getMessageData());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.MailMessage jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.MailMessage m = new com.jasify.schedule.appengine.model.MailMessage();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("pathInfo");
        m.setPathInfo(decoder0.decode(reader, m.getPathInfo()));
        reader = rootReader.newObjectReader("from");
        m.setFrom(decoder0.decode(reader, m.getFrom()));
        reader = rootReader.newObjectReader("subject");
        m.setSubject(decoder0.decode(reader, m.getSubject()));
        reader = rootReader.newObjectReader("messageData");
        m.setMessageData(decoder0.decode(reader, m.getMessageData()));
        return m;
    }
}