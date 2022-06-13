package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;
import tech.becloud.workflow.persistence.WorkflowContextRepository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Flow<T> implements Consumer<WorkflowContext<T>> {
    private final Map<String, Node<T>> nodes;
    private final String startNodeId;
    private WorkflowContextRepository<T> workflowContextRepository;

    public Flow(Map<String, Node<T>> nodes, String startNodeId) {
        this.nodes = Map.copyOf(nodes);
        this.startNodeId = startNodeId;
    }

    @Override
    public void accept(WorkflowContext<T> context) {
        ExecutionContext execution = context.getExecutionContext();
        final int subflowLevel = execution.getSubflowDepth();
        String currentNodeId = Optional.ofNullable(execution.getCurrentNodePath().get(subflowLevel))
                .orElse(startNodeId);
        do {
            execution.getCurrentNodePath().set(execution.getSubflowDepth(), currentNodeId);
            Node<T> currentNode = nodes.get(currentNodeId);
            currentNodeId = currentNode.apply(context);
            execution.getCurrentNodePath().set(subflowLevel, currentNodeId);
            persistContext(currentNode.getPersistenceScope(), context);
        } while (currentNodeId != null);
    }

    private void persistContext(PersistContextScope persistenceScope, WorkflowContext<T> context) {
        if (workflowContextRepository == null || persistenceScope == PersistContextScope.NONE) {
            return;
        }
        switch (persistenceScope) {
            case EXECUTION:
                workflowContextRepository.saveExecutionContext(context.getExecutionContext());
                break;
            case USER:
                ExecutionContext execution = context.getExecutionContext();
                workflowContextRepository.saveUserContext(execution.getExecutionId(), execution.getExecutionPoint(),
                        context.getContext());
                break;
            case ALL:
                workflowContextRepository.save(context);
                break;
        }
    }

    public void setWorkflowContextRepository(WorkflowContextRepository<T> workflowContextRepository) {
        this.workflowContextRepository = workflowContextRepository;
    }

    public void setPersistContextScope(PersistContextScope persistContextScope) {
        nodes.forEach((id, node) -> node.setPersistenceScope(persistContextScope));
    }
}
