package tech.becloud.workflow.graph;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeBuilder<T, N extends NodeBuilder<T, N>> {

    protected final FlowBuilder<T> flowBuilder;
    protected final String id;
    protected final List<ExceptionRoute> exceptionRoutes;

    protected NodeBuilder(FlowBuilder<T> flowBuilder, String id) {
        this.flowBuilder = flowBuilder;
        this.id = id;
        exceptionRoutes = new ArrayList<>();
    }

    public N onException(Class<? extends Exception> exceptionClass, String nodeId) {
        exceptionRoutes.add(new ExceptionRoute(exceptionClass, nodeId));
        return (N) this;
    }

    public abstract Node<T> build();

    public FlowBuilder<T> done() {
        return flowBuilder;
    }

    /**
     *
     * @return parent {@link FlowBuilder}, this NodeBuilder belongs to.
     */
    public FlowBuilder<T> getFlowBuilder() {
        return flowBuilder;
    }

    /**
     * @return This node (step) id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public List<ExceptionRoute> getExceptionRoutes() {
        return exceptionRoutes;
    }
}
