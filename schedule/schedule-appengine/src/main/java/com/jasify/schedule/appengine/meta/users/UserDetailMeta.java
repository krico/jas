package com.jasify.schedule.appengine.meta.users;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class UserDetailMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.users.UserDetail> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, com.google.appengine.api.datastore.Text> about = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.users.UserDetail, com.google.appengine.api.datastore.Text>(this, "about", "about", com.google.appengine.api.datastore.Text.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final UserDetailMeta slim3_singleton = new UserDetailMeta();

    /**
     * @return the singleton
     */
    public static UserDetailMeta get() {
       return slim3_singleton;
    }

    /** */
    public UserDetailMeta() {
        super("UserDetail", com.jasify.schedule.appengine.model.users.UserDetail.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.users.UserDetail entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.users.UserDetail model = new com.jasify.schedule.appengine.model.users.UserDetail();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setAbout((com.google.appengine.api.datastore.Text) entity.getProperty("about"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.UserDetail m = (com.jasify.schedule.appengine.model.users.UserDetail) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setUnindexedProperty("about", m.getAbout());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.users.UserDetail m = (com.jasify.schedule.appengine.model.users.UserDetail) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.users.UserDetail m = (com.jasify.schedule.appengine.model.users.UserDetail) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.users.UserDetail) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.users.UserDetail m = (com.jasify.schedule.appengine.model.users.UserDetail) model;
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
        com.jasify.schedule.appengine.model.users.UserDetail m = (com.jasify.schedule.appengine.model.users.UserDetail) model;
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
        if(m.getAbout() != null && m.getAbout().getValue() != null){
            writer.setNextPropertyName("about");
            encoder0.encode(writer, m.getAbout());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.users.UserDetail jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.users.UserDetail m = new com.jasify.schedule.appengine.model.users.UserDetail();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("about");
        m.setAbout(decoder0.decode(reader, m.getAbout()));
        return m;
    }
}