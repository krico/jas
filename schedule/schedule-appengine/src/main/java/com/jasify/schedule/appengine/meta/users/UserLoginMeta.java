package com.jasify.schedule.appengine.meta.users;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-08 21:36:28")
/** */
public final class UserLoginMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.users.UserLogin> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin> provider = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin>(this, "provider", "provider");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin> userId = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin>(this, "userId", "userId");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin> email = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin>(this, "email", "email");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin> realName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin>(this, "realName", "realName");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Link> profile = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Link>(this, "profile", "profile", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Link> avatar = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, com.google.appengine.api.datastore.Link>(this, "avatar", "avatar", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.UserLogin, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final UserLoginMeta slim3_singleton = new UserLoginMeta();

    /**
     * @return the singleton
     */
    public static UserLoginMeta get() {
       return slim3_singleton;
    }

    /** */
    public UserLoginMeta() {
        super("UserLogin", com.jasify.schedule.appengine.model.users.UserLogin.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.users.UserLogin entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.users.UserLogin model = new com.jasify.schedule.appengine.model.users.UserLogin();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setProvider((java.lang.String) entity.getProperty("provider"));
        model.setUserId((java.lang.String) entity.getProperty("userId"));
        model.setEmail((java.lang.String) entity.getProperty("email"));
        model.setRealName((java.lang.String) entity.getProperty("realName"));
        model.setProfile((com.google.appengine.api.datastore.Link) entity.getProperty("profile"));
        model.setAvatar((com.google.appengine.api.datastore.Link) entity.getProperty("avatar"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("provider", m.getProvider());
        entity.setProperty("userId", m.getUserId());
        entity.setProperty("email", m.getEmail());
        entity.setProperty("realName", m.getRealName());
        entity.setProperty("profile", m.getProfile());
        entity.setProperty("avatar", m.getAvatar());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.users.UserLogin) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
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
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
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
        com.jasify.schedule.appengine.model.users.UserLogin m = (com.jasify.schedule.appengine.model.users.UserLogin) model;
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
        if(m.getProvider() != null){
            writer.setNextPropertyName("provider");
            encoder0.encode(writer, m.getProvider());
        }
        if(m.getUserId() != null){
            writer.setNextPropertyName("userId");
            encoder0.encode(writer, m.getUserId());
        }
        if(m.getEmail() != null){
            writer.setNextPropertyName("email");
            encoder0.encode(writer, m.getEmail());
        }
        if(m.getRealName() != null){
            writer.setNextPropertyName("realName");
            encoder0.encode(writer, m.getRealName());
        }
        if(m.getProfile() != null){
            writer.setNextPropertyName("profile");
            encoder0.encode(writer, m.getProfile());
        }
        if(m.getAvatar() != null){
            writer.setNextPropertyName("avatar");
            encoder0.encode(writer, m.getAvatar());
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.users.UserLogin jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.users.UserLogin m = new com.jasify.schedule.appengine.model.users.UserLogin();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("provider");
        m.setProvider(decoder0.decode(reader, m.getProvider()));
        reader = rootReader.newObjectReader("userId");
        m.setUserId(decoder0.decode(reader, m.getUserId()));
        reader = rootReader.newObjectReader("email");
        m.setEmail(decoder0.decode(reader, m.getEmail()));
        reader = rootReader.newObjectReader("realName");
        m.setRealName(decoder0.decode(reader, m.getRealName()));
        reader = rootReader.newObjectReader("profile");
        m.setProfile(decoder0.decode(reader, m.getProfile()));
        reader = rootReader.newObjectReader("avatar");
        m.setAvatar(decoder0.decode(reader, m.getAvatar()));
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        return m;
    }
}