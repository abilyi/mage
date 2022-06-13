package tech.becloud.workflow.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExecutionContext {
    private String serviceInstanceId;
    private final UUID executionId;
    private final String workflowName;
    private final int workflowVersion;
    private final List<String> currentNodePath;
    private int subflowDepth;

    public ExecutionContext(String workflowName, int workflowVersion, UUID executionId) {
        this.workflowName = workflowName;
        this.workflowVersion = workflowVersion;
        this.executionId = executionId;
        this.currentNodePath = new ArrayList<>();
    }

    /**
     * This property identifies an instance running a workflow.
     * On instance termination other instances may pick up a workflow and continue execution
     * @return server instance identifier
     */
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    /**
     * Set an identifier of instance running a workflow. Intended for clustering applications running workflows.
     * @param serviceInstanceId server instance identifier
     */
    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    /**
     *
     * @return Workflow instance identifier
     */
    public UUID getExecutionId() {
        return executionId;
    }

    /**
     * Workflow name serves as workflow type identifier. This property can be set only via constructor.
     * @return workflow name, also used as workflow type
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * Workflow version along with name identifies distinct flow. This property can be set only via constructor.
     * This allows workflows versioning which is a typical task in application's lifecycle.
     * @return workflow version
     */
    public int getWorkflowVersion() {
        return workflowVersion;
    }

    /**
     *
     * @return
     */
    public String getExecutionPoint() {
        return String.join("/", currentNodePath);
    }

    public void setExecutionPoint(String executionPoint) {
        currentNodePath.clear();
        if (executionPoint == null || executionPoint.isEmpty()) {
            return;
        }
        String[] path = executionPoint.split("/");
        currentNodePath.addAll(Arrays.asList(path));
    }

    List<String> getCurrentNodePath() {
        return currentNodePath;
    }

    int getSubflowDepth() {
        return subflowDepth;
    }

    void setSubflowDepth(int subflowDepth) {
        this.subflowDepth = subflowDepth;
    }

}
