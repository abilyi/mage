package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RouterBuilder<T extends UserContext> extends NodeBuilder<T, RouterBuilder<T>> {

    private final List<Route<? super T>> routes;
    private String defaultRoute;

    public RouterBuilder(FlowBuilder<T> flowBuilder, String id) {
        super(flowBuilder, id);
        this.routes = new ArrayList<>();
    }

    @Override
    public Node<T> build() {
        return new RouterNode<>(id, routes, defaultRoute, exceptionRoutes);
    }

    /**
     * Adds new route to given step, selected if supplied predicate matches
     * @param predicate predicate to test on flow data
     * @param nodeId step (node) id to be executed next
     * @return builder for {@link RouterNode}
     */
    RouterBuilder<T> routeTo(Predicate<? super T> predicate, String nodeId) {
        routes.add(new Route<T>(predicate, nodeId));
        return this;
    }

    /**
     * Adds new route with following steps supplied as flow builder, selected if supplied predicate matches.
     * @param predicate predicate to test on flow data
     * @param builder flow builder defining steps to execute
     * @return builder for {@link RouterNode}
     */
    RouterBuilder<T> route(Predicate<? super T> predicate, FlowBuilder<T> builder) {
        routes.add(new Route<T>(predicate, builder.getStartNode()));
        flowBuilder.add(builder);
        return this;
    }

    /**
     * Adds new 'default' route to given step, selected if no other routes matches.
     * @param nodeId step (node) id to be executed next
     * @return builder for {@link RouterNode}
     */
    RouterBuilder<T> defaultRoute(String nodeId) {
        this.defaultRoute = nodeId;
        return this;
    }

    /**
     * Adds new 'default' route with following steps supplied as flow builder, selected if no other routes matches.
     * @param builder flow builder defining steps to execute
     * @return builder for {@link RouterNode}
     */
    RouterBuilder<T> defaultRoute(FlowBuilder<T> builder) {
        this.defaultRoute = builder.getStartNode();
        flowBuilder.add(builder);
        return this;
    }

    /**
     * @return routes configured in this builder. Returned list can't be modified directly.
     */
    public List<Route<? super T>> getRoutes() {
        return List.copyOf(routes);
    }

    /**
     * @return default route configured in this builder.
     */
    public String getDefaultRoute() {
        return defaultRoute;
    }
}
