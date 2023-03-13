package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;
import tech.becloud.mage.persistence.WorkflowContextRepository;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Flow<T extends UserContext> implements Consumer<WorkflowContext<T>> {
    private final Map<String, Node<T>> nodes;
    private final String startNodeId;
    private WorkflowContextRepository<T> workflowContextRepository;

    public Flow(Map<String, Node<T>> nodes, String startNodeId) {
        this.nodes = Map.copyOf(nodes);
        this.startNodeId = startNodeId;
    }

    @Override
    public void accept(WorkflowContext<T> context) {
        ExecutionContext<T> execution = context.getExecutionContext();
        final int subflowLevel = execution.getSubflowDepth();
        String currentNodeId = Optional.ofNullable(execution.getCurrentNodePath().get(subflowLevel))
                .orElse(startNodeId);
        execution.setExecutionState(ExecutionState.RUNNING);
        do {
            Node<T> currentNode = nodes.get(currentNodeId);
            try {
                currentNodeId = currentNode.apply(context);
            } catch (WokflowExecutionException e) {
                execution.setExecutionState(ExecutionState.FAILED);
                Optional.ofNullable(execution.getExceptionHandler()).ifPresent(
                        eh -> eh.handle(context.getUserContext(), e.getCause(), e.getExecutionPath())
                );
                break;
            }
            execution.getCurrentNodePath().set(subflowLevel, currentNodeId);
            PersistContextScope persistenceScope = currentNode.getPersistenceScope();
            persistContext(persistenceScope, context);
            if (execution.isPauseRequested()) {
                execution.setExecutionState(ExecutionState.PAUSED);
                execution.setPauseRequested(false);
                execution.getPausedCompletableFuture().complete(null);
            }
        } while (currentNodeId != null && execution.getExecutionState() == ExecutionState.RUNNING);
        if (currentNodeId == null && subflowLevel == 0) {
            execution.setExecutionState(ExecutionState.COMPLETED);
        }
        final ExecutionState state = execution.getExecutionState();
        BiConsumer<? super T, ExecutionState> completionHandler = execution.getCompletionHandler();
        if (completionHandler != null && (state == ExecutionState.COMPLETED || state == ExecutionState.FAILED)) {
            completionHandler.accept(context.getUserContext(), state);
        }
    }

    private void persistContext(PersistContextScope persistenceScope, WorkflowContext<T> context) {
        if (workflowContextRepository == null) {
            return;
        }
        switch (persistenceScope) {
            case EXECUTION:
                workflowContextRepository.saveExecutionContext(context.getExecutionContext());
                break;
            case USER:
                ExecutionContext<T> execution = context.getExecutionContext();
                workflowContextRepository.saveUserContext(execution.getExecutionId(), execution.getExecutionPoint(),
                        context.getUserContext());
                break;
            case ALL:
                workflowContextRepository.save(context);
                break;
            case NONE:
                break;
        }
    }

    public void setWorkflowContextRepository(WorkflowContextRepository<T> workflowContextRepository) {
        this.workflowContextRepository = workflowContextRepository;
        // Propagate repository to subflows
        nodes.values().stream().filter(SubflowNode.class::isInstance)
                .map(node -> (SubflowNode<T>)node)
                .map(SubflowNode::getFlow)
                .forEach(flow -> setWorkflowContextRepository(workflowContextRepository));
    }

    public void setPersistContextScope(PersistContextScope persistContextScope) {
        nodes.forEach((id, node) -> node.setPersistenceScope(persistContextScope));
    }

    public String getStartNode() {
        return startNodeId;
    }

    public Node<T> getNode(String nodeId) {
        if (nodeId == null) {
            nodeId = startNodeId;
        }
        return nodes.get(nodeId);
    }
}
