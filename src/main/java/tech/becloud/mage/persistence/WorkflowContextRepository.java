package tech.becloud.mage.persistence;

import tech.becloud.mage.graph.ExecutionContext;
import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;

import java.util.UUID;

public interface WorkflowContextRepository<T extends UserContext> {

    T loadUserContext(UUID executionId, String executionPath);

    void saveUserContext(UUID executionId, String executionPath, T context);

    ExecutionContext<T> loadExecutionContext(UUID executionId);

    void saveExecutionContext(ExecutionContext<T> executionContext);

    WorkflowContext<T> load(UUID executionId);

    void save(WorkflowContext<T> context);
}
