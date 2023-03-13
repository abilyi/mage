package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;

import java.util.List;
import java.util.Optional;

public class SubflowNode<T extends UserContext> extends Node<T> implements FlowCompletionHandler<T> {

    private final Flow<T> flow;
    private final String nextNodeId;

    SubflowNode(String id, Flow<T> flow, String nextNode, List<ExceptionRoute<T>> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.flow = flow;
        this.nextNodeId = nextNode;
    }

    @Override
    public String apply(WorkflowContext<T> context) {
        ExecutionContext<T> executionContext = context.getExecutionContext();
        executionContext.pushFlowCall(flow, this);
        return Optional.ofNullable(executionContext.getExecutionPoint())
                .map(path -> path.split("/"))
                .filter(sp -> sp.length >= executionContext.getSubflowDepth())
                .map(sp -> sp[executionContext.getSubflowDepth() - 1])
                .orElseGet(flow::getStartNode);
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

    @Override
    public String handle(WorkflowContext<T> workflowContext, WokflowExecutionException ex) {
        return (ex == null) ? nextNodeId : routeOnException(ex, workflowContext);
    }

    public Flow<T> getFlow() {
        return flow;
    }

    public String getNextNodeId() {
        return nextNodeId;
    }
}
