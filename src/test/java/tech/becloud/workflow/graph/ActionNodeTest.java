package tech.becloud.workflow.graph;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionNodeTest extends SimpleNodeTestBase {

    @Test
    void testSuccessfulExecution() {
        ActionNode<TestContext> node = new ActionNode<>("test", new Consumer1(), "next", Collections.emptyList());
        String nextId = node.apply(workflowContext);
        assertTrue(context.action1);
        assertEquals("next", nextId);
    }

    @Test
    void testConditionalExecution() {
        ActionNode<TestContext> node = new ActionNode<>("test", t -> t.condition, new Consumer1(), "next", Collections.emptyList());
        String nextId = node.apply(workflowContext);
        assertFalse(context.action1);
        assertEquals("next", nextId);
        context.condition = true;
        nextId = node.apply(workflowContext);
        assertTrue(context.action1);
        assertEquals("next", nextId);
    }

    @Test
    void testHandledException() {
        List<ExceptionRoute> exceptionRoutes = List.of(new ExceptionRoute(IllegalStateException.class, "errorPath"));
        ActionNode<TestContext> node = new ActionNode<>("test", new ThrowingConsumer(), "nextNode", exceptionRoutes);
        String nextNode = node.apply(workflowContext);
        assertEquals("errorPath", nextNode);
    }

    @Test
    void testUnhandledException() {
        List<ExceptionRoute> exceptionRoutes = List.of(new ExceptionRoute(NullPointerException.class, "errorPath"));
        ActionNode<TestContext> node = new ActionNode<>("test", new ThrowingConsumer(), "next", exceptionRoutes);
        assertThrows(WokflowExecutionException.class, () -> node.apply(workflowContext));
    }
}
