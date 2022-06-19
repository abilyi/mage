package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SubflowBuilder<T extends UserContext> extends NodeBuilder<T, SubflowBuilder<T>> {
    private final Flow<T> flow;
    private String nextNode;

    public SubflowBuilder(FlowBuilder<T> flowBuilder, String id, Flow<T> flow) {
        super(flowBuilder, id);
        this.flow = flow;
    }

    public Node<T> build() {
        return new SubflowNode<>(id, flow, nextNode, exceptionRoutes);
    }

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>}.
     * Added step became a successor of current one.
     * @param id workflow step id
     * @param action {@link Consumer<T>} implementation to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalArgumentException if step with same id already exists
     */
    public ActionNodeBuilder<T> execute(String id, Consumer<? super T> action) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, action);
        nextNode = id;
        return next;
    }

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>} bean.
     * Added step became a successor of current one.
     * @param id workflow step id
     * @param beanName bean implementing {@link Consumer<T>} interface to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalStateException if no {@link BeanResolver} supplied to parent {@link FlowBuilder}
     * @throws IllegalArgumentException if step with same id already exists
     *
     */
    public ActionNodeBuilder<T> execute(String id, String beanName) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id, beanName);
        nextNode = id;
        return next;
    }

    /**
     * Adds a step that executes an action calling an {@code accept()} method of supplied {@link Consumer<T>} bean.
     * Added step became a successor of current one.
     * @param id workflow step id, also used as bean name to lookup a {@link Consumer<T>} interface to call on this step
     * @return builder for {@link ActionNode<T>} implementing such step.
     * @throws IllegalStateException if no {@link BeanResolver} supplied to parent {@link FlowBuilder}
     * @throws IllegalArgumentException if step with same id already exists
     *
     */
    public ActionNodeBuilder<T> execute(String id) {
        ActionNodeBuilder<T> next = flowBuilder.execute(id);
        nextNode = id;
        return next;
    }

    /**
     * Adds a "router" step intended for dispatching further execution to a distinct path.
     * Basically, it is an if-elseif-else implementation in terms of workflow step.
     * Added step became a successor of current one.
     * @param id workflow step id
     * @param predicate predicate to test to select this route
     * @param targetNodeId id of node to be executed when condition is met
     * @return builder for {@link RouterNode<T>} implementing such step.Note that unlike other builders {@link RouterBuilder}
     * doesn't offers methods to 'continue' flow; instead it's routeIf() and defaultRoute() methods accepts subsequent
     * node or FlowBuilder, so further flow is built in scope of these methods call.
     * @throws IllegalArgumentException if step with same id already exists
     */
    public RouterBuilder<T> routeTo(String id, Predicate<? super T> predicate, String targetNodeId) {
        RouterBuilder<T> routerBuilder = flowBuilder.routeTo(id, predicate, targetNodeId);
        nextNode = id;
        return routerBuilder;
    }

    /**
     * Adds a "router" step intended for dispatching further execution to a distinct path.
     * Basically, it is an if-elseif-else implementation in terms of workflow step.
     * Added step became a successor of current one.
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
        RouterBuilder<T> routerBuilder = flowBuilder.route(id, predicate, flowBuilder);
        nextNode = id;
        return routerBuilder;
    }

    /**
     * Adds a subflow call step to workflow. Calls of subflow from subflow is supported.
     * The same flow may be used as main in one workflow and as subflow in another.
     * Added step became a successor of current one.
     * @param id workflow step id
     * @param flow flow to execute as subflow
     * @return a builder for {@link SubflowNode} implementing such step
     */
    public SubflowBuilder<T> subflow(String id, Flow<T> flow) {
        SubflowBuilder<T> next = flowBuilder.subflow(id, flow);
        nextNode = id;
        return next;
    }

    /**
     * Adds a subflow call step to workflow. Calls of subflow from subflow is supported.
     * The same flow may be used as main in one workflow and as subflow in another.
     * Added step became a successor of current one.
     * @param id subflow step id
     * @param flowBean bean name to lookup {@link Flow} instance
     * @return
     */
    public SubflowBuilder<T> subflow(String id, String flowBean) {
        SubflowBuilder<T> next = flowBuilder.subflow(id, flowBean);
        nextNode = id;
        return next;
    }
}
