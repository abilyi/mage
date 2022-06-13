package tech.becloud.workflow.graph;

import lombok.Getter;

import java.util.function.Predicate;

public final class Route<T> implements Predicate<T> {
    private final Predicate<? super T> predicate;
    @Getter
    private final String nodeId;

    public Route(Predicate<? super T> predicate, String nodeId) {
        this.predicate = predicate;
        this.nodeId = nodeId;
    }

    @Override
    public boolean test(T context) {
        return predicate.test(context);
    }

}
