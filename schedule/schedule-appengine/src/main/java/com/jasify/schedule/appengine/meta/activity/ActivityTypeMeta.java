package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ActivityTypeMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.ActivityType> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> lcName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "lcName", "lcName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "description", "description");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.lang.Double> price = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.lang.Double>(this, "price", "price", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> location = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "location", "location");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.lang.Integer> maxSubscriptions = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, java.lang.Integer>(this, "maxSubscriptions", "maxSubscriptions", int.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType> colourTag = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType>(this, "colourTag", "colourTag");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization> organizationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityType, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization>(this, "organizationRef", "organizationRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Organization.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final com.jasify.schedule.appengine.model.LowerCaseListener slim3_lcNameAttributeListener = new com.jasify.schedule.appengine.model.LowerCaseListener();

    private static final ActivityTypeMeta slim3_singleton = new ActivityTypeMeta();

    /**
     * @return the singleton
     */
    public static ActivityTypeMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityTypeMeta() {
        super("ActivityType", com.jasify.schedule.appengine.model.activity.ActivityType.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.ActivityType entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.ActivityType model = new com.jasify.schedule.appengine.model.activity.ActivityType();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setLcName((java.lang.String) entity.getProperty("lcName"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        model.setPrice((java.lang.Double) entity.getProperty("price"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setLocation((java.lang.String) entity.getProperty("location"));
        model.setMaxSubscriptions(longToPrimitiveInt((java.lang.Long) entity.getProperty("maxSubscriptions")));
        model.setColourTag((java.lang.String) entity.getProperty("colourTag"));
        if (model.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) is null.");
        }
        model.getOrganizationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("organizationRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("name", m.getName());
        entity.setProperty("lcName", m.getLcName());
        entity.setProperty("description", m.getDescription());
        entity.setProperty("price", m.getPrice());
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("location", m.getLocation());
        entity.setProperty("maxSubscriptions", m.getMaxSubscriptions());
        entity.setProperty("colourTag", m.getColourTag());
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        entity.setProperty("organizationRef", m.getOrganizationRef().getKey());
        entity.setProperty("SV", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.ActivityType) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
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
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
        m.setModified(slim3_modifiedAttributeListener.prePut(m.getModified()));
        m.setLcName(slim3_lcNameAttributeListener.prePut(m.getLcName()));
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
        com.jasify.schedule.appengine.model.activity.ActivityType m = (com.jasify.schedule.appengine.model.activity.ActivityType) model;
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
        if(m.getLcName() != null){
            writer.setNextPropertyName("lcName");
            encoder0.encode(writer, m.getLcName());
        }
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        if(m.getPrice() != null){
            writer.setNextPropertyName("price");
            encoder0.encode(writer, m.getPrice());
        }
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getLocation() != null){
            writer.setNextPropertyName("location");
            encoder0.encode(writer, m.getLocation());
        }
        writer.setNextPropertyName("maxSubscriptions");
        encoder0.encode(writer, m.getMaxSubscriptions());
        if(m.getColourTag() != null){
            writer.setNextPropertyName("colourTag");
            encoder0.encode(writer, m.getColourTag());
        }
        if(m.getOrganizationRef() != null && m.getOrganizationRef().getKey() != null){
            writer.setNextPropertyName("organizationRef");
            encoder0.encode(writer, m.getOrganizationRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.ActivityType jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.ActivityType m = new com.jasify.schedule.appengine.model.activity.ActivityType();
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
        reader = rootReader.newObjectReader("lcName");
        m.setLcName(decoder0.decode(reader, m.getLcName()));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("price");
        m.setPrice(decoder0.decode(reader, m.getPrice()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("location");
        m.setLocation(decoder0.decode(reader, m.getLocation()));
        reader = rootReader.newObjectReader("maxSubscriptions");
        m.setMaxSubscriptions(decoder0.decode(reader, m.getMaxSubscriptions()));
        reader = rootReader.newObjectReader("colourTag");
        m.setColourTag(decoder0.decode(reader, m.getColourTag()));
        reader = rootReader.newObjectReader("organizationRef");
        decoder0.decode(reader, m.getOrganizationRef(), maxDepth, currentDepth);
        return m;
    }
}