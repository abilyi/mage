package tech.becloud.workflow.graph;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class FlowBuilder<T> {

    public static final String ERROR_NO_BEAN_RESOLVER = "BeanResolver must be supplied to refer beans in FlowBuilder";

    private final Class<T> contextClass;
    private BeanResolver beanResolver;
    private String startNode;
    private final Map<String, NodeBuilder<T, ?>> nodeBuilders;

    private FlowBuilder(Class<T> contextClass) {
        this.contextClass = contextClass;
        nodeBuilders = new HashMap<>();
    }

    public static <T> FlowBuilder<T> flowBuilderFor(Class<T> contextClass) {
        return new FlowBuilder<>(contextClass);
    }

    public ActionNodeBuilder<T> execute(String id, Consumer<? super T> action) {
        ActionNodeBuilder<T> builder = new ActionNodeBuilder<T>(this, id, action);
        if (nodeBuilders.isEmpty()) {
            startNode = id;
        }
        nodeBuilders.put(id, builder);
        return builder;
    }

    public ActionNodeBuilder<T> execute(String id, String beanName) {
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        Consumer<T> action = beanResolver.getConsumer(beanName, contextClass);
        return execute(id, action);
    }

    public ActionNodeBuilder<T> execute(String id) {
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        Consumer<T> action = beanResolver.getConsumer(id, contextClass);
        return execute(id, action);
    }

    public RouterBuilder<T> route(String id) {
        return new RouterBuilder<>(this, id);
    }

    public SubflowBuilder<T> subflow(String id, Flow<T> flow) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException("Node with id " + id + " already defined.");
        }
        SubflowBuilder<T> builder = new SubflowBuilder<>(this, id, flow);
        nodeBuilders.put(id, builder);
        return builder;
    }

    public SubflowBuilder<T> subflow(String id, String flowBean) {
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        SubflowBuilder<T> builder = new SubflowBuilder<>(this, id, beanResolver.getFlow(flowBean, contextClass));
        nodeBuilders.put(id, builder);
        return builder;
    }

    public void add(FlowBuilder<T> builder) {
        for (Map.Entry<String, NodeBuilder<T, ?>> entry : builder.nodeBuilders.entrySet()) {
            String id = entry.getKey();
            if (nodeBuilders.containsKey(id) && nodeBuilders.get(id) != entry.getValue()) {
                throw new IllegalArgumentException("Unadle to add flow due to conflicting key " + id);
            } else {
                nodeBuilders.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public Flow<T> build() {
        Map<String, Node<T>> nodes = new HashMap<>();
        // TODO validate builders so all references can be resolved
        nodeBuilders.forEach((id, builder) -> nodes.put(id, builder.build()));
        return new Flow<>(nodes, startNode);
    }

    /**
     * Allows to use methods that accepts a name in place of Predicate or Consumer.
     * Supplied {@link BeanResolver} implementation will be used to lookup corresponding objects by name.
     * @param beanResolver
     */
    public void setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }
}
