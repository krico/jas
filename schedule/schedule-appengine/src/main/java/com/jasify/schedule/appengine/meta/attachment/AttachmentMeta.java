package com.jasify.schedule.appengine.meta.attachment;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class AttachmentMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.attachment.Attachment> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment> mimeType = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment>(this, "mimeType", "mimeType");

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, com.google.appengine.api.datastore.Blob> data = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.attachment.Attachment, com.google.appengine.api.datastore.Blob>(this, "data", "data", com.google.appengine.api.datastore.Blob.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final AttachmentMeta slim3_singleton = new AttachmentMeta();

    /**
     * @return the singleton
     */
    public static AttachmentMeta get() {
       return slim3_singleton;
    }

    /** */
    public AttachmentMeta() {
        super("Attachment", com.jasify.schedule.appengine.model.attachment.Attachment.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.attachment.Attachment entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.attachment.Attachment model = new com.jasify.schedule.appengine.model.attachment.Attachment();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setMimeType((java.lang.String) entity.getProperty("mimeType"));
        model.setData((com.google.appengine.api.datastore.Blob) entity.getProperty("data"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.attachment.Attachment m = (com.jasify.schedule.appengine.model.attachment.Attachment) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("name", m.getName());
        entity.setProperty("mimeType", m.getMimeType());
        entity.setProperty("data", m.getData());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.attachment.Attachment m = (com.jasify.schedule.appengine.model.attachment.Attachment) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.attachment.Attachment m = (com.jasify.schedule.appengine.model.attachment.Attachment) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.attachment.Attachment) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.attachment.Attachment m = (com.jasify.schedule.appengine.model.attachment.Attachment) model;
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
        com.jasify.schedule.appengine.model.attachment.Attachment m = (com.jasify.schedule.appengine.model.attachment.Attachment) model;
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
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getMimeType() != null){
            writer.setNextPropertyName("mimeType");
            encoder0.encode(writer, m.getMimeType());
        }
        if(m.getData() != null && m.getData().getBytes() != null){
            writer.setNextPropertyName("data");
            encoder0.encode(writer, m.getData());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.attachment.Attachment jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.attachment.Attachment m = new com.jasify.schedule.appengine.model.attachment.Attachment();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("mimeType");
        m.setMimeType(decoder0.decode(reader, m.getMimeType()));
        reader = rootReader.newObjectReader("data");
        m.setData(decoder0.decode(reader, m.getData()));
        return m;
    }
}