package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContext;
import tech.becloud.workflow.persistence.PersistContextScope;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionNode<T> extends Node<T> {
    private final Consumer<? super T> action;
    private final Predicate<? super T> predicate;
    protected final String nextNodeId;

    ActionNode(String id, Consumer<? super T> action, String nextNodeId, List<ExceptionRoute> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.action = action;
        this.predicate = null;
        this.nextNodeId = nextNodeId;
    }

    ActionNode(String id, Predicate<? super T> predicate, Consumer<? super T> action, String nextNodeId,
               List<ExceptionRoute> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.action = action;
        this.predicate = predicate;
        this.nextNodeId = nextNodeId;
    }

    @Override
    protected String executeAction(WorkflowContext<T> workflowContext) {
        T context = workflowContext.getContext();
        if (predicate == null || predicate.test(context)) {
            action.accept(context);
        }
        return nextNodeId;
    }

    @Override
    PersistContextScope getPersistenceScope() {
        // FIXME consumer defined scope should have higher priority
        return Optional.ofNullable(persistContextScope).orElseGet(() ->
                Optional.ofNullable(action.getClass().getAnnotation(PersistContext.class))
                        .map(PersistContext::value)
                        .orElse(PersistContextScope.ALL)
        );
    }
}
