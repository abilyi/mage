package tech.becloud.workflow.graph;

import java.util.function.Consumer;

public class TrackingConsumer implements Consumer<TestContext> {

    private final String name;

    public TrackingConsumer(String name) {
        this.name = name;
    }

    @Override
    public void accept(TestContext testContext) {
        testContext.history.add(name);
    }
}
