package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;
import tech.becloud.mage.persistence.WorkflowContextRepository;

import static tech.becloud.mage.graph.ExecutionState.PAUSED;

class RunnerBase<T extends UserContext> {
    protected final WorkflowContext<T> workflowContext;

    protected RunnerBase(WorkflowContext<T> workflowContext) {
        this.workflowContext = workflowContext;
    }


    protected boolean isPauseRequested(final Node<T> currentnode, final boolean nodeExecuted) {
        return workflowContext.getExecutionContext().isPauseRequested() || (currentnode.isPause() && nodeExecuted);
    }

    protected void pause() {
        ExecutionContext<T> executionContext = workflowContext.getExecutionContext();
        executionContext.setExecutionState(PAUSED);
        executionContext.setPauseRequested(false);
        executionContext.getPausedCompletableFuture().complete(executionContext.getExecutionId());
    }

    protected void persistState(PersistContextScope persistContextScope) {
        ExecutionContext<T> executionContext = workflowContext.getExecutionContext();
        WorkflowContextRepository<T> repository = executionContext.getWorkflowContextRepository();
        if (repository == null) {
            return;
        }
        if (persistContextScope == PersistContextScope.ALL) {
            repository.save(workflowContext);
        } else if (persistContextScope == PersistContextScope.EXECUTION) {
            repository.saveExecutionContext(executionContext);
        } else if (persistContextScope == PersistContextScope.USER) {
            String executionPoint = executionContext.getExecutionPoint();
            repository.saveUserContext(workflowContext.getExecutionId(), executionPoint, workflowContext.getUserContext());
        }
    }

    protected String completeFlow(WokflowExecutionException ex) {
        final ExecutionContext<T> executionContext = workflowContext.getExecutionContext();
        String nextNodeId = null;
        do {
            FlowCall<T> completedFlowCall = executionContext.popFlowCall();
            try {
                nextNodeId = completedFlowCall.flowCompletionHandler.handle(workflowContext, ex);
            } catch (WokflowExecutionException e) {
                ex = e;
            }
        } while (nextNodeId == null && executionContext.peekFlowCall() != null);
        return nextNodeId;
    }
}
