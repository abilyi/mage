package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.persistence.WorkflowContextRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ExecutionContext<T extends UserContext> {
    private String serviceInstanceId;
    private final UUID executionId;
    private final String workflowName;
    private final int workflowVersion;
    private WorkflowContextRepository<T> workflowContextRepository;
    private WorkflowExceptionHandler<T> exceptionHandler;
    private BiConsumer<? super T, ExecutionState> completionHandler;
    private volatile ExecutionState executionState;
    private volatile boolean pauseRequested;
    private volatile boolean canceled;
    private volatile String executionPoint;
    private CompletableFuture<UUID> pausedCompletableFuture;
    private ExecutorService executorService;
    private final Deque<FlowCall<T>> flowCallStack;

    public ExecutionContext(String workflowName, int workflowVersion, UUID executionId) {
        this.workflowName = workflowName;
        this.workflowVersion = workflowVersion;
        this.executionId = executionId;
        this.executionPoint = null;
        this.flowCallStack = new ConcurrentLinkedDeque<>();
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

    WorkflowContextRepository<T> getWorkflowContextRepository() {
        return workflowContextRepository;
    }

    void setWorkflowContextRepository(WorkflowContextRepository<T> workflowContextRepository) {
        this.workflowContextRepository = workflowContextRepository;
    }

    public WorkflowExceptionHandler<T> getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(WorkflowExceptionHandler<T> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public BiConsumer<? super T, ExecutionState> getCompletionHandler() {
        return completionHandler;
    }

    public void setCompletionHandler(BiConsumer<? super T, ExecutionState> completionHandler) {
        this.completionHandler = completionHandler;
    }

    public ExecutionState getExecutionState() {
        return executionState;
    }

    public void setExecutionState(ExecutionState executionState) {
        this.executionState = executionState;
    }

    /**
     * @return
     */
    public String getExecutionPoint() {
        return executionPoint;
    }

    /**
     *
     * @param executionPoint
     */
    public void setExecutionPoint(String executionPoint) {
        this.executionPoint = executionPoint;
    }

    String updateExecutionPoint() {
        executionPoint = flowCallStack.stream().map(FlowCall::getNodeId).collect(Collectors.joining("/"));
        return executionPoint;
    }

    int getSubflowDepth() {
        return flowCallStack.size();
    }

    public boolean isPauseRequested() {
        return pauseRequested;
    }

    /**
     * Passing {@code true} means a request to pause execution as soon as possible, i.e. right after current node
     * execution completes.
     * @param pauseRequested
     */
    public void setPauseRequested(boolean pauseRequested) {
        this.pauseRequested = pauseRequested;
    }

    public CompletableFuture<UUID> getPausedCompletableFuture() {
        return pausedCompletableFuture;
    }

    public void setPausedCompletableFuture(CompletableFuture<UUID> completableFuture) {
        this.pausedCompletableFuture = completableFuture;
    }

    FlowCall<T> peekFlowCall() {
        return flowCallStack.peek();
    }

    FlowCall<T> popFlowCall() {
        return flowCallStack.pop();
    }

    void pushFlowCall(Flow<T> flow, FlowCompletionHandler<T> completionHandler) {
        flowCallStack.push(new FlowCall<>(flow, completionHandler));
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
