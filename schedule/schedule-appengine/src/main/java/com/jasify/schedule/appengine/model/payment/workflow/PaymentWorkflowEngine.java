package com.jasify.schedule.appengine.model.payment.workflow;

import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.jasify.schedule.appengine.meta.payment.workflow.PaymentWorkflowMeta;
import com.jasify.schedule.appengine.model.payment.PaymentStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slim3.datastore.Datastore;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * @author krico
 * @since 05/04/15.
 */
public final class PaymentWorkflowEngine {
    private static final Logger log = LoggerFactory.getLogger(PaymentWorkflowEngine.class);

    private static final EnumMap<PaymentStateEnum, EnumSet<PaymentStateEnum>> ALLOWED_TRANSITIONS =
            new EnumMap<PaymentStateEnum, EnumSet<PaymentStateEnum>>(PaymentStateEnum.class) {
                {
                    // * -> New is not allowed
                    put(PaymentStateEnum.New, EnumSet.noneOf(PaymentStateEnum.class));
                    // [New] -> Created
                    put(PaymentStateEnum.Created, EnumSet.of(PaymentStateEnum.New));
                    // [Created] -> Completed
                    put(PaymentStateEnum.Completed, EnumSet.of(PaymentStateEnum.Created));
                    // [New, Created] -> Canceled
                    put(PaymentStateEnum.Canceled, EnumSet.of(PaymentStateEnum.New, PaymentStateEnum.Created));
                }
            };

    private PaymentWorkflowEngine() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends PaymentWorkflow> T transition(Key id, PaymentStateEnum newState) throws PaymentWorkflowException {
        return transition((T) Datastore.get(PaymentWorkflowMeta.get(), id), newState);
    }

    public static <T extends PaymentWorkflow> T transition(T workflow, PaymentStateEnum newState) throws PaymentWorkflowException {
        PaymentStateEnum oldState = Preconditions.checkNotNull(workflow.getState());
        log.debug("{} [{} -> {}]", workflow.getId(), oldState, newState);

        if (oldState == newState) {
            log.warn("Invalid transition {} [{} -> {}]", workflow.getId(), oldState, newState);
            return workflow; //no transition
        }

        if (!ALLOWED_TRANSITIONS.get(newState).contains(oldState)) {
            throw new InvalidWorkflowTransitionException(oldState, newState);
        }

        switch (newState) {
            case Created:
                workflow.onCreated();
                break;
            case Canceled:
                workflow.onCanceled();
                break;
            case Completed:
                workflow.onCompleted();
                break;
            case New:
                throw new IllegalArgumentException("Can't transition to state: " + newState);
        }

        workflow.setState(newState);

        Datastore.put(workflow);

        return workflow;
    }
}
