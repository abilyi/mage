package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;

public class FlowRunner<T extends UserContext> extends RunnerBase<T> implements Runnable {
    private final Flow<T> flow;

    FlowRunner(Flow<T> flow, WorkflowContext<T> workflowContext) {
        super(workflowContext);
        this.flow = flow;
    }

    @Override
    public void run() {
        final ExecutionContext<T> execution = workflowContext.getExecutionContext();
        if(execution.getExecutionPoint() != null) {
            restart();
        }
        boolean useNodePersistenceScope;
        FlowCall<T> flowCall = execution.peekFlowCall();
        String nextNodeId = flowCall.getNodeId();
        do {
            Node<T> currentNode = flowCall.flow.getNode(nextNodeId);
            if (isPauseRequested(currentNode, false)) {
                pause();
            }
            useNodePersistenceScope = false;
            try {
                nextNodeId = currentNode.apply(workflowContext);
                if (nextNodeId == null) {
                    nextNodeId = completeFlow(null);
                } else {
                    useNodePersistenceScope = true;
                }
            } catch (WokflowExecutionException e) {
                nextNodeId = completeFlow(e);
            }
            if (nextNodeId == null) {
                continue;
            }
            if (isPauseRequested()) {
                pause();
            }
            execution.peekFlowCall().setNodeId(nextNodeId);
            execution.updateExecutionPoint();
            persistState(useNodePersistenceScope ? currentNode.getPersistContextScope() : PersistContextScope.ALL);
        } while (nextNodeId != null && execution.getExecutionState() == ExecutionState.RUNNING);

    }

    private void restart() {

    }
}
