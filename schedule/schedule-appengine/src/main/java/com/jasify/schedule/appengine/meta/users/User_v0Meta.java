package com.jasify.schedule.appengine.meta.users;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2014-12-23 00:43:01")
/** */
public final class User_v0Meta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.users.User_v0> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0> nameWithCase = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0>(this, "nameWithCase", "nameWithCase");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Email> email = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Email>(this, "email", "email", com.google.appengine.api.datastore.Email.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Text> about = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.Text>(this, "about", "about", com.google.appengine.api.datastore.Text.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.ShortBlob> password = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, com.google.appengine.api.datastore.ShortBlob>(this, "password", "password", com.google.appengine.api.datastore.ShortBlob.class);

    /** */
    public final org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Set<com.google.appengine.api.datastore.Category>, com.google.appengine.api.datastore.Category> permissions = new org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.users.User_v0, java.util.Set<com.google.appengine.api.datastore.Category>, com.google.appengine.api.datastore.Category>(this, "permissions", "permissions", java.util.Set.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final User_v0Meta slim3_singleton = new User_v0Meta();

    /**
     * @return the singleton
     */
    public static User_v0Meta get() {
       return slim3_singleton;
    }

    /** */
    public User_v0Meta() {
        super("User", com.jasify.schedule.appengine.model.users.User_v0.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.users.User_v0 entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.users.User_v0 model = new com.jasify.schedule.appengine.model.users.User_v0();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setNameWithCase((java.lang.String) entity.getProperty("nameWithCase"));
        model.setEmail((com.google.appengine.api.datastore.Email) entity.getProperty("email"));
        model.setAbout((com.google.appengine.api.datastore.Text) entity.getProperty("about"));
        model.setPassword((com.google.appengine.api.datastore.ShortBlob) entity.getProperty("password"));
        model.setPermissions(new java.util.HashSet<com.google.appengine.api.datastore.Category>(toList(com.google.appengine.api.datastore.Category.class, entity.getProperty("permissions"))));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.users.User_v0 m = (com.jasify.schedule.appengine.model.users.User_v0) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("name", m.getName());
        entity.setProperty("nameWithCase", m.getNameWithCase());
        entity.setProperty("email", m.getEmail());
        entity.setUnindexedProperty("about", m.getAbout());
        entity.setProperty("password", m.getPassword());
        entity.setProperty("permissions", m.getPermissions());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.users.User_v0 m = (com.jasify.schedule.appengine.model.users.User_v0) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.users.User_v0 m = (com.jasify.schedule.appengine.model.users.User_v0) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.users.User_v0) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.users.User_v0 m = (com.jasify.schedule.appengine.model.users.User_v0) model;
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
        com.jasify.schedule.appengine.model.users.User_v0 m = (com.jasify.schedule.appengine.model.users.User_v0) model;
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
        if(m.getNameWithCase() != null){
            writer.setNextPropertyName("nameWithCase");
            encoder0.encode(writer, m.getNameWithCase());
        }
        if(m.getEmail() != null){
            writer.setNextPropertyName("email");
            encoder0.encode(writer, m.getEmail());
        }
        if(m.getAbout() != null && m.getAbout().getValue() != null){
            writer.setNextPropertyName("about");
            encoder0.encode(writer, m.getAbout());
        }
        if(m.getPassword() != null){
            writer.setNextPropertyName("password");
            encoder0.encode(writer, m.getPassword());
        }
        if(m.getPermissions() != null){
            writer.setNextPropertyName("permissions");
            writer.beginArray();
            for(com.google.appengine.api.datastore.Category v : m.getPermissions()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.users.User_v0 jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.users.User_v0 m = new com.jasify.schedule.appengine.model.users.User_v0();
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
        reader = rootReader.newObjectReader("nameWithCase");
        m.setNameWithCase(decoder0.decode(reader, m.getNameWithCase()));
        reader = rootReader.newObjectReader("email");
        m.setEmail(decoder0.decode(reader, m.getEmail()));
        reader = rootReader.newObjectReader("about");
        m.setAbout(decoder0.decode(reader, m.getAbout()));
        reader = rootReader.newObjectReader("password");
        m.setPassword(decoder0.decode(reader, m.getPassword()));
        reader = rootReader.newObjectReader("permissions");
        {
            java.util.HashSet<com.google.appengine.api.datastore.Category> elements = new java.util.HashSet<com.google.appengine.api.datastore.Category>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("permissions");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    com.google.appengine.api.datastore.Category v = decoder0.decode(reader, (com.google.appengine.api.datastore.Category)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setPermissions(elements);
            }
        }
        return m;
    }
}