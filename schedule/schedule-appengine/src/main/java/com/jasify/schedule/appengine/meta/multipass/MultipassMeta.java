package com.jasify.schedule.appengine.meta.multipass;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class MultipassMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.multipass.Multipass> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass> name = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass>(this, "name", "name");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass> lcName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass>(this, "lcName", "lcName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass>(this, "description", "description");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Double> price = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Double>(this, "price", "price", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization> organizationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization>(this, "organizationRef", "organizationRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Organization.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Integer> expiresAfter = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Integer>(this, "expiresAfter", "expiresAfter", java.lang.Integer.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Integer> uses = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, java.lang.Integer>(this, "uses", "uses", java.lang.Integer.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter> activityTypeFilter = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter>(this, "activityTypeFilter", "activityTypeFilter", com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.DayFilter> dayFilter = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.DayFilter>(this, "dayFilter", "dayFilter", com.jasify.schedule.appengine.model.multipass.filter.DayFilter.class);

    /** */
    public final org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.TimeFilter> timeFilter = new org.slim3.datastore.UnindexedAttributeMeta<com.jasify.schedule.appengine.model.multipass.Multipass, com.jasify.schedule.appengine.model.multipass.filter.TimeFilter>(this, "timeFilter", "timeFilter", com.jasify.schedule.appengine.model.multipass.filter.TimeFilter.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final com.jasify.schedule.appengine.model.LowerCaseListener slim3_lcNameAttributeListener = new com.jasify.schedule.appengine.model.LowerCaseListener();

    private static final MultipassMeta slim3_singleton = new MultipassMeta();

    /**
     * @return the singleton
     */
    public static MultipassMeta get() {
       return slim3_singleton;
    }

    /** */
    public MultipassMeta() {
        super("Multipass", com.jasify.schedule.appengine.model.multipass.Multipass.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.multipass.Multipass entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.multipass.Multipass model = new com.jasify.schedule.appengine.model.multipass.Multipass();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setName((java.lang.String) entity.getProperty("name"));
        model.setLcName((java.lang.String) entity.getProperty("lcName"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        model.setPrice((java.lang.Double) entity.getProperty("price"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        if (model.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) is null.");
        }
        model.getOrganizationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("organizationRef"));
        model.setExpiresAfter(longToInteger((java.lang.Long) entity.getProperty("expiresAfter")));
        model.setUses(longToInteger((java.lang.Long) entity.getProperty("uses")));
        com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter _activityTypeFilter = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("activityTypeFilter"));
        model.setActivityTypeFilter(_activityTypeFilter);
        com.jasify.schedule.appengine.model.multipass.filter.DayFilter _dayFilter = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("dayFilter"));
        model.setDayFilter(_dayFilter);
        com.jasify.schedule.appengine.model.multipass.filter.TimeFilter _timeFilter = blobToSerializable((com.google.appengine.api.datastore.Blob) entity.getProperty("timeFilter"));
        model.setTimeFilter(_timeFilter);
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
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
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        entity.setProperty("organizationRef", m.getOrganizationRef().getKey());
        entity.setProperty("expiresAfter", m.getExpiresAfter());
        entity.setProperty("uses", m.getUses());
        entity.setUnindexedProperty("activityTypeFilter", serializableToBlob(m.getActivityTypeFilter()));
        entity.setUnindexedProperty("dayFilter", serializableToBlob(m.getDayFilter()));
        entity.setUnindexedProperty("timeFilter", serializableToBlob(m.getTimeFilter()));
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.multipass.Multipass) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
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
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
        m.setModified(slim3_modifiedAttributeListener.prePut(m.getModified()));
        m.setLcName(slim3_lcNameAttributeListener.prePut(m.getLcName()));
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
        com.jasify.schedule.appengine.model.multipass.Multipass m = (com.jasify.schedule.appengine.model.multipass.Multipass) model;
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
        if(m.getOrganizationRef() != null && m.getOrganizationRef().getKey() != null){
            writer.setNextPropertyName("organizationRef");
            encoder0.encode(writer, m.getOrganizationRef(), maxDepth, currentDepth);
        }
        if(m.getExpiresAfter() != null){
            writer.setNextPropertyName("expiresAfter");
            encoder0.encode(writer, m.getExpiresAfter());
        }
        if(m.getUses() != null){
            writer.setNextPropertyName("uses");
            encoder0.encode(writer, m.getUses());
        }
        if(m.getActivityTypeFilter() != null){
            writer.setNextPropertyName("activityTypeFilter");
            encoder0.encode(writer, m.getActivityTypeFilter());
        }
        if(m.getDayFilter() != null){
            writer.setNextPropertyName("dayFilter");
            encoder0.encode(writer, m.getDayFilter());
        }
        if(m.getTimeFilter() != null){
            writer.setNextPropertyName("timeFilter");
            encoder0.encode(writer, m.getTimeFilter());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.multipass.Multipass jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.multipass.Multipass m = new com.jasify.schedule.appengine.model.multipass.Multipass();
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
        reader = rootReader.newObjectReader("organizationRef");
        decoder0.decode(reader, m.getOrganizationRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("expiresAfter");
        m.setExpiresAfter(decoder0.decode(reader, m.getExpiresAfter()));
        reader = rootReader.newObjectReader("uses");
        m.setUses(decoder0.decode(reader, m.getUses()));
        reader = rootReader.newObjectReader("activityTypeFilter");
        m.setActivityTypeFilter(decoder0.decode(reader, m.getActivityTypeFilter(), com.jasify.schedule.appengine.model.multipass.filter.ActivityTypeFilter.class));
        reader = rootReader.newObjectReader("dayFilter");
        m.setDayFilter(decoder0.decode(reader, m.getDayFilter(), com.jasify.schedule.appengine.model.multipass.filter.DayFilter.class));
        reader = rootReader.newObjectReader("timeFilter");
        m.setTimeFilter(decoder0.decode(reader, m.getTimeFilter(), com.jasify.schedule.appengine.model.multipass.filter.TimeFilter.class));
        return m;
    }
}