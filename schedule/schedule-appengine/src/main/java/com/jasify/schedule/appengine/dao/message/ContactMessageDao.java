package com.jasify.schedule.appengine.dao.message;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.dao.BaseDao;
import com.jasify.schedule.appengine.dao.BaseDaoQuery;
import com.jasify.schedule.appengine.meta.message.ContactMessageMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.message.ContactMessage;
import org.apache.commons.lang3.StringUtils;
import org.slim3.datastore.Datastore;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * @author wszarmach
 * @since 07/11/15.
 */
public class ContactMessageDao extends BaseDao<ContactMessage> {

    public static final ContactMessageDao INSTANCE = new ContactMessageDao();

    public ContactMessageDao() {
        super(ContactMessageMeta.get());
    }

    @Nonnull
    @Override
    public Key save(@Nonnull ContactMessage entity) throws ModelException {
        if (StringUtils.isBlank(entity.getFirstName())) throw new FieldValueException("ContactMessage must have First Name");
        if (StringUtils.isBlank(entity.getLastName())) throw new FieldValueException("ContactMessage must have Last Name");
        if (StringUtils.isBlank(entity.getEmail())) throw new FieldValueException("ContactMessage must have Email");
        if (StringUtils.isBlank(entity.getSubject())) throw new FieldValueException("ContactMessage must have Subject");
        if (StringUtils.isBlank(entity.getMessage())) throw new FieldValueException("ContactMessage must have Message");
        Preconditions.checkArgument(entity.getId() == null, "ContactMessage can not be edited");

        return super.save(entity);
    }

    public List<ContactMessage> getAll() {
        ContactMessageMeta meta = getMeta();
        return query(new BaseDaoQuery<ContactMessage, ContactMessageMeta>(meta, new Serializable[0]) {
            @Override
            public List<Key> execute() {
                return Datastore.query(meta).asKeyList();
            }
        });
    }
}