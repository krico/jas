package com.jasify.schedule.appengine.meta.common;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-08 21:36:28")
/** */
public final class GroupUserMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.common.GroupUser> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Group>, com.jasify.schedule.appengine.model.common.Group> groupRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Group>, com.jasify.schedule.appengine.model.common.Group>(this, "groupRef", "groupRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Group.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.GroupUser, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final GroupUserMeta slim3_singleton = new GroupUserMeta();

    /**
     * @return the singleton
     */
    public static GroupUserMeta get() {
       return slim3_singleton;
    }

    /** */
    public GroupUserMeta() {
        super("GroupUser", com.jasify.schedule.appengine.model.common.GroupUser.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.common.GroupUser entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.common.GroupUser model = new com.jasify.schedule.appengine.model.common.GroupUser();
        model.setId(entity.getKey());
        if (model.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) is null.");
        }
        model.getGroupRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("groupRef"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.common.GroupUser m = (com.jasify.schedule.appengine.model.common.GroupUser) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) must not be null.");
        }
        entity.setProperty("groupRef", m.getGroupRef().getKey());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.common.GroupUser m = (com.jasify.schedule.appengine.model.common.GroupUser) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.common.GroupUser m = (com.jasify.schedule.appengine.model.common.GroupUser) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.common.GroupUser) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.common.GroupUser m = (com.jasify.schedule.appengine.model.common.GroupUser) model;
        if (m.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) must not be null.");
        }
        m.getGroupRef().assignKeyIfNecessary(ds);
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
        com.jasify.schedule.appengine.model.common.GroupUser m = (com.jasify.schedule.appengine.model.common.GroupUser) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getGroupRef() != null && m.getGroupRef().getKey() != null){
            writer.setNextPropertyName("groupRef");
            encoder0.encode(writer, m.getGroupRef(), maxDepth, currentDepth);
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.common.GroupUser jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.common.GroupUser m = new com.jasify.schedule.appengine.model.common.GroupUser();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("groupRef");
        decoder0.decode(reader, m.getGroupRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        return m;
    }
}