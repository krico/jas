package com.jasify.schedule.appengine.meta.message;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ContactMessageMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.message.ContactMessage> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage> firstName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage>(this, "firstName", "firstName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage> lastName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage>(this, "lastName", "lastName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage> email = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage>(this, "email", "email");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage> subject = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage>(this, "subject", "subject");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage> message = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.message.ContactMessage>(this, "message", "message");

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final ContactMessageMeta slim3_singleton = new ContactMessageMeta();

    /**
     * @return the singleton
     */
    public static ContactMessageMeta get() {
       return slim3_singleton;
    }

    /** */
    public ContactMessageMeta() {
        super("ContactMessage", com.jasify.schedule.appengine.model.message.ContactMessage.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.message.ContactMessage entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.message.ContactMessage model = new com.jasify.schedule.appengine.model.message.ContactMessage();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setFirstName((java.lang.String) entity.getProperty("firstName"));
        model.setLastName((java.lang.String) entity.getProperty("lastName"));
        model.setEmail((java.lang.String) entity.getProperty("email"));
        model.setSubject((java.lang.String) entity.getProperty("subject"));
        model.setMessage((java.lang.String) entity.getProperty("message"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.message.ContactMessage m = (com.jasify.schedule.appengine.model.message.ContactMessage) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("firstName", m.getFirstName());
        entity.setProperty("lastName", m.getLastName());
        entity.setProperty("email", m.getEmail());
        entity.setProperty("subject", m.getSubject());
        entity.setProperty("message", m.getMessage());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.message.ContactMessage m = (com.jasify.schedule.appengine.model.message.ContactMessage) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.message.ContactMessage m = (com.jasify.schedule.appengine.model.message.ContactMessage) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.message.ContactMessage) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.message.ContactMessage m = (com.jasify.schedule.appengine.model.message.ContactMessage) model;
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
        com.jasify.schedule.appengine.model.message.ContactMessage m = (com.jasify.schedule.appengine.model.message.ContactMessage) model;
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
        if(m.getFirstName() != null){
            writer.setNextPropertyName("firstName");
            encoder0.encode(writer, m.getFirstName());
        }
        if(m.getLastName() != null){
            writer.setNextPropertyName("lastName");
            encoder0.encode(writer, m.getLastName());
        }
        if(m.getEmail() != null){
            writer.setNextPropertyName("email");
            encoder0.encode(writer, m.getEmail());
        }
        if(m.getSubject() != null){
            writer.setNextPropertyName("subject");
            encoder0.encode(writer, m.getSubject());
        }
        if(m.getMessage() != null){
            writer.setNextPropertyName("message");
            encoder0.encode(writer, m.getMessage());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.message.ContactMessage jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.message.ContactMessage m = new com.jasify.schedule.appengine.model.message.ContactMessage();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("firstName");
        m.setFirstName(decoder0.decode(reader, m.getFirstName()));
        reader = rootReader.newObjectReader("lastName");
        m.setLastName(decoder0.decode(reader, m.getLastName()));
        reader = rootReader.newObjectReader("email");
        m.setEmail(decoder0.decode(reader, m.getEmail()));
        reader = rootReader.newObjectReader("subject");
        m.setSubject(decoder0.decode(reader, m.getSubject()));
        reader = rootReader.newObjectReader("message");
        m.setMessage(decoder0.decode(reader, m.getMessage()));
        return m;
    }
}