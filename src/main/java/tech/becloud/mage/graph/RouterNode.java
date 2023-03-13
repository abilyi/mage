package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;
import tech.becloud.mage.persistence.PersistContextScope;

import java.util.ArrayList;
import java.util.List;

public class RouterNode<T extends UserContext> extends Node<T> {

    private final List<Route<? super T>> routes;

    public RouterNode(String id, List<Route<? super T>> routes, String defaultRoute, List<ExceptionRoute<T>> exceptionRoutes) {
        super(id, exceptionRoutes);
        ArrayList<Route<? super T>> allRoutes = new ArrayList<>(routes);
        allRoutes.add(new Route<>(t -> true, defaultRoute));
        this.routes = List.copyOf(allRoutes);
    }

    @Override
    public String apply(WorkflowContext<T> workflowContext) {
        try {
            T userContext = workflowContext.getUserContext();
            for (Route<? super T> route : routes) {
                if (route.predicate.test(userContext)) {
                    return route.nodeId;
                }
            }
            throw new IllegalStateException("No default route found, abandon execution");
        } catch (Exception e) {
            return routeOnException(e, workflowContext);
        }

    }

    @Override
    PersistContextScope getPersistenceScope() {
        if (persistContextScope == PersistContextScope.NONE || persistContextScope == PersistContextScope.USER) {
            return PersistContextScope.NONE;
        }
        return PersistContextScope.EXECUTION;
    }
}
