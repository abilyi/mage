package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;
import tech.becloud.workflow.persistence.PersistContextScope;

import java.util.ArrayList;
import java.util.List;

public class RouterNode<T> extends Node<T> {

    private List<Route<T>> routes;

    public RouterNode(String id, List<Route<T>> routes, String defaultRoute, List<ExceptionRoute> exceptionRoutes) {
        super(id, exceptionRoutes);
        this.routes = new ArrayList<>(routes);
        this.routes.add(new Route<>(t -> true, defaultRoute));
    }

    @Override
    public String executeAction(WorkflowContext<T> workflowContext) {
        T context = workflowContext.getContext();
        for (Route<T> route : routes) {
            if (route.test(context)) {
                return route.getNodeId();
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
