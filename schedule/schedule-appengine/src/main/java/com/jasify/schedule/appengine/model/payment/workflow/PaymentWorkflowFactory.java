package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;

import javax.annotation.Nonnull;

/**
 * @author krico
 * @since 05/04/15.
 */
public final class PaymentWorkflowFactory {
    private PaymentWorkflowFactory() {
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends PaymentWorkflow> T workflowFor(Key id) {
        Preconditions.checkNotNull(id);
        if (ActivityMeta.get().getKind().equals(id.getKind())) {
            return (T) new ActivityPaymentWorkflow(id);
        }
        throw new IllegalArgumentException("Unsupported kind: [" + id.getKind() + "]");
    }
}
