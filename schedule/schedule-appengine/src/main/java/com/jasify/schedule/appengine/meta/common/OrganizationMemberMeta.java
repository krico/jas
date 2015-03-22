package com.jasify.schedule.appengine.meta.common;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-03-22 13:08:07")
/** */
public final class OrganizationMemberMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.common.OrganizationMember> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization> organizationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization>(this, "organizationRef", "organizationRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Organization.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Group>, com.jasify.schedule.appengine.model.common.Group> groupRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.common.OrganizationMember, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Group>, com.jasify.schedule.appengine.model.common.Group>(this, "groupRef", "groupRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Group.class);

    private static final OrganizationMemberMeta slim3_singleton = new OrganizationMemberMeta();

    /**
     * @return the singleton
     */
    public static OrganizationMemberMeta get() {
       return slim3_singleton;
    }

    /** */
    public OrganizationMemberMeta() {
        super("OrganizationMember", com.jasify.schedule.appengine.model.common.OrganizationMember.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.common.OrganizationMember entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.common.OrganizationMember model = new com.jasify.schedule.appengine.model.common.OrganizationMember();
        model.setId(entity.getKey());
        if (model.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) is null.");
        }
        model.getOrganizationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("organizationRef"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        if (model.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) is null.");
        }
        model.getGroupRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("groupRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.common.OrganizationMember m = (com.jasify.schedule.appengine.model.common.OrganizationMember) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        entity.setProperty("organizationRef", m.getOrganizationRef().getKey());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        if (m.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) must not be null.");
        }
        entity.setProperty("groupRef", m.getGroupRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.common.OrganizationMember m = (com.jasify.schedule.appengine.model.common.OrganizationMember) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.common.OrganizationMember m = (com.jasify.schedule.appengine.model.common.OrganizationMember) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.common.OrganizationMember) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.common.OrganizationMember m = (com.jasify.schedule.appengine.model.common.OrganizationMember) model;
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        m.getOrganizationRef().assignKeyIfNecessary(ds);
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
        if (m.getGroupRef() == null) {
            throw new NullPointerException("The property(groupRef) must not be null.");
        }
        m.getGroupRef().assignKeyIfNecessary(ds);
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
        com.jasify.schedule.appengine.model.common.OrganizationMember m = (com.jasify.schedule.appengine.model.common.OrganizationMember) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getOrganizationRef() != null && m.getOrganizationRef().getKey() != null){
            writer.setNextPropertyName("organizationRef");
            encoder0.encode(writer, m.getOrganizationRef(), maxDepth, currentDepth);
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        if(m.getGroupRef() != null && m.getGroupRef().getKey() != null){
            writer.setNextPropertyName("groupRef");
            encoder0.encode(writer, m.getGroupRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.common.OrganizationMember jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.common.OrganizationMember m = new com.jasify.schedule.appengine.model.common.OrganizationMember();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("organizationRef");
        decoder0.decode(reader, m.getOrganizationRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("groupRef");
        decoder0.decode(reader, m.getGroupRef(), maxDepth, currentDepth);
        return m;
    }
}