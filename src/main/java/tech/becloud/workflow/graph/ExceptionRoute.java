package tech.becloud.workflow.graph;

import lombok.Getter;

import java.util.function.Predicate;

@Getter
public class ExceptionRoute implements Predicate<Exception> {
    private final Class<? extends Exception> matchingClass;
    private final String nodeId;

    public ExceptionRoute(Class<? extends Exception> matchingClass, String nodeId) {
        this.matchingClass = matchingClass;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(Exception e) {
        return matchingClass == null || matchingClass.isInstance(e);
    }
}
