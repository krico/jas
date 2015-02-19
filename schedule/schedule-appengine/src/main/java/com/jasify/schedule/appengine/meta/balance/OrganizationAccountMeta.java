package com.jasify.schedule.appengine.meta.balance;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2015-02-19 22:16:35")
/** */
public final class OrganizationAccountMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount> {

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization> organizationRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.common.Organization>, com.jasify.schedule.appengine.model.common.Organization>(this, "organizationRef", "organizationRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.common.Organization.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.util.Date> created = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.util.Date>(this, "created", "created", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.util.Date> modified = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.util.Date>(this, "modified", "modified", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.lang.Double> balance = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount, java.lang.Double>(this, "balance", "balance", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount> currency = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.balance.OrganizationAccount>(this, "currency", "currency");

    private static final org.slim3.datastore.CreationDate slim3_createdAttributeListener = new org.slim3.datastore.CreationDate();

    private static final org.slim3.datastore.ModificationDate slim3_modifiedAttributeListener = new org.slim3.datastore.ModificationDate();

    private static final OrganizationAccountMeta slim3_singleton = new OrganizationAccountMeta();

    /**
     * @return the singleton
     */
    public static OrganizationAccountMeta get() {
       return slim3_singleton;
    }

    /** */
    public OrganizationAccountMeta() {
        super("Account", com.jasify.schedule.appengine.model.balance.OrganizationAccount.class, java.util.Arrays.asList("com.jasify.schedule.appengine.model.balance.OrganizationAccount"));
    }

    @Override
    public com.jasify.schedule.appengine.model.balance.OrganizationAccount entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount model = new com.jasify.schedule.appengine.model.balance.OrganizationAccount();
        if (model.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) is null.");
        }
        model.getOrganizationRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("organizationRef"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setBalance((java.lang.Double) entity.getProperty("balance"));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        entity.setProperty("organizationRef", m.getOrganizationRef().getKey());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("balance", m.getBalance());
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("slim3.classHierarchyList", classHierarchyList);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.balance.OrganizationAccount) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
        if (m.getOrganizationRef() == null) {
            throw new NullPointerException("The property(organizationRef) must not be null.");
        }
        m.getOrganizationRef().assignKeyIfNecessary(ds);
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
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
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = (com.jasify.schedule.appengine.model.balance.OrganizationAccount) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getOrganizationRef() != null && m.getOrganizationRef().getKey() != null){
            writer.setNextPropertyName("organizationRef");
            encoder0.encode(writer, m.getOrganizationRef(), maxDepth, currentDepth);
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
        if(m.getBalance() != null){
            writer.setNextPropertyName("balance");
            encoder0.encode(writer, m.getBalance());
        }
        if(m.getCurrency() != null){
            writer.setNextPropertyName("currency");
            encoder0.encode(writer, m.getCurrency());
        }
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.balance.OrganizationAccount jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.balance.OrganizationAccount m = new com.jasify.schedule.appengine.model.balance.OrganizationAccount();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("organizationRef");
        decoder0.decode(reader, m.getOrganizationRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("created");
        m.setCreated(decoder0.decode(reader, m.getCreated()));
        reader = rootReader.newObjectReader("modified");
        m.setModified(decoder0.decode(reader, m.getModified()));
        reader = rootReader.newObjectReader("balance");
        m.setBalance(decoder0.decode(reader, m.getBalance()));
        reader = rootReader.newObjectReader("currency");
        m.setCurrency(decoder0.decode(reader, m.getCurrency()));
        return m;
    }
}