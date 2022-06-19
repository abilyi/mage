package tech.becloud.mage.graph;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionNodeTest extends FlowTestBase {

    @Test
    void testSuccessfulExecution() {
        ActionNode<TestContext> node = new ActionNode<>("test", new TrackingConsumer("Step 1"), "next", Collections.emptyList());
        String nextId = node.apply(workflowContext);
        assertEquals("next", nextId);
        assertEquals(1, context.history.size());
        assertEquals("Step 1", context.history.get(0));
    }

    @Test
    void testConditionalExecution() {
        ActionNode<TestContext> node = new ActionNode<>("test", t -> t.condition, new TrackingConsumer("Step 1"), "next", Collections.emptyList());
        String nextId = node.apply(workflowContext);
        assertEquals(0, context.history.size());
        assertEquals("next", nextId);
        context.condition = true;
        nextId = node.apply(workflowContext);
        assertEquals(1, context.history.size());
        assertEquals("Step 1", context.history.get(0));
        assertEquals("next", nextId);
    }

    @Test
    void testHandledException() {
        List<ExceptionRoute<TestContext>> exceptionRoutes = List.of(
                new ExceptionRoute<TestContext>(IllegalStateException.class, "errorPath"));
        ActionNode<TestContext> node = new ActionNode<>("test", new ThrowingConsumer(), "nextNode", exceptionRoutes);
        String nextNode = node.apply(workflowContext);
        assertEquals("errorPath", nextNode);
    }

    @Test
    void testUnhandledException() {
        List<ExceptionRoute<TestContext>> exceptionRoutes = List.of(
                new ExceptionRoute<TestContext>(NullPointerException.class, "errorPath"));
        ActionNode<TestContext> node = new ActionNode<>("test", new ThrowingConsumer(), "next", exceptionRoutes);
        assertThrows(WokflowExecutionException.class, () -> node.apply(workflowContext));
    }
}
