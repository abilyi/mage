package tech.becloud.workflow.graph;

import java.util.function.Consumer;

public interface BeanResolver {
    <T> Consumer<T> getConsumer(String name, Class<T> klass);

    <T> Flow<T> getFlow(String name, Class<T> klass);
}
