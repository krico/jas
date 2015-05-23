package com.jasify.schedule.appengine.model.dao;

import java.lang.annotation.*;

/**
 * Indicates that this method will automatically use the
 * {@link com.google.appengine.api.datastore.DatastoreService#getCurrentTransaction() DatastoreService.getCurrentTransaction()}.
 * <p/>
 * Here's an example.  The method "{@code@AutoTransaction void foo();}" must use "<code>tx</code>" in the following
 * code snippet.
 * <pre>
 *     Transaction tx = Datastore.beginTransaction();
 *     foo();
 * </pre>
 *
 * @author krico
 * @since 24/05/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentTransaction {
}
