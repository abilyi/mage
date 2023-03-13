package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;

public interface FlowCompletionHandler<T extends UserContext> {

    String handle(WorkflowContext<T> tWorkflowContext, WokflowExecutionException ex);

}
