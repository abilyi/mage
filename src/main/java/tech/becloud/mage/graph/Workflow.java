package tech.becloud.mage.graph;

import tech.becloud.mage.model.UserContext;
import tech.becloud.mage.model.WorkflowContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

/**
 * This class represents a workflow type instance.
 * It offers identification to given flow and utility methods to start flow execution on given data,
 * or continue an execution using a full workflow state represented by {@link WorkflowContext}.
 * Workflow is identified by name and version, so application may complete executions of previous workflow version.
 *
 * @param <T> a type of workflow's data object
 */
public class Workflow<T extends UserContext> {

    private final String id;
    private final int version;
    private final Flow<T> flow;
    private WorkflowExceptionHandler<T> exceptionHandler;
    private BiConsumer<? super T, ExecutionState> completionHandler;

    /**
     * @param name workflow name
     * @param version workflow version
     * @param flow flow to be executed
     */
    public Workflow(String name, int version, Flow<T> flow) {
        this.id = name;
        this.version = version;
        this.flow = flow;
    }

    /**
     * Creates {@link WorkflowContext} instance that encapsulates user data and execution state. Note that executionId
     * provided by user data object will be used, otherwise a new id will be generated and set to both user data object
     * and execution state object.
     * This context can be used to pause or cancel running execution. Pausing may be useful for graceful shutdown
     * implementation.
     * @param userContext a data object to process in a flow
     * @return WorkflowContext instance.
     */
    public WorkflowContext<T> createContext(T userContext) {
        final UUID executionId = Optional.ofNullable(userContext.getExecutionId()).orElseGet(UUID::randomUUID);
        ExecutionContext<T> executionContext = new ExecutionContext<T>(id, version, executionId);
        userContext.setExecutionId(executionContext.getExecutionId());
        return new WorkflowContext<>(executionContext, userContext);
    }

    private void prepareContext(WorkflowContext<T> workflowContext) {
        final ExecutionContext<T> executionContext = workflowContext.getExecutionContext();
        Optional.ofNullable(exceptionHandler).ifPresent(executionContext::setExceptionHandler);
        Optional.ofNullable(completionHandler).ifPresent(executionContext::setCompletionHandler);
    }

    /**
     * Starts a flow execution in a separate thread submitting it to provided {@link ExecutorService}
     * @param workflowContext an execution state, including data object to process with a flow
     * @param executorService {@link ExecutorService} that schedules flow execution to it's thread
     * @return a {@link Future} representing result of execution; may be used to check for completion.
     * If flow terminates with an exception it's {@code get()} method will throw that exception.
     */
    public Future<Void> start(WorkflowContext<T> workflowContext, ExecutorService executorService) {
        prepareContext(workflowContext);
        workflowContext.getExecutionContext().setExecutionPoint(flow.getStartNode());
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    /**
     * Executes flow in current thread.
     * @param workflowContext an execution state, including data object to process with a flow
     */
    public void start(WorkflowContext<T> workflowContext) {
        prepareContext(workflowContext);
        workflowContext.getExecutionContext().setExecutionPoint(flow.getStartNode());
        flow.accept(workflowContext);
    }

    /**
     * Resumes a flow execution in a separate thread submitting it to provided {@link ExecutorService}
     * @param workflowContext an execution state, including data object to process with a flow
     * @param executorService {@link ExecutorService} that schedules flow execution to it's thread
     * @return a {@link Future} representing result of execution; may be used to check for completion.
     * If flow terminates with an exception it's {@code get()} method will throw that exception.
     */
    public Future<Void> resume(WorkflowContext<T> workflowContext, ExecutorService executorService) {
        prepareContext(workflowContext);
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    /**
     * Resumes a flow execution in a current thread.
     * @param workflowContext an execution state, including data object to process with a flow
     */
    public void resume(WorkflowContext<T> workflowContext) {
        prepareContext(workflowContext);
        flow.accept(workflowContext);
    }

    /**
     *
     * @param exceptionHandler
     */
    public void setExceptionHandler(WorkflowExceptionHandler<T> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     *
     * @param completionHandler
     */
    public void setCompletionHandler(BiConsumer<? super T, ExecutionState> completionHandler) {
        this.completionHandler = completionHandler;
    }
}
