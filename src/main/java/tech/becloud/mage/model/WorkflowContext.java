package tech.becloud.mage.model;

import tech.becloud.mage.graph.ExecutionContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WorkflowContext<T extends UserContext> {
    private final ExecutionContext<T> executionContext;
    private final T userContext;

    public WorkflowContext(ExecutionContext<T> executionContext, T userContext) {
        this.executionContext = executionContext;
        this.userContext = userContext;
    }

    public ExecutionContext<T> getExecutionContext() {
        return executionContext;
    }

    public T getUserContext() {
        return userContext;
    }

    public UUID getExecutionId() {
        return executionContext.getExecutionId();
    }

    public CompletableFuture<UUID> requestPause() {
        if (executionContext.isPauseRequested()) {
            CompletableFuture<UUID> paused = new CompletableFuture<>();
            executionContext.setPausedCompletableFuture(paused);
            executionContext.setPauseRequested(true);
            return paused;
        } else {
            return executionContext.getPausedCompletableFuture();
        }
    }
}
