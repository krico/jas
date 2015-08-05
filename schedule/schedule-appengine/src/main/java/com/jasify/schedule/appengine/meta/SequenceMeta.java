package com.jasify.schedule.appengine.meta;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class SequenceMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.Sequence> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.Sequence, com.google.appengine.api.datastore.Key> name = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.Sequence, com.google.appengine.api.datastore.Key>(this, "__key__", "name", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.Sequence, java.lang.Long> next = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.Sequence, java.lang.Long>(this, "next", "next", java.lang.Long.class);

    private static final SequenceMeta slim3_singleton = new SequenceMeta();

    /**
     * @return the singleton
     */
    public static SequenceMeta get() {
       return slim3_singleton;
    }

    /** */
    public SequenceMeta() {
        super("SEQ", com.jasify.schedule.appengine.model.Sequence.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.Sequence entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.Sequence model = new com.jasify.schedule.appengine.model.Sequence();
        model.setName(entity.getKey());
        model.setNext((java.lang.Long) entity.getProperty("next"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.Sequence m = (com.jasify.schedule.appengine.model.Sequence) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getName() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getName());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("next", m.getNext());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.Sequence m = (com.jasify.schedule.appengine.model.Sequence) model;
        return m.getName();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.Sequence m = (com.jasify.schedule.appengine.model.Sequence) model;
        m.setName(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.Sequence) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
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
        com.jasify.schedule.appengine.model.Sequence m = (com.jasify.schedule.appengine.model.Sequence) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getNext() != null){
            writer.setNextPropertyName("next");
            encoder0.encode(writer, m.getNext());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.Sequence jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.Sequence m = new com.jasify.schedule.appengine.model.Sequence();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("next");
        m.setNext(decoder0.decode(reader, m.getNext()));
        return m;
    }
}