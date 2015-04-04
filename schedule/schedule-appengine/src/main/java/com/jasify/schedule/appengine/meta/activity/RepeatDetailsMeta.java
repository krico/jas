package com.jasify.schedule.appengine.meta.activity;

//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" })
/** */
public final class RepeatDetailsMeta extends org.slim3.datastore.ModelMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails> {

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.google.appengine.api.datastore.Key> id = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.google.appengine.api.datastore.Key>(this, "__key__", "id", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType> repeatType = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType>(this, "repeatType", "repeatType", com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Integer> repeatEvery = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Integer>(this, "repeatEvery", "repeatEvery", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType> repeatUntilType = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType>(this, "repeatUntilType", "repeatUntilType", com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Integer> untilCount = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Integer>(this, "untilCount", "untilCount", int.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.util.Date> untilDate = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.util.Date>(this, "untilDate", "untilDate", java.util.Date.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> mondayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "mondayEnabled", "mondayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> tuesdayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "tuesdayEnabled", "tuesdayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> wednesdayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "wednesdayEnabled", "wednesdayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> thursdayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "thursdayEnabled", "thursdayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> fridayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "fridayEnabled", "fridayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> saturdayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "saturdayEnabled", "saturdayEnabled", boolean.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean> sundayEnabled = new org.slim3.datastore.CoreAttributeMeta<com.jasify.schedule.appengine.model.activity.RepeatDetails, java.lang.Boolean>(this, "sundayEnabled", "sundayEnabled", boolean.class);

    private static final RepeatDetailsMeta slim3_singleton = new RepeatDetailsMeta();

    /**
     * @return the singleton
     */
    public static RepeatDetailsMeta get() {
       return slim3_singleton;
    }

    /** */
    public RepeatDetailsMeta() {
        super("RepeatDetails", com.jasify.schedule.appengine.model.activity.RepeatDetails.class);
    }

    @Override
    public com.jasify.schedule.appengine.model.activity.RepeatDetails entityToModel(com.google.appengine.api.datastore.Entity entity) {
        com.jasify.schedule.appengine.model.activity.RepeatDetails model = new com.jasify.schedule.appengine.model.activity.RepeatDetails();
        model.setId(entity.getKey());
        model.setRepeatType(stringToEnum(com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType.class, (java.lang.String) entity.getProperty("repeatType")));
        model.setRepeatEvery(longToPrimitiveInt((java.lang.Long) entity.getProperty("repeatEvery")));
        model.setRepeatUntilType(stringToEnum(com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType.class, (java.lang.String) entity.getProperty("repeatUntilType")));
        model.setUntilCount(longToPrimitiveInt((java.lang.Long) entity.getProperty("untilCount")));
        model.setUntilDate((java.util.Date) entity.getProperty("untilDate"));
        model.setMondayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("mondayEnabled")));
        model.setTuesdayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("tuesdayEnabled")));
        model.setWednesdayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("wednesdayEnabled")));
        model.setThursdayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("thursdayEnabled")));
        model.setFridayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("fridayEnabled")));
        model.setSaturdayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("saturdayEnabled")));
        model.setSundayEnabled(booleanToPrimitiveBoolean((java.lang.Boolean) entity.getProperty("sundayEnabled")));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        com.jasify.schedule.appengine.model.activity.RepeatDetails m = (com.jasify.schedule.appengine.model.activity.RepeatDetails) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getId() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getId());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("repeatType", enumToString(m.getRepeatType()));
        entity.setProperty("repeatEvery", m.getRepeatEvery());
        entity.setProperty("repeatUntilType", enumToString(m.getRepeatUntilType()));
        entity.setProperty("untilCount", m.getUntilCount());
        entity.setProperty("untilDate", m.getUntilDate());
        entity.setProperty("mondayEnabled", m.isMondayEnabled());
        entity.setProperty("tuesdayEnabled", m.isTuesdayEnabled());
        entity.setProperty("wednesdayEnabled", m.isWednesdayEnabled());
        entity.setProperty("thursdayEnabled", m.isThursdayEnabled());
        entity.setProperty("fridayEnabled", m.isFridayEnabled());
        entity.setProperty("saturdayEnabled", m.isSaturdayEnabled());
        entity.setProperty("sundayEnabled", m.isSundayEnabled());
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        com.jasify.schedule.appengine.model.activity.RepeatDetails m = (com.jasify.schedule.appengine.model.activity.RepeatDetails) model;
        return m.getId();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        com.jasify.schedule.appengine.model.activity.RepeatDetails m = (com.jasify.schedule.appengine.model.activity.RepeatDetails) model;
        m.setId(key);
    }

    @Override
    protected long getVersion(Object model) {
        throw new IllegalStateException("The version property of the model(com.jasify.schedule.appengine.model.activity.RepeatDetails) is not defined.");
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
    }

    @Override
    protected void prePut(Object model) {
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
        com.jasify.schedule.appengine.model.activity.RepeatDetails m = (com.jasify.schedule.appengine.model.activity.RepeatDetails) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getId() != null){
            writer.setNextPropertyName("id");
            encoder0.encode(writer, m.getId());
        }
        if(m.getRepeatType() != null){
            writer.setNextPropertyName("repeatType");
            encoder0.encode(writer, m.getRepeatType());
        }
        writer.setNextPropertyName("repeatEvery");
        encoder0.encode(writer, m.getRepeatEvery());
        if(m.getRepeatUntilType() != null){
            writer.setNextPropertyName("repeatUntilType");
            encoder0.encode(writer, m.getRepeatUntilType());
        }
        writer.setNextPropertyName("untilCount");
        encoder0.encode(writer, m.getUntilCount());
        if(m.getUntilDate() != null){
            writer.setNextPropertyName("untilDate");
            encoder0.encode(writer, m.getUntilDate());
        }
        writer.setNextPropertyName("mondayEnabled");
        encoder0.encode(writer, m.isMondayEnabled());
        writer.setNextPropertyName("tuesdayEnabled");
        encoder0.encode(writer, m.isTuesdayEnabled());
        writer.setNextPropertyName("wednesdayEnabled");
        encoder0.encode(writer, m.isWednesdayEnabled());
        writer.setNextPropertyName("thursdayEnabled");
        encoder0.encode(writer, m.isThursdayEnabled());
        writer.setNextPropertyName("fridayEnabled");
        encoder0.encode(writer, m.isFridayEnabled());
        writer.setNextPropertyName("saturdayEnabled");
        encoder0.encode(writer, m.isSaturdayEnabled());
        writer.setNextPropertyName("sundayEnabled");
        encoder0.encode(writer, m.isSundayEnabled());
        writer.endObject();
    }

    @Override
    protected com.jasify.schedule.appengine.model.activity.RepeatDetails jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        com.jasify.schedule.appengine.model.activity.RepeatDetails m = new com.jasify.schedule.appengine.model.activity.RepeatDetails();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("id");
        m.setId(decoder0.decode(reader, m.getId()));
        reader = rootReader.newObjectReader("repeatType");
        m.setRepeatType(decoder0.decode(reader, m.getRepeatType(), com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatType.class));
        reader = rootReader.newObjectReader("repeatEvery");
        m.setRepeatEvery(decoder0.decode(reader, m.getRepeatEvery()));
        reader = rootReader.newObjectReader("repeatUntilType");
        m.setRepeatUntilType(decoder0.decode(reader, m.getRepeatUntilType(), com.jasify.schedule.appengine.model.activity.RepeatDetails.RepeatUntilType.class));
        reader = rootReader.newObjectReader("untilCount");
        m.setUntilCount(decoder0.decode(reader, m.getUntilCount()));
        reader = rootReader.newObjectReader("untilDate");
        m.setUntilDate(decoder0.decode(reader, m.getUntilDate()));
        reader = rootReader.newObjectReader("mondayEnabled");
        m.setMondayEnabled(decoder0.decode(reader, m.isMondayEnabled()));
        reader = rootReader.newObjectReader("tuesdayEnabled");
        m.setTuesdayEnabled(decoder0.decode(reader, m.isTuesdayEnabled()));
        reader = rootReader.newObjectReader("wednesdayEnabled");
        m.setWednesdayEnabled(decoder0.decode(reader, m.isWednesdayEnabled()));
        reader = rootReader.newObjectReader("thursdayEnabled");
        m.setThursdayEnabled(decoder0.decode(reader, m.isThursdayEnabled()));
        reader = rootReader.newObjectReader("fridayEnabled");
        m.setFridayEnabled(decoder0.decode(reader, m.isFridayEnabled()));
        reader = rootReader.newObjectReader("saturdayEnabled");
        m.setSaturdayEnabled(decoder0.decode(reader, m.isSaturdayEnabled()));
        reader = rootReader.newObjectReader("sundayEnabled");
        m.setSundayEnabled(decoder0.decode(reader, m.isSundayEnabled()));
        return m;
    }
}