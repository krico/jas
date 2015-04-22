package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ActivityPackageMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date> validFrom = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date>(this, "validFrom", "validFrom", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date> validUntil = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.util.Date>(this, "validUntil", "validUntil", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer> maxExecutions = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer>(this, "maxExecutions", "maxExecutions", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer> executionCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer>(this, "executionCount", "executionCount", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Double> price = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Double>(this, "price", "price", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage>(this, "description", "description");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer> itemCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, java.lang.Integer>(this, "itemCount", "itemCount", int.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization> organizationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackage, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization>(this, "organizationRef", "organizationRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Organization.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ActivityPackageMeta slim3_singleton = new ActivityPackageMeta();

    /**
     * @return the singleton
     */
    public static ActivityPackageMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityPackageMeta() {
        super("ActivityPackage", com.jasify.schedule.appengine.model.activity.ActivityPackage.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.ActivityPackage entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage model = new com.jasify.schedule.appengine.model.activity.ActivityPackage();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setValidFrom((java.util.Date) entity.getProperty("validFrom"));
        model.setValidUntil((java.util.Date) entity.getProperty("validUntil"));
        model.setMaxExecutions(longToPrimitiveInt((java.lang.Long) entity.getProperty("maxExecutions")));
        model.setExecutionCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("executionCount")));
        model.setPrice((java.lang.Double) entity.getProperty("price"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        model.setItemCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("itemCount")));
        if (model.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) is null.");
        }
        model.getOrganizationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("organizationRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("validFrom", m.getValidFrom());
        entity.setProperty("validUntil", m.getValidUntil());
        entity.setProperty("maxExecutions", m.getMaxExecutions());
        entity.setProperty("executionCount", m.getExecutionCount());
        entity.setProperty("price", m.getPrice());
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("name", m.getName());
        entity.setProperty("description", m.getDescription());
        entity.setProperty("itemCount", m.getItemCount());
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        entity.setProperty("organizationRef", m.getOrganizationRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.ActivityPackage) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        m.getOrganizationRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
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
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = (com.jasify.schedule.appengine.model.activity.ActivityPackage) model;
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
        if(m.getValidFrom() != null){
            writer.setNextPropertyName("validFrom");
            encoder0.encode(writer, m.getValidFrom());
        }
        if(m.getValidUntil() != null){
            writer.setNextPropertyName("validUntil");
            encoder0.encode(writer, m.getValidUntil());
        }
        writer.setNextPropertyName("maxExecutions");
        encoder0.encode(writer, m.getMaxExecutions());
        writer.setNextPropertyName("executionCount");
        encoder0.encode(writer, m.getExecutionCount());
        if(m.getPrice() != null){
            writer.setNextPropertyName("price");
            encoder0.encode(writer, m.getPrice());
        }
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getName() != null){
            writer.setNextPropertyName("name");
            encoder0.encode(writer, m.getName());
        }
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        writer.setNextPropertyName("itemCount");
        encoder0.encode(writer, m.getItemCount());
        if(m.getOrganizationRef() != null && m.getOrganizationRef().getKey() != null){
            writer.setNextPropertyName("organizationRef");
            encoder0.encode(writer, m.getOrganizationRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.ActivityPackage jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.ActivityPackage m = new com.jasify.schedule.appengine.model.activity.ActivityPackage();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("validFrom");
        m.setValidFrom(decoder0.decode(reader, m.getValidFrom()));
        reader = rootReader.newObjectReader("validUntil");
        m.setValidUntil(decoder0.decode(reader, m.getValidUntil()));
        reader = rootReader.newObjectReader("maxExecutions");
        m.setMaxExecutions(decoder0.decode(reader, m.getMaxExecutions()));
        reader = rootReader.newObjectReader("executionCount");
        m.setExecutionCount(decoder0.decode(reader, m.getExecutionCount()));
        reader = rootReader.newObjectReader("price");
        m.setPrice(decoder0.decode(reader, m.getPrice()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("name");
        m.setName(decoder0.decode(reader, m.getName()));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("itemCount");
        m.setItemCount(decoder0.decode(reader, m.getItemCount()));
        reader = rootReader.newObjectReader("organizationRef");
        decoder0.decode(reader, m.getOrganizationRef(), maxDepth, currentDepth);
        return m;
    }
}