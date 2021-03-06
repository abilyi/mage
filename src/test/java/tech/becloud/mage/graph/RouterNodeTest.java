package tech.becloud.mage.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouterNodeTest extends FlowTestBase {

    @Test
    void testRouting() {
        List<Route<? super TestContext>> routes = List.of(new Route<TestContext>(t -> t.action1, "Step2"),
                new Route<TestContext>(t -> t.action2, "Step3"));
        List<ExceptionRoute<TestContext>> exceptionRoutes = List.of(
                new ExceptionRoute<TestContext>(IllegalStateException.class, "IllegalStateRecovery"));
        RouterNode<TestContext> node = new RouterNode<>("Route1", routes, "DefaultStep", exceptionRoutes);
        String path = node.apply(workflowContext);
        assertEquals("DefaultStep", path);
        workflowContext.getUserContext().action2 = true;
        path = node.apply(workflowContext);
        assertEquals("Step3", path);
        workflowContext.getUserContext().action1 = true;
        path = node.apply(workflowContext);
        assertEquals("Step2", path);
    }

    @Test
    void testHandledException() {
        List<Route<? super TestContext>> routes = List.of(new Route<TestContext>(t -> t.action1, "Step2"),
                new Route<TestContext>(new ThrowingPredicate(), "Step3"));
        List<ExceptionRoute<TestContext>> exceptionRoutes = List.of(
                new ExceptionRoute<TestContext>(IllegalStateException.class, "IllegalStateRecovery"));
        RouterNode<TestContext> node = new RouterNode<>("Route1", routes, "DefaultStep", exceptionRoutes);
        String path = node.apply(workflowContext);
        assertEquals("IllegalStateRecovery", path);
    }

    @Test
    void testUnhandledException() {
        List<Route<? super TestContext>> routes = List.of(new Route<TestContext>(t -> t.action1, "Step2"),
                new Route<TestContext>(new ThrowingPredicate(), "Step3"));
        List<ExceptionRoute<TestContext>> exceptionRoutes = List.of(
                new ExceptionRoute<TestContext>(IllegalArgumentException.class, "IllegalArgumentRecovery"));
        RouterNode<TestContext> node = new RouterNode<>("Route1", routes, "DefaultStep", exceptionRoutes);
        assertThrows(WokflowExecutionException.class, () -> node.apply(workflowContext));
    }

    private static class ThrowingPredicate implements Predicate<TestContext> {
        @Override
        public boolean test(TestContext testContext) {
            throw new IllegalStateException("test");
        }
    }
}
