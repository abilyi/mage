package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;

import java.util.ArrayList;
import java.util.List;

public class RouterNode<T> extends Node<T> {

    private final List<Route<? super T>> routes;

    public RouterNode(String id, List<Route<? super T>> routes, String defaultRoute, List<ExceptionRoute> exceptionRoutes) {
        super(id, exceptionRoutes);
        ArrayList<Route<? super T>> allRoutes = new ArrayList<>(routes);
        allRoutes.add(new Route<>(t -> true, defaultRoute));
        this.routes = List.copyOf(allRoutes);
    }

    @Override
    public String executeAction(WorkflowContext<T> workflowContext) {
        T context = workflowContext.getContext();
        for (Route<? super T> route : routes) {
            if (route.predicate.test(context)) {
                return route.nodeId;
            }
        }
        throw new IllegalStateException("No default route found, abandon execution");
    }

    @Override
    PersistContextScope getPersistenceScope() {
        if (persistContextScope == PersistContextScope.NONE || persistContextScope == PersistContextScope.USER) {
            return PersistContextScope.NONE;
        }
        return PersistContextScope.EXECUTION;
    }
}
