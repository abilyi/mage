package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContext;
import tech.becloud.mage.persistence.PersistContextScope;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a workflow step executing a single action. May be executed conditionally on supplied predicate.
 * On successful execution it returns a next step to be executed, or null if no such step defined.
 * On exception next step may be defined per exception type, otherwise exception is delivered to a caller.
 * @param <T> type parameter representing type of data processed by the flow.
 */
public class ActionNode<T extends UserContext> extends Node<T> {
    private final Consumer<? super T> action;
    private final Predicate<? super T> predicate;
    protected final String nextNodeId;

    /**
     * Constructor, package private as intended way to create nodes is via builders.
     * @param id step (node) id
     * @param action action to perform, represented as {@link Consumer} implementation
     * @param nextNodeId id of next step to execute on successful completion
     * @param exceptionRoutes next steps per exception type on exceptional action completion
     */
    ActionNode(String id, Consumer<? super T> action, String nextNodeId, List<ExceptionRoute<T>> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.action = action;
        this.predicate = null;
        this.nextNodeId = nextNodeId;
    }

    /**
     * Constructor, package private as intended way to create nodes is via builders.
     * @param id step (node) id
     * @param predicate to test for conditional execution
     * @param action action to perform, represented as {@link Consumer} implementation
     * @param nextNodeId id of next step to execute on successful completion
     * @param exceptionRoutes next steps per exception type on exceptional action completion
     */
    ActionNode(String id, Predicate<? super T> predicate, Consumer<? super T> action, String nextNodeId,
               List<ExceptionRoute<T>> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.action = action;
        this.predicate = predicate;
        this.nextNodeId = nextNodeId;
    }

    @Override
    public String apply(WorkflowContext<T> workflowContext) {
        try {
            T userContext = workflowContext.getUserContext();
            if (predicate == null || predicate.test(userContext)) {
                action.accept(userContext);
            }
            return nextNodeId;
        } catch (Exception e) {
            return routeOnException(e, workflowContext);
        }
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

    /**
     * @return action to be performed
     */
    public Consumer<? super T> getAction() {
        return action;
    }

    /**
     * @return predicate for conditional execution, if any
     */
    public Predicate<? super T> getPredicate() {
        return predicate;
    }

    /**
     * @return id of next step (node) on successful completion. May be {@code null} which means flow completion.
     */
    public String getNextNodeId() {
        return nextNodeId;
    }
}
