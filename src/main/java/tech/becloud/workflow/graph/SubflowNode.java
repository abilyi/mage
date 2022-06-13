package tech.becloud.workflow.graph;

import lombok.Getter;
import lombok.Setter;
import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class SubflowNode<T> extends Node<T> {

    private final Flow<T> flow;
    private final String nextNodeId;

    SubflowNode(String id, Flow<T> flow, String nextNode, List<ExceptionRoute> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.flow = flow;
        this.nextNodeId = nextNode;
    }

    @Override
    protected String executeAction(WorkflowContext<T> context) {
        flow.accept(context);
        return nextNodeId;
    }

    @Override
    protected void enterNode(WorkflowContext<T> workflowContext) {
        ExecutionContext execution = workflowContext.getExecutionContext();
        execution.setSubflowDepth(execution.getSubflowDepth() + 1);
        if (execution.getSubflowDepth() == execution.getCurrentNodePath().size()) {
            execution.getCurrentNodePath().add(null);
        }
    }

    @Override
    protected void exitNode(WorkflowContext<T> workflowContext) {
        ExecutionContext execution = workflowContext.getExecutionContext();
        execution.getCurrentNodePath().remove(execution.getSubflowDepth());
        execution.setSubflowDepth(execution.getSubflowDepth() - 1);
    }

    @Override
    PersistContextScope getPersistenceScope() {
        return Optional.ofNullable(persistContextScope).orElse(PersistContextScope.ALL);
    }

    @Override
    public void setPersistenceScope(PersistContextScope persistContextScope) {
        this.persistContextScope = persistContextScope;
        flow.setPersistContextScope(persistContextScope);
    }
}