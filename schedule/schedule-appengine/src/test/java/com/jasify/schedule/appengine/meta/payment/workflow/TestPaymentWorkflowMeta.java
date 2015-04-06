package com.jasify.schedule.appengine.meta.payment.workflow;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class TestPaymentWorkflowMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer> onCreatedCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer>(this, "onCreatedCount", "onCreatedCount", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer> onCanceledCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer>(this, "onCanceledCount", "onCanceledCount", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer> onCompletedCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.lang.Integer>(this, "onCompletedCount", "onCompletedCount", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum> state = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum>(this, "state", "state", com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment> paymentRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment>(this, "paymentRef", "paymentRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.payment.Payment.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final TestPaymentWorkflowMeta slim3_singleton = new TestPaymentWorkflowMeta();

    /**
     * @return the singleton
     */
    public static TestPaymentWorkflowMeta get() {
       return slim3_singleton;
    }

    /** */
    public TestPaymentWorkflowMeta() {
        super("PaymentWorkflow", com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow"));
    }

    @Override
    public com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow model = new com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow();
        model.setOnCreatedCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("onCreatedCount")));
        model.setOnCanceledCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("onCanceledCount")));
        model.setOnCompletedCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("onCompletedCount")));
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
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("onCreatedCount", m.getOnCreatedCount());
        entity.setProperty("onCanceledCount", m.getOnCanceledCount());
        entity.setProperty("onCompletedCount", m.getOnCompletedCount());
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
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
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
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
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
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        writer.setNextPropertyName("onCreatedCount");
        encoder0.encode(writer, m.getOnCreatedCount());
        writer.setNextPropertyName("onCanceledCount");
        encoder0.encode(writer, m.getOnCanceledCount());
        writer.setNextPropertyName("onCompletedCount");
        encoder0.encode(writer, m.getOnCompletedCount());
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
    protected com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow m = new com.jasify.schedule.appengine.model.payment.workflow.TestPaymentWorkflow();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("onCreatedCount");
        m.setOnCreatedCount(decoder0.decode(reader, m.getOnCreatedCount()));
        reader = rootReader.newObjectReader("onCanceledCount");
        m.setOnCanceledCount(decoder0.decode(reader, m.getOnCanceledCount()));
        reader = rootReader.newObjectReader("onCompletedCount");
        m.setOnCompletedCount(decoder0.decode(reader, m.getOnCompletedCount()));
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