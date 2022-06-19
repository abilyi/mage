package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class NodeBuilder<T extends UserContext, N extends NodeBuilder<T, N>> {

    protected final FlowBuilder<T> flowBuilder;
    protected final String id;
    protected final List<ExceptionRoute<T>> exceptionRoutes;
    protected boolean pause;

    protected NodeBuilder(FlowBuilder<T> flowBuilder, String id) {
        this.flowBuilder = flowBuilder;
        this.id = id;
        exceptionRoutes = new ArrayList<>();
        this.pause = false;
    }

    @SuppressWarnings("unchecked")
    public N onException(Class<? extends Exception> exceptionClass, String nodeId) {
        exceptionRoutes.add(new ExceptionRoute<T>(exceptionClass, null, nodeId));
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N onException(Class<? extends Throwable> exceptionClass, BiConsumer<? super T, Throwable> handler,
                         String nodeId) {
        exceptionRoutes.add(new ExceptionRoute<T>(exceptionClass, handler, nodeId));
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N pause() {
        this.pause = true;
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
    public List<ExceptionRoute<T>> getExceptionRoutes() {
        return exceptionRoutes;
    }
}
