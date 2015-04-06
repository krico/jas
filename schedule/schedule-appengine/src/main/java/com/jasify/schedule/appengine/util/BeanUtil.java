package com.jasify.schedule.appengine.util;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.appengine.api.datastore.Key;
import com.jasify.schedule.appengine.model.users.User;
import org.apache.commons.beanutils.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author krico
 * @since 04/01/15.
 */
public final class BeanUtil {
    private static final Converter stringToKeyConverter = new Converter() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T convert(Class<T> targetType, Object value) {
            if (value == null) return null;

            if (targetType == Key.class && value instanceof String) {
                return (T) KeyUtil.stringToKey((String) value);
            } else if (targetType == String.class && value instanceof Key) {
                return (T) KeyUtil.keyToString((Key) value);
            } else if (targetType == Key.class && value instanceof Key) {
                return (T) value;
            }

            throw new ConversionException("Can't convert " + value.getClass().getName() + " -> " + targetType.getName());
        }
    };
    private static final BeanIntrospector jasIntrospector = new BeanIntrospector() {
        @Override
        public void introspect(IntrospectionContext context) throws IntrospectionException {
            if (User.class.isAssignableFrom(context.getTargetClass())) {
                //make created/modified read only on user.class
                PropertyDescriptor created = context.getPropertyDescriptor("created");
                if (created != null)
                    created.setWriteMethod(null);
                PropertyDescriptor modified = context.getPropertyDescriptor("modified");
                if (modified != null)
                    modified.setWriteMethod(null);
            }
        }
    };
    private final BeanUtilsBean beanUtils;

    private BeanUtil() {
        beanUtils = new BeanUtilsBean2();
        ConvertUtilsBean2 convertUtils = (ConvertUtilsBean2) beanUtils.getConvertUtils();
        convertUtils.register(stringToKeyConverter, Key.class);

        beanUtils.getPropertyUtils().addBeanIntrospector(jasIntrospector);
    }

    public static void copyProperties(Object destination, Object origin) {
        Preconditions.checkNotNull(destination);
        Preconditions.checkNotNull(origin);
        try {
            Singleton.INSTANCE.beanUtils.copyProperties(destination, origin);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed to copy properties "
                    + origin.getClass().getName() + " -> " + destination.getClass().getName());
        }
    }

    public static void copyPropertiesExcluding(Object destination, Object origin, String... excludedProperties) {
        Preconditions.checkNotNull(destination);
        Preconditions.checkNotNull(origin);
        Map<Object, Object> beanMap = new HashMap<>(new BeanMap(origin));
        for (String excludedProperty : excludedProperties) {
            beanMap.remove(excludedProperty);
        }
        copyProperties(destination, beanMap);
    }

    private static class Singleton {
        private static final BeanUtil INSTANCE = new BeanUtil();
    }
}
