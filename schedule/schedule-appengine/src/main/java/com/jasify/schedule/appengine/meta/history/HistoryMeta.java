package com.jasify.schedule.appengine.meta.history;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class HistoryMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.history.History> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, com.jasify.schedule.appengine.model.history.HistoryTypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.History, com.jasify.schedule.appengine.model.history.HistoryTypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.History> message = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.History>(this, "message", "message");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.History, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> currentUserRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.History, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "currentUserRef", "currentUserRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final HistoryMeta slim3_singleton = new HistoryMeta();

    /**
     * @return the singleton
     */
    public static HistoryMeta get() {
       return slim3_singleton;
    }

    /** */
    public HistoryMeta() {
        super("History", com.jasify.schedule.appengine.model.history.History.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.history.History entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.history.History model = new com.jasify.schedule.appengine.model.history.History();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setMessage((java.lang.String) entity.getProperty("message"));
        if (model.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) is null.");
        }
        model.getCurrentUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("currentUserRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("message", m.getMessage());
        if (m.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) must not be null.");
        }
        entity.setProperty("currentUserRef", m.getCurrentUserRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.history.History) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
        if (m.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) must not be null.");
        }
        m.getCurrentUserRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
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
        com.jasify.schedule.appengine.model.history.History m = (com.jasify.schedule.appengine.model.history.History) model;
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
        if(m.getType() != null){
            writer.setNextPropertyName("type");
            encoder0.encode(writer, m.getType());
        }
        if(m.getMessage() != null){
            writer.setNextPropertyName("message");
            encoder0.encode(writer, m.getMessage());
        }
        if(m.getCurrentUserRef() != null && m.getCurrentUserRef().getKey() != null){
            writer.setNextPropertyName("currentUserRef");
            encoder0.encode(writer, m.getCurrentUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.history.History jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.history.History m = new com.jasify.schedule.appengine.model.history.History();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType(), com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class));
        reader = rootReader.newObjectReader("message");
        m.setMessage(decoder0.decode(reader, m.getMessage()));
        reader = rootReader.newObjectReader("currentUserRef");
        decoder0.decode(reader, m.getCurrentUserRef(), maxDepth, currentDepth);
        return m;
    }
}