package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;

public class WorkflowCompletionHandler<T extends UserContext> implements FlowCompletionHandler<T> {

    @Override
    public String handle(WorkflowContext<T> workflowContext, WokflowExecutionException ex) {
        return null;
    }
}
