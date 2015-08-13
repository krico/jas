package com.jasify.schedule.appengine.meta.history;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class AuthHistoryMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.history.AuthHistory> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> remoteAddress = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "remoteAddress", "remoteAddress");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> provider = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "provider", "provider");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> providerUserId = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "providerUserId", "providerUserId");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> providerUserEmail = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "providerUserEmail", "providerUserEmail");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, com.jasify.schedule.appengine.model.history.HistoryTypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, com.jasify.schedule.appengine.model.history.HistoryTypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory>(this, "description", "description");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> currentUserRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.AuthHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "currentUserRef", "currentUserRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final AuthHistoryMeta slim3_singleton = new AuthHistoryMeta();

    /**
     * @return the singleton
     */
    public static AuthHistoryMeta get() {
       return slim3_singleton;
    }

    /** */
    public AuthHistoryMeta() {
        super("History", com.jasify.schedule.appengine.model.history.AuthHistory.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.history.AuthHistory"));
    }

    @Override
    public com.jasify.schedule.appengine.model.history.AuthHistory entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.history.AuthHistory model = new com.jasify.schedule.appengine.model.history.AuthHistory();
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setRemoteAddress((java.lang.String) entity.getProperty("remoteAddress"));
        model.setProvider((java.lang.String) entity.getProperty("provider"));
        model.setProviderUserId((java.lang.String) entity.getProperty("providerUserId"));
        model.setProviderUserEmail((java.lang.String) entity.getProperty("providerUserEmail"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        if (model.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) is null.");
        }
        model.getCurrentUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("currentUserRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("name", m.getName());
        entity.setProperty("remoteAddress", m.getRemoteAddress());
        entity.setProperty("provider", m.getProvider());
        entity.setProperty("providerUserId", m.getProviderUserId());
        entity.setProperty("providerUserEmail", m.getProviderUserEmail());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("description", m.getDescription());
        if (m.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) must not be null.");
        }
        entity.setProperty("currentUserRef", m.getCurrentUserRef().getKey());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.history.AuthHistory) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
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
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
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
        com.jasify.schedule.appengine.model.history.AuthHistory m = (com.jasify.schedule.appengine.model.history.AuthHistory) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getRemoteAddress() != null){
            writer.setNextPropertyName("remoteAddress");
            encoder0.encode(writer, m.getRemoteAddress());
        }
        if(m.getProvider() != null){
            writer.setNextPropertyName("provider");
            encoder0.encode(writer, m.getProvider());
        }
        if(m.getProviderUserId() != null){
            writer.setNextPropertyName("providerUserId");
            encoder0.encode(writer, m.getProviderUserId());
        }
        if(m.getProviderUserEmail() != null){
            writer.setNextPropertyName("providerUserEmail");
            encoder0.encode(writer, m.getProviderUserEmail());
        }
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
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        if(m.getCurrentUserRef() != null && m.getCurrentUserRef().getKey() != null){
            writer.setNextPropertyName("currentUserRef");
            encoder0.encode(writer, m.getCurrentUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.history.AuthHistory jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.history.AuthHistory m = new com.jasify.schedule.appengine.model.history.AuthHistory();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("remoteAddress");
        m.setRemoteAddress(decoder0.decode(reader, m.getRemoteAddress()));
        reader = rootReader.newObjectReader("provider");
        m.setProvider(decoder0.decode(reader, m.getProvider()));
        reader = rootReader.newObjectReader("providerUserId");
        m.setProviderUserId(decoder0.decode(reader, m.getProviderUserId()));
        reader = rootReader.newObjectReader("providerUserEmail");
        m.setProviderUserEmail(decoder0.decode(reader, m.getProviderUserEmail()));
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType(), com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("currentUserRef");
        decoder0.decode(reader, m.getCurrentUserRef(), maxDepth, currentDepth);
        return m;
    }
}