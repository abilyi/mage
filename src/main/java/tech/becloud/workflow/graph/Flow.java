package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.UserContext;
import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;
import tech.becloud.workflow.persistence.WorkflowContextRepository;

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
                        eh -> eh.handle(context.getContext(), e.getCause(), e.getExecutionPath())
                );
                break;
            }
            execution.getCurrentNodePath().set(subflowLevel, currentNodeId);
            persistContext(currentNode.getPersistenceScope(), context);
            if (execution.isPauseRequested()) {
                execution.setExecutionState(ExecutionState.PAUSED);
                execution.setPauseRequested(false);
            }
        } while (currentNodeId != null && execution.getExecutionState() == ExecutionState.RUNNING);
        if (currentNodeId == null && subflowLevel == 0) {
            execution.setExecutionState(ExecutionState.COMPLETED);
        }
        final ExecutionState state = execution.getExecutionState();
        BiConsumer<? super T, ExecutionState> completionHandler = execution.getCompletionHandler();
        if (completionHandler != null && (state == ExecutionState.COMPLETED || state == ExecutionState.FAILED)) {
            completionHandler.accept(context.getContext(), state);
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
                        context.getContext());
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
}
