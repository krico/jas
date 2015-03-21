package com.jasify.schedule.appengine.meta.payment;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-03-21 17:11:31")
/** */
public final class PaymentMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.payment.Payment> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.jasify.schedule.appengine.model.payment.PaymentTypeEnum> type = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.jasify.schedule.appengine.model.payment.PaymentTypeEnum>(this, "type", "type", com.jasify.schedule.appengine.model.payment.PaymentTypeEnum.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.jasify.schedule.appengine.model.payment.PaymentStateEnum> state = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, com.jasify.schedule.appengine.model.payment.PaymentStateEnum>(this, "state", "state", com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.lang.Double> amount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.lang.Double>(this, "amount", "amount", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.lang.Double> fee = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.Payment, java.lang.Double>(this, "fee", "fee", java.lang.Double.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final PaymentMeta slim3_singleton = new PaymentMeta();

    /**
     * @return the singleton
     */
    public static PaymentMeta get() {
       return slim3_singleton;
    }

    /** */
    public PaymentMeta() {
        super("Payment", com.jasify.schedule.appengine.model.payment.Payment.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.payment.Payment entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.payment.Payment model = new com.jasify.schedule.appengine.model.payment.Payment();
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
        com.jasify.schedule.appengine.model.payment.Payment m = (com.jasify.schedule.appengine.model.payment.Payment) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("state", enumToString(m.getState()));
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("amount", m.getAmount());
        entity.setProperty("fee", m.getFee());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.payment.Payment m = (com.jasify.schedule.appengine.model.payment.Payment) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.payment.Payment m = (com.jasify.schedule.appengine.model.payment.Payment) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.payment.Payment) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.payment.Payment m = (com.jasify.schedule.appengine.model.payment.Payment) model;
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
        com.jasify.schedule.appengine.model.payment.Payment m = (com.jasify.schedule.appengine.model.payment.Payment) model;
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
    protected com.jasify.schedule.appengine.model.payment.Payment jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.payment.Payment m = new com.jasify.schedule.appengine.model.payment.Payment();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
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