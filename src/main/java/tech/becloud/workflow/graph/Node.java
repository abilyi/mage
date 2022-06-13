package tech.becloud.workflow.graph;

import lombok.Getter;
import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;

import java.util.List;
import java.util.function.Function;

@Getter
public abstract class Node<T> implements Function<WorkflowContext<T>, String> {

    private final String id;
    protected final List<ExceptionRoute> exceptionRoutes;
    protected PersistContextScope persistContextScope;

    Node(String id, List<ExceptionRoute> exceptionRoutes) {
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
        Exception cause;
        WokflowExecutionException executionException;
        if (e instanceof WokflowExecutionException) {
            cause = (Exception) e.getCause();
            executionException = (WokflowExecutionException) e;
        } else {
            // TODO log an exception
            cause = e;
            executionException = new WokflowExecutionException(e, context.getExecutionContext().getExecutionPoint());
        }
        for (ExceptionRoute exceptionRoute: getExceptionRoutes()) {
            if (exceptionRoute.test(cause)) {
                return exceptionRoute.getNodeId();
            }
        }
        throw executionException;
    }

    abstract PersistContextScope getPersistenceScope();

    protected void enterNode(WorkflowContext<T> workflowContext) {
    }

    protected void exitNode(WorkflowContext<T> workflowContext) {
    }

    public void setPersistenceScope(PersistContextScope persistContextScope) {
        this.persistContextScope = persistContextScope;
    }
}
