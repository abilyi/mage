package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Base class for all nodes. Provides exception handling with routing flow execution to distinct paths based on
 * exception class,
 * @param <T>
 */
public abstract class Node<T extends UserContext> implements Function<WorkflowContext<T>, String> {

    private final String id;
    protected final List<ExceptionRoute<T>> exceptionRoutes;
    protected PersistContextScope persistContextScope;
    protected boolean pause;

    Node(String id, List<ExceptionRoute<T>> exceptionRoutes) {
        this.id = id;
        this.exceptionRoutes = List.copyOf(exceptionRoutes);
    }

    public String apply(WorkflowContext<T> context) {
        enterNode(context);
        try {
            return executeAction(context);
        } catch (Exception e) {
            return routeOnException(e, context);
        } finally {
            exitNode(context);
        }
    }

    protected abstract String executeAction(WorkflowContext<T> context);

    protected String routeOnException(Exception e, WorkflowContext<T> context) {
        Throwable cause;
        WokflowExecutionException executionException;
        if (e instanceof WokflowExecutionException) {
            cause = e.getCause();
            executionException = (WokflowExecutionException) e;
        } else {
            // TODO log an exception
            cause = e;
            executionException = new WokflowExecutionException(e, context.getExecutionContext().getExecutionPoint());
        }
        for (ExceptionRoute<T> exceptionRoute: getExceptionRoutes()) {
            if (exceptionRoute.test(cause)) {
                Optional.ofNullable(exceptionRoute.handler).ifPresent(h -> h.accept(context.getContext(), cause));
                return exceptionRoute.nodeId;
            }
        }
        throw executionException;
    }

    /**
     * @return persistence scope of this step. Node may narrow the scope, excluding flow data or even disabling
     * persistence completely
     */
    abstract PersistContextScope getPersistenceScope();

    protected void enterNode(WorkflowContext<T> workflowContext) {
    }

    protected void exitNode(WorkflowContext<T> workflowContext) {
    }

    /**
     * @return step (node) id
     */
    public String getId() {
        return id;
    }

    /**
     * @return {@link ExceptionRoute}s defining execution paths in case of exception, if any
     */
    public List<ExceptionRoute<T>> getExceptionRoutes() {
        return exceptionRoutes;
    }

    public boolean isPause() {
        return pause;
    }

    void setPause(boolean pause) {
        this.pause = pause;
    }

    /**
     * @return default scope of context to be persisted. Node still may reduce this scope for it's execution.
     */
    public PersistContextScope getPersistContextScope() {
        return persistContextScope;
    }

    /**
     * Sets default scope of context to be persisted. Node still may reduce this scope for it's execution.
     * @param persistContextScope
     */
    public void setPersistenceScope(PersistContextScope persistContextScope) {
        this.persistContextScope = persistContextScope;
    }
}
