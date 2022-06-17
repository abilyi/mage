package tech.becloud.workflow.graph;

import java.util.function.Predicate;

/**
 * Supplemental class for RouterNode with predicate to match and step (node) id to follow.
 * @param <T>
 */
final class Route<T> {
    public final Predicate<? super T> predicate;
    public final String nodeId;

    public Route(Predicate<? super T> predicate, String nodeId) {
        this.predicate = predicate;
        this.nodeId = nodeId;
    }
}
