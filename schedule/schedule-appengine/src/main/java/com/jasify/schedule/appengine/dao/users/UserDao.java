package com.jasify.schedule.appengine.dao.users;

import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.dao.BaseCachingDao;
import com.jasify.schedule.appengine.dao.UniqueIndex;
import com.jasify.schedule.appengine.dao.UniqueIndexCache;
import com.jasify.schedule.appengine.meta.users.UserMeta;
import com.jasify.schedule.appengine.model.FieldValueException;
import com.jasify.schedule.appengine.model.ModelException;
import com.jasify.schedule.appengine.model.UniqueConstraintException;
import com.jasify.schedule.appengine.model.users.EmailExistsException;
import com.jasify.schedule.appengine.model.users.User;
import com.jasify.schedule.appengine.model.users.UsernameExistsException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 12/06/15.
 */
public class UserDao extends BaseCachingDao<User> {
    private static final String UNIQUE_USER_NAME_CONSTRAINT = "UserDao.Username";
    private static final String UNIQUE_USER_EMAIL_CONSTRAINT = "UserDao.Email";
    private final UniqueIndex nameIndex;
    private final UniqueIndex emailIndex;

    public UserDao() {
        super(UserMeta.get());

        nameIndex = UniqueIndexCache.get(UNIQUE_USER_NAME_CONSTRAINT, meta, UserMeta.get().name, false);
        emailIndex = UniqueIndexCache.get(UNIQUE_USER_EMAIL_CONSTRAINT, meta, UserMeta.get().email, true);
    }

    @Nonnull
    @Override
    public Key save(@Nonnull User entity) throws ModelException {
        entity.setName(StringUtils.lowerCase(StringUtils.trimToNull(entity.getName())));
        if (entity.getName() == null) {
            throw new FieldValueException("User.name = NULL");
        }
        entity.setEmail(StringUtils.lowerCase(StringUtils.trimToNull(entity.getEmail())));

        if (entity.getId() == null) {
            //new entity, reserve
            reserveName(entity);
            reserveEmail(entity);
        } else {
            User current = getOrNull(entity.getId());
            if (current == null) {
                //new entity, reserve
                reserveName(entity);
                reserveEmail(entity);
            } else {
                if (!StringUtils.equals(current.getName(), entity.getName())) {
                    //existing entity, release old, reserve new
                    nameIndex.release(current.getName());
                    reserveName(entity);
                }
                if (!StringUtils.equals(current.getEmail(), entity.getEmail())) {
                    //existing entity, release old, reserve new
                    emailIndex.release(current.getEmail());
                    reserveEmail(entity);
                }
            }
        }

        return super.save(entity);
    }

    @Override
    public void delete(@Nonnull Key id) {
        User found = getOrNull(id);
        if (found != null) {
            nameIndex.release(found.getName());
            if (found.getEmail() != null) {
                emailIndex.release(found.getEmail());
            }
        }
        super.delete(id);
    }

    private void reserveEmail(User entity) throws EmailExistsException {
        if (entity.getEmail() != null) {
            try {
                emailIndex.reserve(entity.getEmail());
            } catch (UniqueConstraintException e) {
                throw new EmailExistsException(e.getMessage());
            }
        }
    }

    private void reserveName(User entity) throws UsernameExistsException {
        try {
            nameIndex.reserve(entity.getName());
        } catch (UniqueConstraintException e) {
            throw new UsernameExistsException(e.getMessage());
        }
    }
}
