package tech.becloud.mage.model;

import tech.becloud.mage.graph.ExecutionContext;

import java.util.UUID;

public class WorkflowContext<T extends UserContext> {
    private final ExecutionContext<T> executionContext;
    private final T context;

    public WorkflowContext(ExecutionContext<T> executionContext, T context) {
        this.executionContext = executionContext;
        this.context = context;
    }

    public ExecutionContext<T> getExecutionContext() {
        return executionContext;
    }

    public T getContext() {
        return context;
    }

    public UUID getExecutionId() {
        return executionContext.getExecutionId();
    }

    public void requestPause() {
        executionContext.setPauseRequested(true);
    }
}
