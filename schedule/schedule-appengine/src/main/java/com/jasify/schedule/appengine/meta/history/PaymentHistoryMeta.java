package com.jasify.schedule.appengine.meta.history;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class PaymentHistoryMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.history.PaymentHistory> {

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment> paymentRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.payment.Payment>, com.jasify.schedule.appengine.model.payment.Payment>(this, "paymentRef", "paymentRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.payment.Payment.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, com.jasify.schedule.appengine.model.history.HistoryTypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, com.jasify.schedule.appengine.model.history.HistoryTypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory>(this, "description", "description");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> currentUserRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.history.PaymentHistory, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "currentUserRef", "currentUserRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final PaymentHistoryMeta slim3_singleton = new PaymentHistoryMeta();

    /**
     * @return the singleton
     */
    public static PaymentHistoryMeta get() {
       return slim3_singleton;
    }

    /** */
    public PaymentHistoryMeta() {
        super("History", com.jasify.schedule.appengine.model.history.PaymentHistory.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.history.PaymentHistory"));
    }

    @Override
    public com.jasify.schedule.appengine.model.history.PaymentHistory entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.history.PaymentHistory model = new com.jasify.schedule.appengine.model.history.PaymentHistory();
        if (model.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) is null.");
        }
        model.getPaymentRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("paymentRef"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        if (model.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) is null.");
        }
        model.getCurrentUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("currentUserRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) must not be null.");
        }
        entity.setProperty("paymentRef", m.getPaymentRef().getKey());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("description", m.getDescription());
        if (m.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) must not be null.");
        }
        entity.setProperty("currentUserRef", m.getCurrentUserRef().getKey());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.history.PaymentHistory) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        if (m.getPaymentRef() == null) {
            throw new NullPointerException("The property(paymentRef) must not be null.");
        }
        m.getPaymentRef().assignKeyIfNecessary(ds);
        if (m.getCurrentUserRef() == null) {
            throw new NullPointerException("The property(currentUserRef) must not be null.");
        }
        m.getCurrentUserRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        m.setCreated(slim3_createdAttributeListener.prePut(m.getCreated()));
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
        com.jasify.schedule.appengine.model.history.PaymentHistory m = (com.jasify.schedule.appengine.model.history.PaymentHistory) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getPaymentRef() != null && m.getPaymentRef().getKey() != null){
            writer.setNextPropertyName("paymentRef");
            encoder0.encode(writer, m.getPaymentRef(), maxDepth, currentDepth);
        }
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getCreated() != null){
            writer.setNextPropertyName("created");
            encoder0.encode(writer, m.getCreated());
        }
        if(m.getType() != null){
            writer.setNextPropertyName("type");
            encoder0.encode(writer, m.getType());
        }
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        if(m.getCurrentUserRef() != null && m.getCurrentUserRef().getKey() != null){
            writer.setNextPropertyName("currentUserRef");
            encoder0.encode(writer, m.getCurrentUserRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.history.PaymentHistory jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.history.PaymentHistory m = new com.jasify.schedule.appengine.model.history.PaymentHistory();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("paymentRef");
        decoder0.decode(reader, m.getPaymentRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType(), com.jasify.schedule.appengine.model.history.HistoryTypeEnum.class));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("currentUserRef");
        decoder0.decode(reader, m.getCurrentUserRef(), maxDepth, currentDepth);
        return m;
    }
}