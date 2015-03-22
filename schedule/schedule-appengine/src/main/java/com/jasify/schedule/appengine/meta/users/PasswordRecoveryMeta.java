package com.jasify.schedule.appengine.meta.users;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-03-22 13:08:07")
/** */
public final class PasswordRecoveryMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, com.google.appengine.api.datastore.Key> code = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, com.google.appengine.api.datastore.Key>(this, "__key__", "code", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.PasswordRecovery, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final PasswordRecoveryMeta slim3_singleton = new PasswordRecoveryMeta();

    /**
     * @return the singleton
     */
    public static PasswordRecoveryMeta get() {
       return slim3_singleton;
    }

    /** */
    public PasswordRecoveryMeta() {
        super("PasswordRecovery", com.jasify.schedule.appengine.model.users.PasswordRecovery.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.users.PasswordRecovery entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery model = new com.jasify.schedule.appengine.model.users.PasswordRecovery();
        model.setCode(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getCode() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getCode());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
        return m.getCode();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
        m.setCode(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.users.PasswordRecovery) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
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
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = (com.jasify.schedule.appengine.model.users.PasswordRecovery) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getCode() != null){
            writer.setNextPropertyName("code");
            encoder0.encode(writer, m.getCode());
        }
        if(m.getCreated() != null){
            writer.setNextPropertyName("created");
            encoder0.encode(writer, m.getCreated());
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.users.PasswordRecovery jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.users.PasswordRecovery m = new com.jasify.schedule.appengine.model.users.PasswordRecovery();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("code");
        m.setCode(decoder0.decode(reader, m.getCode()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        return m;
    }
}