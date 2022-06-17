package tech.becloud.workflow.graph;

import java.util.ArrayList;
import java.util.List;

public class TestContext {
    boolean action1;
    boolean action2;
    boolean condition;
    List<String> history;

    public TestContext() {
        history = new ArrayList<>();
    }
}
