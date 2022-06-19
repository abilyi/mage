package tech.becloud.workflow.persistence;

import tech.becloud.workflow.graph.ExecutionContext;
import tech.becloud.workflow.model.UserContext;
import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;

public interface WorkflowContextRepository<T extends UserContext> {

    T loadUserContext(UUID executionId, String executionPath);

    void saveUserContext(UUID executionId, String executionPath, T context);

    ExecutionContext<T> loadExecutionContext(UUID executionId);

    void saveExecutionContext(ExecutionContext<T> executionContext);

    WorkflowContext<T> load(UUID executionId);

    void save(WorkflowContext<T> context);
}
