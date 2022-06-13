package tech.becloud.workflow.persistence;

import tech.becloud.workflow.graph.ExecutionContext;
import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;

public interface WorkflowContextRepository<T> {

    T loadUserContext(UUID executionId, String executionPath);

    void saveUserContext(UUID executionId, String executionPath, T context);

    ExecutionContext loadExecutionContext(UUID executionId);

    void saveExecutionContext(ExecutionContext executionContext);

    WorkflowContext<T> load(UUID executionId);

    void save(WorkflowContext<T> context);
}
