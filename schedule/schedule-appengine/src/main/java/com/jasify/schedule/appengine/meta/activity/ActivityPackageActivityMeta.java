package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ActivityPackageActivityMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityPackage>, com.jasify.schedule.appengine.model.activity.ActivityPackage> activityPackageRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityPackage>, com.jasify.schedule.appengine.model.activity.ActivityPackage>(this, "activityPackageRef", "activityPackageRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.activity.ActivityPackage.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.Activity>, com.jasify.schedule.appengine.model.activity.Activity> activityRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageActivity, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.Activity>, com.jasify.schedule.appengine.model.activity.Activity>(this, "activityRef", "activityRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.activity.Activity.class);

    private static final ActivityPackageActivityMeta slim3_singleton = new ActivityPackageActivityMeta();

    /**
     * @return the singleton
     */
    public static ActivityPackageActivityMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityPackageActivityMeta() {
        super("ActivityPackageActivity", com.jasify.schedule.appengine.model.activity.ActivityPackageActivity.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.ActivityPackageActivity entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity model = new com.jasify.schedule.appengine.model.activity.ActivityPackageActivity();
        model.setId(entity.getKey());
        if (model.getActivityPackageRef() == null) {
            throw new NullPointerException("The property(activityPackageRef) is null.");
        }
        model.getActivityPackageRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("activityPackageRef"));
        if (model.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) is null.");
        }
        model.getActivityRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("activityRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = (com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getActivityPackageRef() == null) {
            throw new NullPointerException("The property(activityPackageRef) must not be null.");
        }
        entity.setProperty("activityPackageRef", m.getActivityPackageRef().getKey());
        if (m.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) must not be null.");
        }
        entity.setProperty("activityRef", m.getActivityRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = (com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = (com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = (com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) model;
        if (m.getActivityPackageRef() == null) {
            throw new NullPointerException("The property(activityPackageRef) must not be null.");
        }
        m.getActivityPackageRef().assignKeyIfNecessary(ds);
        if (m.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) must not be null.");
        }
        m.getActivityRef().assignKeyIfNecessary(ds);
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
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = (com.jasify.schedule.appengine.model.activity.ActivityPackageActivity) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getActivityPackageRef() != null && m.getActivityPackageRef().getKey() != null){
            writer.setNextPropertyName("activityPackageRef");
            encoder0.encode(writer, m.getActivityPackageRef(), maxDepth, currentDepth);
        }
        if(m.getActivityRef() != null && m.getActivityRef().getKey() != null){
            writer.setNextPropertyName("activityRef");
            encoder0.encode(writer, m.getActivityRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.ActivityPackageActivity jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageActivity m = new com.jasify.schedule.appengine.model.activity.ActivityPackageActivity();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("activityPackageRef");
        decoder0.decode(reader, m.getActivityPackageRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("activityRef");
        decoder0.decode(reader, m.getActivityRef(), maxDepth, currentDepth);
        return m;
    }
}