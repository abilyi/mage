package tech.becloud.workflow.graph;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Getter
public class RouterBuilder<T> extends NodeBuilder<T, RouterBuilder<T>> {

    private final List<Route<T>> routes;
    private String defaultRoute;

    public RouterBuilder(FlowBuilder<T> flowBuilder, String id) {
        super(flowBuilder, id);
        this.routes = new ArrayList<>();
    }

    @Override
    public Node<T> build() {
        return new RouterNode<>(id, routes, defaultRoute, exceptionRoutes);
    }

    RouterBuilder<T> routeIf(Predicate<T> predicate, String nodeId) {
        routes.add(new Route<>(predicate, nodeId));
        return this;
    }

    RouterBuilder<T> routeIf(Predicate<T> predicate, FlowBuilder<T> builder) {
        routes.add(new Route<>(predicate, builder.getStartNode()));
        flowBuilder.add(builder);
        return this;
    }

    RouterBuilder<T> defaultRoute(String nodeId) {
        this.defaultRoute = nodeId;
        return this;
    }

    RouterBuilder<T> defaultRoute(FlowBuilder<T> builder) {
        this.defaultRoute = builder.getStartNode();
        flowBuilder.add(builder);
        return this;
    }
}
