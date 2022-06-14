package tech.becloud.workflow.graph;

import lombok.AccessLevel;
import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionNodeBuilder<T> extends NodeBuilder<T, ActionNodeBuilder<T>> {
    @Setter
    private Predicate<? super T> predicate;
    private final Consumer<? super T> action;
    private ActionNode<T> node;
    @Setter(value = AccessLevel.PACKAGE)
    private String nextNode;

    public ActionNodeBuilder(FlowBuilder<T> flowBuilder, String id, Consumer<? super T> action) {
        super(flowBuilder, id);
        this.action = action;
    }

    public ActionNodeBuilder<T> ifMatch(Predicate<? super T> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public Node<T> build() {
        if (node != null) {
            return node;
        }
        node = new ActionNode<T>(id, action, nextNode, exceptionRoutes);
        return node;
    }

    public ActionNodeBuilder<T> execute(String id, Consumer<? super T> action) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, action);
        nextNode = id;
        return next;
    }

    public ActionNodeBuilder<T> execute(String id, String beanName) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, beanName);
        nextNode = id;
        return next;
    }

    public ActionNodeBuilder<T> execute(String id) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id);
        nextNode = id;
        return next;
    }

    public RouterBuilder<T> routeTo(String id, Predicate<? super T> predicate, String targetNodeId) {
        RouterBuilder<T> routerBuilder = flowBuilder.routeTo(id, predicate, targetNodeId);
        nextNode = id;
        return routerBuilder;
    }

    public RouterBuilder<T> route(String id, Predicate<? super T> predicate, FlowBuilder<T> flowBuilder) {
        RouterBuilder<T> routerBuilder = flowBuilder.route(id, predicate, flowBuilder);
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
