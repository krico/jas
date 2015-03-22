package com.jasify.schedule.appengine.model.balance;

import com.google.appengine.api.datastore.Key;
import org.slim3.datastore.ModelRef;

/**
 * Model objects that can be linked to a transfer.
 *
 * @author krico
 * @since 22/02/15.
 */
public interface HasTransfer {
    Key getId();

    ModelRef<Transfer> getTransferRef();
}
