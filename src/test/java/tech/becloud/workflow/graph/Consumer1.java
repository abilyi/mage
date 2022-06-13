package tech.becloud.workflow.graph;

import java.util.function.Consumer;

public class Consumer1 implements Consumer<TestContext> {

    @Override
    public void accept(TestContext testContext) {
        testContext.action1 = true;
    }
}
