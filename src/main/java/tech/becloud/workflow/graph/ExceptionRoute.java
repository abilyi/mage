package tech.becloud.workflow.graph;

import java.util.function.Predicate;

/**
 * Class defining a routing to next execution step if current step completes exceptionally.
 * The rule for definition order for such routes are the same as for exception handling.
 */
public class ExceptionRoute implements Predicate<Exception> {
    /**
     * An exception type to match. Note that subclassed will be matched as well.
     */
    public final Class<? extends Exception> matchingClass;

    /**
     * id of step (node) to execute if exception matches, i.e. is of supplied class or a subclass of it.
     */
    public final String nodeId;

    public ExceptionRoute(Class<? extends Exception> matchingClass, String nodeId) {
        this.matchingClass = matchingClass;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(Exception e) {
        return matchingClass == null || matchingClass.isInstance(e);
    }
}
