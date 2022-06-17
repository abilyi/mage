package tech.becloud.workflow.graph;

import org.junit.jupiter.api.BeforeEach;
import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;

public class FlowTestBase {
    protected TestContext context;
    protected WorkflowContext<TestContext> workflowContext;

    @BeforeEach
    public void setupContext() {
        context = new TestContext();
        ExecutionContext executionContext = new ExecutionContext("test", 1, UUID.randomUUID());
        executionContext.getCurrentNodePath().add("test");
        workflowContext = new WorkflowContext<>(executionContext, context);
    }
}
