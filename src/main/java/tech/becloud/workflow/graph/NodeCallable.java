package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;

import java.util.concurrent.Callable;

public class NodeCallable<T> implements Callable<String> {
    private final Node<T> node;
    private final WorkflowContext<T> context;

    public NodeCallable(Node<T> node, WorkflowContext<T> context) {
        this.node = node;
        this.context = context;
    }

    @Override
    public String call() throws Exception {
        return node.apply(context);
    }
}
