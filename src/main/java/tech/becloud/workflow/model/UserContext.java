package tech.becloud.workflow.model;

import java.util.UUID;

public interface UserContext {

    UUID getExecutionId();

    void setExecutionId(UUID executionId);
}
