package com.jasify.schedule.appengine.meta.payment.workflow;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class ActivityPaymentWorkflowMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key> activityId = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key>(this, "activityId", "activityId", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key> subscriptionId = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key>(this, "subscriptionId", "subscriptionId", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum> state = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum>(this, "state", "state", com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment> paymentRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment>(this, "paymentRef", "paymentRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.payment.Payment.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final ActivityPaymentWorkflowMeta slim3_singleton = new ActivityPaymentWorkflowMeta();

    /**
     * @return the singleton
     */
    public static ActivityPaymentWorkflowMeta get() {
       return slim3_singleton;
    }

    /** */
    public ActivityPaymentWorkflowMeta() {
        super("PaymentWorkflow", com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow"));
    }

    @Override
    public com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow model = new com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow();
        model.setActivityId((com.google.appengine.api.datastore.Key) entity.getProperty("activityId"));
        model.setSubscriptionId((com.google.appengine.api.datastore.Key) entity.getProperty("subscriptionId"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setState(stringToEnum(com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class, (java.lang.String) entity.getProperty("state")));
        if (model.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) is null.");
        }
        model.getPaymentRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("paymentRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("activityId", m.getActivityId());
        entity.setProperty("subscriptionId", m.getSubscriptionId());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("state", enumToString(m.getState()));
        if (m.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) must not be null.");
        }
        entity.setProperty("paymentRef", m.getPaymentRef().getKey());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
        if (m.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) must not be null.");
        }
        m.getPaymentRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
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
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getActivityId() != null){
            writer.setNextPropertyName("activityId");
            encoder0.encode(writer, m.getActivityId());
        }
        if(m.getSubscriptionId() != null){
            writer.setNextPropertyName("subscriptionId");
            encoder0.encode(writer, m.getSubscriptionId());
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
        if(m.getState() != null){
            writer.setNextPropertyName("state");
            encoder0.encode(writer, m.getState());
        }
        if(m.getPaymentRef() != null && m.getPaymentRef().getKey() != null){
            writer.setNextPropertyName("paymentRef");
            encoder0.encode(writer, m.getPaymentRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow m = new com.jasify.schedule.appengine.model.payment.workflow.ActivityPaymentWorkflow();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("activityId");
        m.setActivityId(decoder0.decode(reader, m.getActivityId()));
        reader = rootReader.newObjectReader("subscriptionId");
        m.setSubscriptionId(decoder0.decode(reader, m.getSubscriptionId()));
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("state");
        m.setState(decoder0.decode(reader, m.getState(), com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class));
        reader = rootReader.newObjectReader("paymentRef");
        decoder0.decode(reader, m.getPaymentRef(), maxDepth, currentDepth);
        return m;
    }
}