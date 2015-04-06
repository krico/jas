package com.jasify.schedule.appengine.meta.balance;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class TransactionMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.balance.Transaction> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction>(this, "currency", "currency");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, java.lang.Double> amount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, java.lang.Double>(this, "amount", "amount", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction> description = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction>(this, "description", "description");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction> reference = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction>(this, "reference", "reference");

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Account>, com.jasify.schedule.appengine.model.balance.Account> accountRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Account>, com.jasify.schedule.appengine.model.balance.Account>(this, "accountRef", "accountRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Account.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer> transferRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.Transaction, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer>(this, "transferRef", "transferRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Transfer.class);

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final TransactionMeta slim3_singleton = new TransactionMeta();

    /**
     * @return the singleton
     */
    public static TransactionMeta get() {
       return slim3_singleton;
    }

    /** */
    public TransactionMeta() {
        super("Transaction", com.jasify.schedule.appengine.model.balance.Transaction.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.balance.Transaction entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.balance.Transaction model = new com.jasify.schedule.appengine.model.balance.Transaction();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setAmount((java.lang.Double) entity.getProperty("amount"));
        model.setDescription((java.lang.String) entity.getProperty("description"));
        model.setReference((java.lang.String) entity.getProperty("reference"));
        if (model.getAccountRef() == null) {
            throw new NullPointerException("The property(accountRef) is null.");
        }
        model.getAccountRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("accountRef"));
        if (model.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) is null.");
        }
        model.getTransferRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("transferRef"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("amount", m.getAmount());
        entity.setProperty("description", m.getDescription());
        entity.setProperty("reference", m.getReference());
        if (m.getAccountRef() == null) {
            throw new NullPointerException("The property(accountRef) must not be null.");
        }
        entity.setProperty("accountRef", m.getAccountRef().getKey());
        if (m.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) must not be null.");
        }
        entity.setProperty("transferRef", m.getTransferRef().getKey());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.balance.Transaction) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
        if (m.getAccountRef() == null) {
            throw new NullPointerException("The property(accountRef) must not be null.");
        }
        m.getAccountRef().assignKeyIfNecessary(ds);
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
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
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
        com.jasify.schedule.appengine.model.balance.Transaction m = (com.jasify.schedule.appengine.model.balance.Transaction) model;
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
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        if(m.getAmount() != null){
            writer.setNextPropertyName("amount");
            encoder0.encode(writer, m.getAmount());
        }
        if(m.getDescription() != null){
            writer.setNextPropertyName("description");
            encoder0.encode(writer, m.getDescription());
        }
        if(m.getReference() != null){
            writer.setNextPropertyName("reference");
            encoder0.encode(writer, m.getReference());
        }
        if(m.getAccountRef() != null && m.getAccountRef().getKey() != null){
            writer.setNextPropertyName("accountRef");
            encoder0.encode(writer, m.getAccountRef(), maxDepth, currentDepth);
        }
        if(m.getTransferRef() != null && m.getTransferRef().getKey() != null){
            writer.setNextPropertyName("transferRef");
            encoder0.encode(writer, m.getTransferRef(), maxDepth, currentDepth);
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.balance.Transaction jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.balance.Transaction m = new com.jasify.schedule.appengine.model.balance.Transaction();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        reader = rootReader.newObjectReader("amount");
        m.setAmount(decoder0.decode(reader, m.getAmount()));
        reader = rootReader.newObjectReader("description");
        m.setDescription(decoder0.decode(reader, m.getDescription()));
        reader = rootReader.newObjectReader("reference");
        m.setReference(decoder0.decode(reader, m.getReference()));
        reader = rootReader.newObjectReader("accountRef");
        decoder0.decode(reader, m.getAccountRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("transferRef");
        decoder0.decode(reader, m.getTransferRef(), maxDepth, currentDepth);
        return m;
    }
}