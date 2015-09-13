package com.jasify.schedule.appengine.model.balance;

import com.jasify.schedule.appengine.model.HasId;
import org.slim3.datastore.ModelRef;

/**
 * Model objects that can be linked to a transfer.
 *
 * @author krico
 * @since 22/02/15.
 */
public interface HasTransfer extends HasId {
    ModelRef<Transfer> getTransferRef();
}
