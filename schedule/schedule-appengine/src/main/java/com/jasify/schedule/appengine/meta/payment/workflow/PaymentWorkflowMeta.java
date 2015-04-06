package com.jasify.schedule.appengine.meta.payment.workflow;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class PaymentWorkflowMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum> state = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, com.jasify.schedule.appengine.model.payment.PaymentStateEnum>(this, "state", "state", com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment> paymentRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment>(this, "paymentRef", "paymentRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.payment.Payment.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final PaymentWorkflowMeta slim3_singleton = new PaymentWorkflowMeta();

    /**
     * @return the singleton
     */
    public static PaymentWorkflowMeta get() {
       return slim3_singleton;
    }

    /** */
    public PaymentWorkflowMeta() {
        super("PaymentWorkflow", com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow entityToModel(com.google.appengine.api.datastore.Entity entity) {
        throw new java.lang.UnsupportedOperationException("The class(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is abstract.");
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        throw new java.lang.UnsupportedOperationException("The class(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is abstract.");
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        throw new java.lang.UnsupportedOperationException("The class(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is abstract.");
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow m = (com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) model;
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
        throw new java.lang.UnsupportedOperationException("The class(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is abstract.");
    }

    @Override
    protected com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        throw new java.lang.UnsupportedOperationException("The class(com.jasify.schedule.appengine.model.payment.workflow.PaymentWorkflow) is abstract.");
    }
}