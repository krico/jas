package com.jasify.schedule.appengine.meta.balance;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class TransferMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.balance.Transfer> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.lang.Double> amount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.lang.Double>(this, "amount", "amount", double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.lang.Double> unpaid = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, java.lang.Double>(this, "unpaid", "unpaid", double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer>(this, "description", "description");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer> reference = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer>(this, "reference", "reference");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transaction>, com.jasify.schedule.appengine.model.balance.Transaction> beneficiaryLegRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transaction>, com.jasify.schedule.appengine.model.balance.Transaction>(this, "beneficiaryLegRef", "beneficiaryLegRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Transaction.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transaction>, com.jasify.schedule.appengine.model.balance.Transaction> payerLegRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transfer, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transaction>, com.jasify.schedule.appengine.model.balance.Transaction>(this, "payerLegRef", "payerLegRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Transaction.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final TransferMeta slim3_singleton = new TransferMeta();

    /**
     * @return the singleton
     */
    public static TransferMeta get() {
       return slim3_singleton;
    }

    /** */
    public TransferMeta() {
        super("Transfer", com.jasify.schedule.appengine.model.balance.Transfer.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.balance.Transfer entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.balance.Transfer model = new com.jasify.schedule.appengine.model.balance.Transfer();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setAmount(doubleToPrimitiveDouble((java.lang.Double) entity.getProperty("amount")));
        model.setUnpaid(doubleToPrimitiveDouble((java.lang.Double) entity.getProperty("unpaid")));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        model.setReference((java.lang.String) entity.getProperty("reference"));
        if (model.getBeneficiaryLegRef() == null) {
            throw new NullPointerException("The property(beneficiaryLegRef) is null.");
        }
        model.getBeneficiaryLegRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("beneficiaryLegRef"));
        if (model.getPayerLegRef() == null) {
            throw new NullPointerException("The property(payerLegRef) is null.");
        }
        model.getPayerLegRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("payerLegRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("amount", m.getAmount());
        entity.setProperty("unpaid", m.getUnpaid());
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("description", m.getDescription());
        entity.setProperty("reference", m.getReference());
        if (m.getBeneficiaryLegRef() == null) {
            throw new NullPointerException("The property(beneficiaryLegRef) must not be null.");
        }
        entity.setProperty("beneficiaryLegRef", m.getBeneficiaryLegRef().getKey());
        if (m.getPayerLegRef() == null) {
            throw new NullPointerException("The property(payerLegRef) must not be null.");
        }
        entity.setProperty("payerLegRef", m.getPayerLegRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.balance.Transfer) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
        if (m.getBeneficiaryLegRef() == null) {
            throw new NullPointerException("The property(beneficiaryLegRef) must not be null.");
        }
        m.getBeneficiaryLegRef().assignKeyIfNecessary(ds);
        if (m.getPayerLegRef() == null) {
            throw new NullPointerException("The property(payerLegRef) must not be null.");
        }
        m.getPayerLegRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
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
        com.jasify.schedule.appengine.model.balance.Transfer m = (com.jasify.schedule.appengine.model.balance.Transfer) model;
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
        writer.setNextPropertyName("amount");
        encoder0.encode(writer, m.getAmount());
        writer.setNextPropertyName("unpaid");
        encoder0.encode(writer, m.getUnpaid());
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        if(m.getReference() != null){
            writer.setNextPropertyName("reference");
            encoder0.encode(writer, m.getReference());
        }
        if(m.getBeneficiaryLegRef() != null && m.getBeneficiaryLegRef().getKey() != null){
            writer.setNextPropertyName("beneficiaryLegRef");
            encoder0.encode(writer, m.getBeneficiaryLegRef(), maxDepth, currentDepth);
        }
        if(m.getPayerLegRef() != null && m.getPayerLegRef().getKey() != null){
            writer.setNextPropertyName("payerLegRef");
            encoder0.encode(writer, m.getPayerLegRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.balance.Transfer jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.balance.Transfer m = new com.jasify.schedule.appengine.model.balance.Transfer();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("amount");
        m.setAmount(decoder0.decode(reader, m.getAmount()));
        reader = rootReader.newObjectReader("unpaid");
        m.setUnpaid(decoder0.decode(reader, m.getUnpaid()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("reference");
        m.setReference(decoder0.decode(reader, m.getReference()));
        reader = rootReader.newObjectReader("beneficiaryLegRef");
        decoder0.decode(reader, m.getBeneficiaryLegRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("payerLegRef");
        decoder0.decode(reader, m.getPayerLegRef(), maxDepth, currentDepth);
        return m;
    }
}