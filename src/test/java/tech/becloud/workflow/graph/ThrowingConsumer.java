package tech.becloud.workflow.graph;

import java.util.function.Consumer;

public class ThrowingConsumer implements Consumer<TestContext> {
    @Override
    public void accept(TestContext testContext) {
        throw new IllegalStateException("Throwing Consumer");
    }
}
