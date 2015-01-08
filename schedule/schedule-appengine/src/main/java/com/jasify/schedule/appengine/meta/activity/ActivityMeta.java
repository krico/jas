package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-07 23:31:20")
/** */
public final class ActivityMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.Activity> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date> start = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date>(this, "start", "start", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date> finish = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.util.Date>(this, "finish", "finish", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Double> price = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Double>(this, "price", "price", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityType>, com.jasify.schedule.appengine.model.activity.ActivityType> activityTypeRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityType>, com.jasify.schedule.appengine.model.activity.ActivityType>(this, "activityTypeRef", "activityTypeRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.activity.ActivityType.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity> location = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity>(this, "location", "location");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Integer> maxSubscriptions = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Integer>(this, "maxSubscriptions", "maxSubscriptions", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Integer> subscriptionCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity, java.lang.Integer>(this, "subscriptionCount", "subscriptionCount", int.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.activity.Activity>(this, "description", "description");

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ActivityMeta slim3_singleton = new ActivityMeta();

    /**
     * @return the singleton
     */
    public static ActivityMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityMeta() {
        super("Activity", com.jasify.schedule.appengine.model.activity.Activity.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.Activity entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.Activity model = new com.jasify.schedule.appengine.model.activity.Activity();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setStart((java.util.Date) entity.getProperty("start"));
        model.setFinish((java.util.Date) entity.getProperty("finish"));
        model.setPrice((java.lang.Double) entity.getProperty("price"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        if (model.getActivityTypeRef() == null) {
            throw new NullPointerException("The property(activityTypeRef) is null.");
        }
        model.getActivityTypeRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("activityTypeRef"));
        model.setLocation((java.lang.String) entity.getProperty("location"));
        model.setMaxSubscriptions(longToPrimitiveInt((java.lang.Long) entity.getProperty("maxSubscriptions")));
        model.setSubscriptionCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("subscriptionCount")));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("start", m.getStart());
        entity.setProperty("finish", m.getFinish());
        entity.setProperty("price", m.getPrice());
        entity.setProperty("currency", m.getCurrency());
        if (m.getActivityTypeRef() == null) {
            throw new NullPointerException("The property(activityTypeRef) must not be null.");
        }
        entity.setProperty("activityTypeRef", m.getActivityTypeRef().getKey());
        entity.setProperty("location", m.getLocation());
        entity.setProperty("maxSubscriptions", m.getMaxSubscriptions());
        entity.setProperty("subscriptionCount", m.getSubscriptionCount());
        entity.setProperty("description", m.getDescription());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.Activity) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
        if (m.getActivityTypeRef() == null) {
            throw new NullPointerException("The property(activityTypeRef) must not be null.");
        }
        m.getActivityTypeRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
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
        com.jasify.schedule.appengine.model.activity.Activity m = (com.jasify.schedule.appengine.model.activity.Activity) model;
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
        if(m.getStart() != null){
            writer.setNextPropertyName("start");
            encoder0.encode(writer, m.getStart());
        }
        if(m.getFinish() != null){
            writer.setNextPropertyName("finish");
            encoder0.encode(writer, m.getFinish());
        }
        if(m.getPrice() != null){
            writer.setNextPropertyName("price");
            encoder0.encode(writer, m.getPrice());
        }
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getActivityTypeRef() != null && m.getActivityTypeRef().getKey() != null){
            writer.setNextPropertyName("activityTypeRef");
            encoder0.encode(writer, m.getActivityTypeRef(), maxDepth, currentDepth);
        }
        if(m.getLocation() != null){
            writer.setNextPropertyName("location");
            encoder0.encode(writer, m.getLocation());
        }
        writer.setNextPropertyName("maxSubscriptions");
        encoder0.encode(writer, m.getMaxSubscriptions());
        writer.setNextPropertyName("subscriptionCount");
        encoder0.encode(writer, m.getSubscriptionCount());
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.Activity jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.Activity m = new com.jasify.schedule.appengine.model.activity.Activity();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("start");
        m.setStart(decoder0.decode(reader, m.getStart()));
        reader = rootReader.newObjectReader("finish");
        m.setFinish(decoder0.decode(reader, m.getFinish()));
        reader = rootReader.newObjectReader("price");
        m.setPrice(decoder0.decode(reader, m.getPrice()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("activityTypeRef");
        decoder0.decode(reader, m.getActivityTypeRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("location");
        m.setLocation(decoder0.decode(reader, m.getLocation()));
        reader = rootReader.newObjectReader("maxSubscriptions");
        m.setMaxSubscriptions(decoder0.decode(reader, m.getMaxSubscriptions()));
        reader = rootReader.newObjectReader("subscriptionCount");
        m.setSubscriptionCount(decoder0.decode(reader, m.getSubscriptionCount()));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        return m;
    }
}