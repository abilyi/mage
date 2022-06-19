package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;

import java.util.function.Consumer;

/**
 * Interface for integration with DI like Spring, Micronaut, etc. that allows bean lookup by name and type
 * Implementation may support or be based on local cache as well. Once definitions get implemented 
 */
public interface BeanResolver {

    /**
     * Performs lookup for a {@link Consumer} bean by name, verifying that it is parametrized with given type
     * @param name {@link Consumer} implementation bean name to lookup
     * @param klass Type that consumer implementation should accept as parameter
     * @param <T> type parameter for consumer
     * @return a corresponding {@link Consumer} instance
     * @throws
     */
    <T extends UserContext> Consumer<? super T> getConsumer(String name, Class<T> klass);

    /**
     * Performs lookup for a {@link Flow} bean by name, verifying that it is parametrized with given type
     * @param name {@link Flow} bean name to lookup
     * @param klass Type that consumer implementation should accept as parameter
     * @param <T> type parameter for flow
     * @return a corresponding {@link Flow} instance
     * @throws
     */
    <T extends UserContext> Flow<T> getFlow(String name, Class<T> klass);
}
