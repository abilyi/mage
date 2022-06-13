package tech.becloud.workflow.model;

import lombok.Getter;
import tech.becloud.workflow.graph.ExecutionContext;

@Getter
public class WorkflowContext<T> {
    private final ExecutionContext executionContext;
    private final T context;

    public WorkflowContext(ExecutionContext executionContext, T context) {
        this.executionContext = executionContext;
        this.context = context;
    }
}
