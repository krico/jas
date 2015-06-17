package com.jasify.schedule.appengine.model.consistency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a consistency checker for an entity class, that should be executed before that entity is deleted.
 * <p/>
 * The method should be declared in a class that implements {@link EntityConsistency}.  The class needs to have a
 * public no argument constructor, and the method should receive a single argument, namely the id.
 *
 * Here's an example
 * <code>
 * <pre>
 * class OrgConsistency implements EntityConsistency{@literal <}Organization{@literal >} {
 *          {@literal @}BeforeDelete(entityClass = Organization.class)
 *          public void anyMethodName(Key id) throws InconsistentModelStateException {
 * </pre>
 * </code>
 *
 * @author krico
 * @since 17/06/15.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeDelete {
    Class<?> entityClass();
}
