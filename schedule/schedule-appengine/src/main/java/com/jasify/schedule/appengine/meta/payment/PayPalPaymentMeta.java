package com.jasify.schedule.appengine.meta.payment;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-01-11 10:28:29")
/** */
public final class PayPalPaymentMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> externalId = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "externalId", "externalId");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> externalState = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "externalState", "externalState");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> payerId = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "payerId", "payerId");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link> selfUrl = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link>(this, "selfUrl", "selfUrl", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link> approveUrl = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link>(this, "approveUrl", "approveUrl", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link> executeUrl = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Link>(this, "executeUrl", "executeUrl", com.google.appengine.api.datastore.Link.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.jasify.schedule.appengine.model.payment.PaymentTypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.jasify.schedule.appengine.model.payment.PaymentTypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.payment.PaymentTypeEnum.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.jasify.schedule.appengine.model.payment.PaymentStateEnum> state = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, com.jasify.schedule.appengine.model.payment.PaymentStateEnum>(this, "state", "state", com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double> amount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double>(this, "amount", "amount", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double> fee = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double>(this, "fee", "fee", java.lang.Double.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final PayPalPaymentMeta slim3_singleton = new PayPalPaymentMeta();

    /**
     * @return the singleton
     */
    public static PayPalPaymentMeta get() {
       return slim3_singleton;
    }

    /** */
    public PayPalPaymentMeta() {
        super("Payment", com.jasify.schedule.appengine.model.payment.PayPalPayment.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.payment.PayPalPayment"));
    }

    @Override
    public com.jasify.schedule.appengine.model.payment.PayPalPayment entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.payment.PayPalPayment model = new com.jasify.schedule.appengine.model.payment.PayPalPayment();
        model.setExternalId((java.lang.String) entity.getProperty("externalId"));
        model.setExternalState((java.lang.String) entity.getProperty("externalState"));
        model.setPayerId((java.lang.String) entity.getProperty("payerId"));
        model.setSelfUrl((com.google.appengine.api.datastore.Link) entity.getProperty("selfUrl"));
        model.setApproveUrl((com.google.appengine.api.datastore.Link) entity.getProperty("approveUrl"));
        model.setExecuteUrl((com.google.appengine.api.datastore.Link) entity.getProperty("executeUrl"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.payment.PaymentTypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setState(stringToEnum(com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class, (java.lang.String) entity.getProperty("state")));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setAmount((java.lang.Double) entity.getProperty("amount"));
        model.setFee((java.lang.Double) entity.getProperty("fee"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("externalId", m.getExternalId());
        entity.setProperty("externalState", m.getExternalState());
        entity.setProperty("payerId", m.getPayerId());
        entity.setProperty("selfUrl", m.getSelfUrl());
        entity.setProperty("approveUrl", m.getApproveUrl());
        entity.setProperty("executeUrl", m.getExecuteUrl());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("state", enumToString(m.getState()));
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("amount", m.getAmount());
        entity.setProperty("fee", m.getFee());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.payment.PayPalPayment) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
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
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getExternalId() != null){
            writer.setNextPropertyName("externalId");
            encoder0.encode(writer, m.getExternalId());
        }
        if(m.getExternalState() != null){
            writer.setNextPropertyName("externalState");
            encoder0.encode(writer, m.getExternalState());
        }
        if(m.getPayerId() != null){
            writer.setNextPropertyName("payerId");
            encoder0.encode(writer, m.getPayerId());
        }
        if(m.getSelfUrl() != null){
            writer.setNextPropertyName("selfUrl");
            encoder0.encode(writer, m.getSelfUrl());
        }
        if(m.getApproveUrl() != null){
            writer.setNextPropertyName("approveUrl");
            encoder0.encode(writer, m.getApproveUrl());
        }
        if(m.getExecuteUrl() != null){
            writer.setNextPropertyName("executeUrl");
            encoder0.encode(writer, m.getExecuteUrl());
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
        if(m.getType() != null){
            writer.setNextPropertyName("type");
            encoder0.encode(writer, m.getType());
        }
        if(m.getState() != null){
            writer.setNextPropertyName("state");
            encoder0.encode(writer, m.getState());
        }
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getAmount() != null){
            writer.setNextPropertyName("amount");
            encoder0.encode(writer, m.getAmount());
        }
        if(m.getFee() != null){
            writer.setNextPropertyName("fee");
            encoder0.encode(writer, m.getFee());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.payment.PayPalPayment jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = new com.jasify.schedule.appengine.model.payment.PayPalPayment();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("externalId");
        m.setExternalId(decoder0.decode(reader, m.getExternalId()));
        reader = rootReader.newObjectReader("externalState");
        m.setExternalState(decoder0.decode(reader, m.getExternalState()));
        reader = rootReader.newObjectReader("payerId");
        m.setPayerId(decoder0.decode(reader, m.getPayerId()));
        reader = rootReader.newObjectReader("selfUrl");
        m.setSelfUrl(decoder0.decode(reader, m.getSelfUrl()));
        reader = rootReader.newObjectReader("approveUrl");
        m.setApproveUrl(decoder0.decode(reader, m.getApproveUrl()));
        reader = rootReader.newObjectReader("executeUrl");
        m.setExecuteUrl(decoder0.decode(reader, m.getExecuteUrl()));
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("type");
        m.setType(decoder0.decode(reader, m.getType(), com.jasify.schedule.appengine.model.payment.PaymentTypeEnum.class));
        reader = rootReader.newObjectReader("state");
        m.setState(decoder0.decode(reader, m.getState(), com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("amount");
        m.setAmount(decoder0.decode(reader, m.getAmount()));
        reader = rootReader.newObjectReader("fee");
        m.setFee(decoder0.decode(reader, m.getFee()));
        return m;
    }
}