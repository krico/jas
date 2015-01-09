package com.jasify.schedule.appengine.meta.application;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-09 01:22:49")
/** */
public final class ApplicationMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.application.Application> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, com.google.appengine.api.datastore.Key> name = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, com.google.appengine.api.datastore.Key>(this, "__key__", "name", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.application.Application, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ApplicationMeta slim3_singleton = new ApplicationMeta();

    /**
     * @return the singleton
     */
    public static ApplicationMeta get() {
       return slim3_singleton;
    }

    /** */
    public ApplicationMeta() {
        super("App", com.jasify.schedule.appengine.model.application.Application.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.application.Application entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.application.Application model = new com.jasify.schedule.appengine.model.application.Application();
        model.setName(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.application.Application m = (com.jasify.schedule.appengine.model.application.Application) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getName() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getName());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.application.Application m = (com.jasify.schedule.appengine.model.application.Application) model;
        return m.getName();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.application.Application m = (com.jasify.schedule.appengine.model.application.Application) model;
        m.setName(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.application.Application) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.application.Application m = (com.jasify.schedule.appengine.model.application.Application) model;
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
        com.jasify.schedule.appengine.model.application.Application m = (com.jasify.schedule.appengine.model.application.Application) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getCreated() != null){
            writer.setNextPropertyName("created");
            encoder0.encode(writer, m.getCreated());
        }
        if(m.getModified() != null){
            writer.setNextPropertyName("modified");
            encoder0.encode(writer, m.getModified());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.application.Application jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.application.Application m = new com.jasify.schedule.appengine.model.application.Application();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("properties");
        return m;
    }
}