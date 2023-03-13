package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;

import static tech.becloud.mage.graph.ExecutionState.CANCELED;
import static tech.becloud.mage.graph.ExecutionState.PAUSED;

public class NodeRunner<T extends UserContext> extends RunnerBase<T> implements Runnable {

    public NodeRunner(WorkflowContext<T> workflowContext) {
        super(workflowContext);
    }

    @Override
    public void run() {
        final ExecutionContext<T> executionContext = workflowContext.getExecutionContext();
        FlowCall<T> currentFlow = executionContext.peekFlowCall();
        final String nodeId = currentFlow.getNodeId();
        boolean useNodePersistenceScope = true;
        if (executionContext.isPauseRequested()) {
            pause();
            return;
        }
        Node<T> currentNode = currentFlow.flow.getNode(nodeId);
        String nextNodeId;
        try {
            nextNodeId = currentNode.apply(workflowContext);
            if (nextNodeId == null) {
                nextNodeId = completeFlow(null);
            }
        } catch (WokflowExecutionException e) {
            nextNodeId = completeFlow(e);
            useNodePersistenceScope = false;
        }
        if (nextNodeId == null) {
            return;
        }
        executionContext.peekFlowCall().setNodeId(nextNodeId);
        executionContext.updateExecutionPoint();
        if (executionContext.isPauseRequested()) {
            pause();
        }
        persistState(useNodePersistenceScope ? currentNode.getPersistContextScope() : PersistContextScope.ALL);
        if (executionContext.getExecutionState() != PAUSED && executionContext.getExecutionState() != CANCELED) {
            executionContext.getExecutorService().submit(this);
        }
    }

}
