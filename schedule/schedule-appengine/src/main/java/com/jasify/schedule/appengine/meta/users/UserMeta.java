package com.jasify.schedule.appengine.meta.users;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class UserMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.users.User> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User> realName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User>(this, "realName", "realName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User> email = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User>(this, "email", "email");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.lang.Boolean> emailVerified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.lang.Boolean>(this, "emailVerified", "emailVerified", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, com.google.appengine.api.datastore.ShortBlob> password = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, com.google.appengine.api.datastore.ShortBlob>(this, "password", "password", com.google.appengine.api.datastore.ShortBlob.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.lang.Boolean> admin = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User, java.lang.Boolean>(this, "admin", "admin", boolean.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.User, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.UserDetail>, com.jasify.schedule.appengine.model.users.UserDetail> detailRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.users.User, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.UserDetail>, com.jasify.schedule.appengine.model.users.UserDetail>(this, "detailRef", "detailRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.UserDetail.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final com.jasify.schedule.appengine.model.LowerCaseListener slim3_nameAttributeListener = new com.jasify.schedule.appengine.model.LowerCaseListener();

    private static final com.jasify.schedule.appengine.model.LowerCaseListener slim3_emailAttributeListener = new com.jasify.schedule.appengine.model.LowerCaseListener();

    private static final UserMeta slim3_singleton = new UserMeta();

    /**
     * @return the singleton
     */
    public static UserMeta get() {
       return slim3_singleton;
    }

    /** */
    public UserMeta() {
        super("User", com.jasify.schedule.appengine.model.users.User.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.users.User entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.users.User model = new com.jasify.schedule.appengine.model.users.User();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setRealName((java.lang.String) entity.getProperty("realName"));
        model.setEmail((java.lang.String) entity.getProperty("email"));
        model.setEmailVerified(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("emailVerified")));
        model.setPassword((com.google.appengine.api.datastore.ShortBlob) entity.getProperty("password"));
        model.setAdmin(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("admin")));
        if (model.getDetailRef() == null) {
            throw new NullPointerException("The property(detailRef) is null.");
        }
        model.getDetailRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("detailRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("name", m.getName());
        entity.setProperty("realName", m.getRealName());
        entity.setProperty("email", m.getEmail());
        entity.setProperty("emailVerified", m.isEmailVerified());
        entity.setProperty("password", m.getPassword());
        entity.setProperty("admin", m.isAdmin());
        if (m.getDetailRef() == null) {
            throw new NullPointerException("The property(detailRef) must not be null.");
        }
        entity.setProperty("detailRef", m.getDetailRef().getKey());
        entity.setProperty("SV", 2);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.users.User) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
        if (m.getDetailRef() == null) {
            throw new NullPointerException("The property(detailRef) must not be null.");
        }
        m.getDetailRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
        m.setModified(slim3_modifiedAttributeListener.prePut(m.getModified()));
        m.setName(slim3_nameAttributeListener.prePut(m.getName()));
        m.setEmail(slim3_emailAttributeListener.prePut(m.getEmail()));
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
        com.jasify.schedule.appengine.model.users.User m = (com.jasify.schedule.appengine.model.users.User) model;
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
        if(m.getRealName() != null){
            writer.setNextPropertyName("realName");
            encoder0.encode(writer, m.getRealName());
        }
        if(m.getEmail() != null){
            writer.setNextPropertyName("email");
            encoder0.encode(writer, m.getEmail());
        }
        writer.setNextPropertyName("emailVerified");
        encoder0.encode(writer, m.isEmailVerified());
        if(m.getPassword() != null){
            writer.setNextPropertyName("password");
            encoder0.encode(writer, m.getPassword());
        }
        writer.setNextPropertyName("admin");
        encoder0.encode(writer, m.isAdmin());
        if(m.getDetailRef() != null && m.getDetailRef().getKey() != null){
            writer.setNextPropertyName("detailRef");
            encoder0.encode(writer, m.getDetailRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.users.User jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.users.User m = new com.jasify.schedule.appengine.model.users.User();
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
        reader = rootReader.newObjectReader("realName");
        m.setRealName(decoder0.decode(reader, m.getRealName()));
        reader = rootReader.newObjectReader("email");
        m.setEmail(decoder0.decode(reader, m.getEmail()));
        reader = rootReader.newObjectReader("emailVerified");
        m.setEmailVerified(decoder0.decode(reader, m.isEmailVerified()));
        reader = rootReader.newObjectReader("password");
        m.setPassword(decoder0.decode(reader, m.getPassword()));
        reader = rootReader.newObjectReader("admin");
        m.setAdmin(decoder0.decode(reader, m.isAdmin()));
        reader = rootReader.newObjectReader("detailRef");
        decoder0.decode(reader, m.getDetailRef(), maxDepth, currentDepth);
        return m;
    }
}