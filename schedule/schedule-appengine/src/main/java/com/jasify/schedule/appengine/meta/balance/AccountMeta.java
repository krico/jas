package com.jasify.schedule.appengine.meta.balance;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class AccountMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.balance.Account> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.lang.Double> balance = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.lang.Double>(this, "balance", "balance", double.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.lang.Double> unpaid = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.Account, java.lang.Double>(this, "unpaid", "unpaid", double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Account> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.Account>(this, "currency", "currency");

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final AccountMeta slim3_singleton = new AccountMeta();

    /**
     * @return the singleton
     */
    public static AccountMeta get() {
       return slim3_singleton;
    }

    /** */
    public AccountMeta() {
        super("Account", com.jasify.schedule.appengine.model.balance.Account.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.balance.Account entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.balance.Account model = new com.jasify.schedule.appengine.model.balance.Account();
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setBalance(doubleToPrimitiveDouble((java.lang.Double) entity.getProperty("balance")));
        model.setUnpaid(doubleToPrimitiveDouble((java.lang.Double) entity.getProperty("unpaid")));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.Account m = (com.jasify.schedule.appengine.model.balance.Account) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("balance", m.getBalance());
        entity.setProperty("unpaid", m.getUnpaid());
        entity.setProperty("currency", m.getCurrency());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.balance.Account m = (com.jasify.schedule.appengine.model.balance.Account) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.balance.Account m = (com.jasify.schedule.appengine.model.balance.Account) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.balance.Account) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.balance.Account m = (com.jasify.schedule.appengine.model.balance.Account) model;
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
        com.jasify.schedule.appengine.model.balance.Account m = (com.jasify.schedule.appengine.model.balance.Account) model;
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
        writer.setNextPropertyName("balance");
        encoder0.encode(writer, m.getBalance());
        writer.setNextPropertyName("unpaid");
        encoder0.encode(writer, m.getUnpaid());
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.balance.Account jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.balance.Account m = new com.jasify.schedule.appengine.model.balance.Account();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("balance");
        m.setBalance(decoder0.decode(reader, m.getBalance()));
        reader = rootReader.newObjectReader("unpaid");
        m.setUnpaid(decoder0.decode(reader, m.getUnpaid()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        return m;
    }
}