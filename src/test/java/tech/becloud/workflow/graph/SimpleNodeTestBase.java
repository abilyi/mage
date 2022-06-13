package tech.becloud.workflow.graph;

import org.junit.jupiter.api.BeforeEach;
import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;

public class SimpleNodeTestBase {
    protected TestContext context;
    protected WorkflowContext<TestContext> workflowContext;

    @BeforeEach
    public void setUp() {
        context = new TestContext();
        ExecutionContext executionContext = new ExecutionContext("test", 1, UUID.randomUUID());
        executionContext.getCurrentNodePath().add("test");
        workflowContext = new WorkflowContext<>(executionContext, context);
    }
}
