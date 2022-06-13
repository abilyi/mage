package tech.becloud.workflow.graph;

import java.util.function.Consumer;

public class SubflowBuilder<T> extends NodeBuilder<T, SubflowBuilder<T>> {
    private final Flow<T> flow;
    private String nextNode;

    public SubflowBuilder(FlowBuilder<T> flowBuilder, String id, Flow<T> flow) {
        super(flowBuilder, id);
        this.flow = flow;
    }

    public Node<T> build() {
        return new SubflowNode<>(id, flow, nextNode, exceptionRoutes);
    }

    public ActionNodeBuilder<T> execute(String id, Consumer<? super T> action) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, action);
        nextNode = id;
        return next;
    }

    public ActionNodeBuilder<T> execute(String id, String consumerBean) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, consumerBean);
        nextNode = id;
        return next;
    }

    public ActionNodeBuilder<T> execute(String id) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id);
        nextNode = id;
        return next;
    }

    public RouterBuilder<T> route(String id) {
        RouterBuilder<T> routerBuilder = flowBuilder.route(id);
        nextNode = id;
        return routerBuilder;
    }

    public SubflowBuilder<T> subflow(String id, Flow<T> flow) {
        SubflowBuilder<T> next = flowBuilder.subflow(id, flow);
        nextNode = id;
        return next;
    }

    public SubflowBuilder<T> subflow(String id, String flowBean) {
        SubflowBuilder<T> next = flowBuilder.subflow(id, flowBean);
        nextNode = id;
        return next;
    }
}
