package tech.becloud.mage.graph;

import org.junit.jupiter.api.BeforeEach;
import tech.becloud.mage.model.WorkflowContext;

import java.util.UUID;

public class FlowTestBase {
    protected TestContext context;
    protected WorkflowContext<TestContext> workflowContext;

    @BeforeEach
    public void setupContext() {
        context = new TestContext();
        ExecutionContext<TestContext> executionContext = new ExecutionContext<>("test", 1, UUID.randomUUID());
        executionContext.getCurrentNodePath().add("test");
        context.setExecutionId(executionContext.getExecutionId());
        workflowContext = new WorkflowContext<>(executionContext, context);
    }
}
