package com.jasify.schedule.appengine.meta.payment;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
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
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> payerEmail = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "payerEmail", "payerEmail");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> payerFirstName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "payerFirstName", "payerFirstName");

    /** */
    public final org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment> payerLastName = new org.slim3.datastore.StringAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment>(this, "payerLastName", "payerLastName");

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

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double> realFee = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.lang.Double>(this, "realFee", "realFee", java.lang.Double.class);

    /** */
    public final org.slim3.datastore.StringCollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.String>> itemDescriptions = new org.slim3.datastore.StringCollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.String>>(this, "itemDescriptions", "itemDescriptions", java.util.List.class);

    /** */
    public final org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.Integer>, java.lang.Integer> itemUnits = new org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.Integer>, java.lang.Integer>(this, "itemUnits", "itemUnits", java.util.List.class);

    /** */
    public final org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.Double>, java.lang.Double> itemPrices = new org.slim3.datastore.CollectionAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, java.util.List<java.lang.Double>, java.lang.Double>(this, "itemPrices", "itemPrices", java.util.List.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer> transferRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.balance.Transfer>, com.jasify.schedule.appengine.model.balance.Transfer>(this, "transferRef", "transferRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.balance.Transfer.class);

    /** */
    public final org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User> userRef = new org.slim3.datastore.ModelRefAttributeMeta<com.jasify.schedule.appengine.model.payment.PayPalPayment, org.slim3.datastore.ModelRef<com.jasify.schedule.appengine.model.users.User>, com.jasify.schedule.appengine.model.users.User>(this, "userRef", "userRef", org.slim3.datastore.ModelRef.class, com.jasify.schedule.appengine.model.users.User.class);

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
        model.setPayerEmail((java.lang.String) entity.getProperty("payerEmail"));
        model.setPayerFirstName((java.lang.String) entity.getProperty("payerFirstName"));
        model.setPayerLastName((java.lang.String) entity.getProperty("payerLastName"));
        model.setId(entity.getKey());
        model.setCreated((java.util.Date) entity.getProperty("created"));
        model.setModified((java.util.Date) entity.getProperty("modified"));
        model.setType(stringToEnum(com.jasify.schedule.appengine.model.payment.PaymentTypeEnum.class, (java.lang.String) entity.getProperty("type")));
        model.setState(stringToEnum(com.jasify.schedule.appengine.model.payment.PaymentStateEnum.class, (java.lang.String) entity.getProperty("state")));
        model.setCurrency((java.lang.String) entity.getProperty("currency"));
        model.setAmount((java.lang.Double) entity.getProperty("amount"));
        model.setFee((java.lang.Double) entity.getProperty("fee"));
        model.setRealFee((java.lang.Double) entity.getProperty("realFee"));
        model.setItemDescriptions(toList(java.lang.String.class, entity.getProperty("itemDescriptions")));
        model.setItemUnits(longListToIntegerList(entity.getProperty("itemUnits")));
        model.setItemPrices(toList(java.lang.Double.class, entity.getProperty("itemPrices")));
        if (model.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) is null.");
        }
        model.getTransferRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("transferRef"));
        if (model.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) is null.");
        }
        model.getUserRef().setKey((com.google.appengine.api.datastore.Key) entity.getProperty("userRef"));
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
        entity.setProperty("payerEmail", m.getPayerEmail());
        entity.setProperty("payerFirstName", m.getPayerFirstName());
        entity.setProperty("payerLastName", m.getPayerLastName());
        entity.setProperty("created", m.getCreated());
        entity.setProperty("modified", m.getModified());
        entity.setProperty("type", enumToString(m.getType()));
        entity.setProperty("state", enumToString(m.getState()));
        entity.setProperty("currency", m.getCurrency());
        entity.setProperty("amount", m.getAmount());
        entity.setProperty("fee", m.getFee());
        entity.setProperty("realFee", m.getRealFee());
        entity.setProperty("itemDescriptions", m.getItemDescriptions());
        entity.setProperty("itemUnits", m.getItemUnits());
        entity.setProperty("itemPrices", m.getItemPrices());
        if (m.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) must not be null.");
        }
        entity.setProperty("transferRef", m.getTransferRef().getKey());
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        entity.setProperty("userRef", m.getUserRef().getKey());
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
        com.jasify.schedule.appengine.model.payment.PayPalPayment m = (com.jasify.schedule.appengine.model.payment.PayPalPayment) model;
        if (m.getTransferRef() == null) {
            throw new NullPointerException("The property(transferRef) must not be null.");
        }
        m.getTransferRef().assignKeyIfNecessary(ds);
        if (m.getUserRef() == null) {
            throw new NullPointerException("The property(userRef) must not be null.");
        }
        m.getUserRef().assignKeyIfNecessary(ds);
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
        if(m.getPayerEmail() != null){
            writer.setNextPropertyName("payerEmail");
            encoder0.encode(writer, m.getPayerEmail());
        }
        if(m.getPayerFirstName() != null){
            writer.setNextPropertyName("payerFirstName");
            encoder0.encode(writer, m.getPayerFirstName());
        }
        if(m.getPayerLastName() != null){
            writer.setNextPropertyName("payerLastName");
            encoder0.encode(writer, m.getPayerLastName());
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
        if(m.getRealFee() != null){
            writer.setNextPropertyName("realFee");
            encoder0.encode(writer, m.getRealFee());
        }
        if(m.getItemDescriptions() != null){
            writer.setNextPropertyName("itemDescriptions");
            writer.beginArray();
            for(java.lang.String v : m.getItemDescriptions()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        if(m.getItemUnits() != null){
            writer.setNextPropertyName("itemUnits");
            writer.beginArray();
            for(java.lang.Integer v : m.getItemUnits()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        if(m.getItemPrices() != null){
            writer.setNextPropertyName("itemPrices");
            writer.beginArray();
            for(java.lang.Double v : m.getItemPrices()){
                encoder0.encode(writer, v);
            }
            writer.endArray();
        }
        if(m.getTransferRef() != null && m.getTransferRef().getKey() != null){
            writer.setNextPropertyName("transferRef");
            encoder0.encode(writer, m.getTransferRef(), maxDepth, currentDepth);
        }
        if(m.getUserRef() != null && m.getUserRef().getKey() != null){
            writer.setNextPropertyName("userRef");
            encoder0.encode(writer, m.getUserRef(), maxDepth, currentDepth);
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
        reader = rootReader.newObjectReader("payerEmail");
        m.setPayerEmail(decoder0.decode(reader, m.getPayerEmail()));
        reader = rootReader.newObjectReader("payerFirstName");
        m.setPayerFirstName(decoder0.decode(reader, m.getPayerFirstName()));
        reader = rootReader.newObjectReader("payerLastName");
        m.setPayerLastName(decoder0.decode(reader, m.getPayerLastName()));
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
        reader = rootReader.newObjectReader("realFee");
        m.setRealFee(decoder0.decode(reader, m.getRealFee()));
        reader = rootReader.newObjectReader("itemDescriptions");
        {
            java.util.ArrayList<java.lang.String> elements = new java.util.ArrayList<java.lang.String>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("itemDescriptions");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    java.lang.String v = decoder0.decode(reader, (java.lang.String)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setItemDescriptions(elements);
            }
        }
        reader = rootReader.newObjectReader("itemUnits");
        {
            java.util.ArrayList<java.lang.Integer> elements = new java.util.ArrayList<java.lang.Integer>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("itemUnits");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    java.lang.Integer v = decoder0.decode(reader, (java.lang.Integer)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setItemUnits(elements);
            }
        }
        reader = rootReader.newObjectReader("itemPrices");
        {
            java.util.ArrayList<java.lang.Double> elements = new java.util.ArrayList<java.lang.Double>();
            org.slim3.datastore.json.JsonArrayReader r = rootReader.newArrayReader("itemPrices");
            if(r != null){
                reader = r;
                int n = r.length();
                for(int i = 0; i < n; i++){
                    r.setIndex(i);
                    java.lang.Double v = decoder0.decode(reader, (java.lang.Double)null)                    ;
                    if(v != null){
                        elements.add(v);
                    }
                }
                m.setItemPrices(elements);
            }
        }
        reader = rootReader.newObjectReader("transferRef");
        decoder0.decode(reader, m.getTransferRef(), maxDepth, currentDepth);
        reader = rootReader.newObjectReader("userRef");
        decoder0.decode(reader, m.getUserRef(), maxDepth, currentDepth);
        return m;
    }
}