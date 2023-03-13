package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;

public class FlowCall<T extends UserContext> {
    public final Flow<T> flow;
    public final FlowCompletionHandler<T> flowCompletionHandler;
    private String nodeId;

    public FlowCall(Flow<T> flow, FlowCompletionHandler<T> flowCompletionHandler) {
        this.flow = flow;
        this.flowCompletionHandler = flowCompletionHandler;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
