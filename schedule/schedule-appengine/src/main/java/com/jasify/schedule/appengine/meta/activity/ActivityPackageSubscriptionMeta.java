package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ActivityPackageSubscriptionMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription> {

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityPackageExecution>, com.jasify.schedule.appengine.model.activity.ActivityPackageExecution> activityPackageExecutionRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.ActivityPackageExecution>, com.jasify.schedule.appengine.model.activity.ActivityPackageExecution>(this, "activityPackageExecutionRef", "activityPackageExecutionRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.activity.ActivityPackageExecution.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.Activity>, com.jasify.schedule.appengine.model.activity.Activity> activityRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.activity.Activity>, com.jasify.schedule.appengine.model.activity.Activity>(this, "activityRef", "activityRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.activity.Activity.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer> transferRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer>(this, "transferRef", "transferRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Transfer.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ActivityPackageSubscriptionMeta slim3_singleton = new ActivityPackageSubscriptionMeta();

    /**
     * @return the singleton
     */
    public static ActivityPackageSubscriptionMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityPackageSubscriptionMeta() {
        super("Subscription", com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription"));
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription model = new com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription();
        if (model.getActivityPackageExecutionRef() == null) {
            throw new NullPointerException("The property(activityPackageExecutionRef) is null.");
        }
        model.getActivityPackageExecutionRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("activityPackageExecutionRef"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        if (model.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) is null.");
        }
        model.getActivityRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("activityRef"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
        if (model.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) is null.");
        }
        model.getTransferRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("transferRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getActivityPackageExecutionRef() == null) {
            throw new NullPointerException("The property(activityPackageExecutionRef) must not be null.");
        }
        entity.setProperty("activityPackageExecutionRef", m.getActivityPackageExecutionRef().getKey());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        if (m.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) must not be null.");
        }
        entity.setProperty("activityRef", m.getActivityRef().getKey());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
        if (m.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) must not be null.");
        }
        entity.setProperty("transferRef", m.getTransferRef().getKey());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
        if (m.getActivityPackageExecutionRef() == null) {
            throw new NullPointerException("The property(activityPackageExecutionRef) must not be null.");
        }
        m.getActivityPackageExecutionRef().assignKeyIfNecessary(ds);
        if (m.getActivityRef() == null) {
            throw new NullPointerException("The property(activityRef) must not be null.");
        }
        m.getActivityRef().assignKeyIfNecessary(ds);
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
        if (m.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) must not be null.");
        }
        m.getTransferRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
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
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = (com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getActivityPackageExecutionRef() != null && m.getActivityPackageExecutionRef().getKey() != null){
            writer.setNextPropertyName("activityPackageExecutionRef");
            encoder0.encode(writer, m.getActivityPackageExecutionRef(), maxDepth, currentDepth);
        }
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
        if(m.getActivityRef() != null && m.getActivityRef().getKey() != null){
            writer.setNextPropertyName("activityRef");
            encoder0.encode(writer, m.getActivityRef(), maxDepth, currentDepth);
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
        }
        if(m.getTransferRef() != null && m.getTransferRef().getKey() != null){
            writer.setNextPropertyName("transferRef");
            encoder0.encode(writer, m.getTransferRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription m = new com.jasify.schedule.appengine.model.activity.ActivityPackageSubscription();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("activityPackageExecutionRef");
        decoder0.decode(reader, m.getActivityPackageExecutionRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("activityRef");
        decoder0.decode(reader, m.getActivityRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("transferRef");
        decoder0.decode(reader, m.getTransferRef(), maxDepth, currentDepth);
        return m;
    }
}