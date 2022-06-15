package tech.becloud.workflow.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FlowBuilder<T> {

    public static final String ERROR_NO_BEAN_RESOLVER = "BeanResolver must be supplied to refer beans in FlowBuilder";
    public static final String NODE_ALREADY_EXIST = "Node with id %s already defined.";

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

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>}
     * @param id workflow step id
     * @param action {@link Consumer<T>} implementation to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalArgumentException if step with same id already exists
     */
    public ActionNodeBuilder<T> execute(String id, Consumer<? super T> action) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException(String.format(NODE_ALREADY_EXIST, id));
        }
        ActionNodeBuilder<T> builder = new ActionNodeBuilder<T>(this, id, action);
        if (nodeBuilders.isEmpty()) {
            startNode = id;
        }
        nodeBuilders.put(id, builder);
        return builder;
    }

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>} bean.
     * @param id workflow step id
     * @param beanName bean implementing {@link Consumer<T>} interface to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalStateException if no {@link BeanResolver} supplied
     * @throws IllegalArgumentException if step with same id already exists
     *
     */
    public ActionNodeBuilder<T> execute(String id, String beanName) {
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        Consumer<T> action = beanResolver.getConsumer(beanName, contextClass);
        return execute(id, action);
    }

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>} bean.
     * @param id workflow step id, also used as bean name to lookup a {@link Consumer<T>} interface to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalStateException if no {@link BeanResolver} supplied
     * @throws IllegalArgumentException if step with same id already exists
     *
     */
    public ActionNodeBuilder<T> execute(String id) {
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        Consumer<T> action = beanResolver.getConsumer(id, contextClass);
        return execute(id, action);
    }

    /**
     * Adds a "router" step intended for dispatching further execution to a distinct path.
     * Basically, it is an if-elseif-else implementation in terms of workflow step.
     * @param id workflow step id
     * @param predicate predicate to test to select this route
     * @param nextNode id of node to be executed when condition is met
     * @return builder for {@link RouterNode<T>} implementing such step.Note that unlike other builders {@link RouterBuilder}
     * doesn't offers methods to 'continue' flow; instead it's routeIf() and defaultRoute() methods accepts subsequent
     * node or FlowBuilder, so further flow is built in scope of these methods call.
     * @throws IllegalArgumentException if step with same id already exists
     */
    public RouterBuilder<T> routeTo(String id, Predicate<? super T> predicate, String nextNode) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException(String.format(NODE_ALREADY_EXIST, id));
        }
        RouterBuilder<T> builder = new RouterBuilder<>(this, id).routeTo(predicate, nextNode);
        nodeBuilders.put(id, builder);
        return builder;
    }

    /**
     * Adds a "router" step intended for dispatching further execution to a distinct path.
     * Basically, it is an if-elseif-else implementation in terms of workflow step.
     * @param id workflow step id
     * @param predicate predicate to test to select this route
     * @param flowBuilder execution will be routed to the start step of supplied builder, builder itself will be added
     *                    to current one, see {@link FlowBuilder#add(FlowBuilder)}.
     * @return builder for {@link RouterNode<T>} implementing such step.Note that unlike other builders {@link RouterBuilder}
     * doesn't offers methods to 'continue' flow; instead it's routeIf() and defaultRoute() methods accepts subsequent
     * node or FlowBuilder, so further flow is built in scope of these methods call.
     * @throws IllegalArgumentException if step with same id already exists
     */
    public RouterBuilder<T> route(String id, Predicate<? super T> predicate, FlowBuilder<T> flowBuilder) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException(String.format(NODE_ALREADY_EXIST, id));
        }
        RouterBuilder<T> builder = new RouterBuilder<>(this, id).route(predicate, flowBuilder);
        nodeBuilders.put(id, builder);
        return builder;
    }

    /**
     * Adds a subflow call step to workflow. Calls of subflow from subflow is supported.
     * The same flow may be used as main in one workflow and as subflow in another.
     * @param id workflow step id
     * @param flow flow to execute as subflow
     * @return a builder for {@link SubflowNode} implementing such step
     */
    public SubflowBuilder<T> subflow(String id, Flow<T> flow) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException(String.format(NODE_ALREADY_EXIST, id));
        }
        SubflowBuilder<T> builder = new SubflowBuilder<>(this, id, flow);
        nodeBuilders.put(id, builder);
        return builder;
    }

    /**
     * Adds a subflow call step to workflow. Calls of subflow from subflow is supported.
     * The same flow may be used as main in one workflow and as subflow in another.
     * @param id subflow step id
     * @param flowBean bean name to lookup {@link Flow} instance
     * @return
     */
    public SubflowBuilder<T> subflow(String id, String flowBean) {
        if (nodeBuilders.containsKey(id)) {
            throw new IllegalArgumentException(String.format(NODE_ALREADY_EXIST, id));
        }
        if (beanResolver == null) {
            throw new IllegalStateException(ERROR_NO_BEAN_RESOLVER);
        }
        SubflowBuilder<T> builder = new SubflowBuilder<>(this, id, beanResolver.getFlow(flowBean, contextClass));
        nodeBuilders.put(id, builder);
        return builder;
    }

    /**
     * Adds all nodes from given builder. In case of node name clash an {@link IllegalArgumentException}
     * is thrown unless clashing name references the same node builder.
     * @param builder A {@link FlowBuilder} to add nodes from
     * @throws {@link IllegalArgumentException} on node name clash referencing different node builders
     */
    public void add(FlowBuilder<T> builder) {
        for (Map.Entry<String, NodeBuilder<T, ?>> entry : builder.nodeBuilders.entrySet()) {
            String id = entry.getKey();
            if (nodeBuilders.containsKey(id) && nodeBuilders.get(id) != entry.getValue()) {
                throw new IllegalArgumentException("Unable to add flow due to conflicting key " + id);
            } else {
                nodeBuilders.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Builds a flow. Note that all node references including exceptional routes are validated and
     * {@link IllegalStateException} is thrown if nonexistent node is referenced.
     * @return {@link Flow} instance
     * @throws IllegalStateException if nonexistent node is referenced.
     */
    public Flow<T> build() {
        Map<String, Node<T>> nodes = new HashMap<>();
        // TODO validate builders so all references can be resolved
        nodeBuilders.forEach((id, builder) -> nodes.put(id, builder.build()));
        return new Flow<>(nodes, startNode);
    }

    /**
     * Allows to use methods that accepts a name in place of {@link Flow}, {@link Consumer} or {@link Predicate}.
     * Supplied {@link BeanResolver} implementation will be used to lookup corresponding objects by name.
     * @param beanResolver
     */
    public void setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    /**
     *
     * @return {@link BeanResolver} implementation used by this builder to lookup {@link Consumer},
     * {@link Flow} and {@link Predicate} beans
     */
    public BeanResolver getBeanResolver() {
        return beanResolver;
    }

    /**
     *
     * @return a class serving as flow data object
     */
    public Class<T> getContextClass() {
        return contextClass;
    }

    /**
     * Actually just an id, not a node.
     * @return id of start step
     */
    public String getStartNode() {
        return startNode;
    }
}
