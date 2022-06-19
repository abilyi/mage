package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestContext implements UserContext {
    boolean action1;
    boolean action2;
    boolean condition;
    List<String> history;

    public TestContext() {
        history = new ArrayList<>();
    }

    @Override
    public UUID getExecutionId() {
        return null;
    }

    @Override
    public void setExecutionId(UUID executionId) {

    }
}
