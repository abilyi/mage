package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Workflow<T> {

    private final String id;
    private final int version;
    private final Flow<T> flow;

    public Workflow(String id, int version, Flow<T> flow) {
        this.id = id;
        this.version = version;
        this.flow = flow;
    }

    public Future<Void> start(T context, ExecutorService executorService) {
        ExecutionContext executionContext = new ExecutionContext(id, version, UUID.randomUUID());
        WorkflowContext<T> workflowContext = new WorkflowContext<>(executionContext, context);
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    public void start(T context) {
        ExecutionContext executionContext = new ExecutionContext(id, version, UUID.randomUUID());
        WorkflowContext<T> workflowContext = new WorkflowContext<>(executionContext, context);
        flow.accept(workflowContext);
    }

    public Future<Void> start(WorkflowContext<T> workflowContext, ExecutorService executorService) {
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    public void start(WorkflowContext<T> workflowContext) {
        flow.accept(workflowContext);
    }
}
