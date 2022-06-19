package tech.becloud.mage.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.becloud.mage.graph.BeanResolver;
import tech.becloud.mage.graph.Flow;
import tech.becloud.mage.model.UserContext;

import java.util.function.Consumer;

@Component
public class SpringBeanResolver implements BeanResolver {
    private final BeanFactory beanFactory;

    @Autowired
    public SpringBeanResolver(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UserContext> Consumer<? super T> getConsumer(String name, Class<T> klass) {
        Consumer<?> bean = beanFactory.getBean(name, Consumer.class);
        // TODO verify consuming class using GenericTypeResolver mehods
        return (Consumer<? super T>) bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends UserContext> Flow<T> getFlow(String name, Class<T> klass) {
        Flow<?> bean = beanFactory.getBean(name, Flow.class);
        // TODO verify parameter class using GenericTypeResolver mehods
        return (Flow<T>) bean;
    }
}
