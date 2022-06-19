package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.UserContext;
import tech.becloud.workflow.model.WorkflowContext;

import java.util.concurrent.Callable;

class FlowCallable<T extends UserContext> implements Callable<Void> {
    private final Flow<T> flow;
    private final WorkflowContext<T> workflowContext;

    FlowCallable(Flow<T> flow, WorkflowContext<T> workflowContext) {
        this.flow = flow;
        this.workflowContext = workflowContext;
    }

    @Override
    public Void call() throws Exception {
        flow.accept(workflowContext);
        return null;
    }
}
