package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.activity.ActivityMeta;
import com.jasify.schedule.appengine.meta.activity.ActivityPackageMeta;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author krico
 * @since 05/04/15.
 */
public final class PaymentWorkflowFactory {
    private PaymentWorkflowFactory() {
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T extends PaymentWorkflow> T workflowFor(Key id, Object data) {
        Preconditions.checkNotNull(id);
        if (ActivityMeta.get().getKind().equals(id.getKind())) {
            return (T) new ActivityPaymentWorkflow(id);
        }
        if (ActivityPackageMeta.get().getKind().equals(id.getKind())) {
            Preconditions.checkNotNull(data, "You need the list of activities");
            return (T) new ActivityPackagePaymentWorkflow(id, (List<Key>) data);
        }
        throw new IllegalArgumentException("Unsupported kind: [" + id.getKind() + "]");
    }

    @SuppressWarnings("unchecked")
    public static <T extends PaymentWorkflow> T workflowForCartId(String cartId) {
        return (T) new ShoppingCartPaymentWorkflow(cartId);
    }
}
