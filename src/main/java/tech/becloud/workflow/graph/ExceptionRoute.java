package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.UserContext;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Class defining a routing to next execution step if current step completes exceptionally.
 * The rule for definition order for such routes are the same as for exception handling.
 */
public class ExceptionRoute<T extends UserContext> implements Predicate<Throwable> {
    /**
     * An exception type to match. Note that subclassed will be matched as well.
     */
    public final Class<? extends Throwable> matchingClass;
    public final BiConsumer<? super T, Throwable> handler;

    /**
     * id of step (node) to execute if exception matches, i.e. is of supplied class or a subclass of it.
     */
    public final String nodeId;

    public ExceptionRoute(Class<? extends Throwable> matchingClass, String nodeId) {
        this.matchingClass = matchingClass;
        this.handler = null;
        this.nodeId = nodeId;
    }

    public ExceptionRoute(Class<? extends Throwable> matchingClass, BiConsumer<? super T, Throwable> handler,
                          String nodeId) {
        this.matchingClass = matchingClass;
        this.handler = handler;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(Throwable e) {
        return matchingClass == null || matchingClass.isInstance(e);
    }
}
