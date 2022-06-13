package tech.becloud.workflow.graph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
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
}
