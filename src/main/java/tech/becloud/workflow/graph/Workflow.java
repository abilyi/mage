package tech.becloud.workflow.graph;

import tech.becloud.workflow.model.WorkflowContext;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This class represents a workflow type instance.
 * It offers identification to given flow and utility methods to start flow execution on given data,
 * or continue an execution using a fill workflow state represented by {@link WorkflowContext}.
 * Workflow is identified by name and version, so application may complete executions of previous workflow version.
 *
 * @param <T> a type of workflow's data object
 */
public class Workflow<T> {

    private final String id;
    private final int version;
    private final Flow<T> flow;

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
     * Starts a flow execution in a separate thread submitting it to provided {@link ExecutorService}
     * @param context a data object to process with a flow
     * @param executorService {@link ExecutorService} that schedules flow execution to it's thread
     * @return a {@link Future} representing result of execution; may be used to check for completion.
     * If flow terminates with an exception it's {@code get()} method will throw that exception.
     */
    public Future<Void> start(T context, ExecutorService executorService) {
        ExecutionContext executionContext = new ExecutionContext(id, version, UUID.randomUUID());
        WorkflowContext<T> workflowContext = new WorkflowContext<>(executionContext, context);
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    /**
     * Executes flow in current thread.
     * @param context a data object to process with a flow
     */
    public void start(T context) {
        ExecutionContext executionContext = new ExecutionContext(id, version, UUID.randomUUID());
        WorkflowContext<T> workflowContext = new WorkflowContext<>(executionContext, context);
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
        return executorService.submit(new FlowCallable<>(flow, workflowContext));
    }

    /**
     * Resumes a flow execution in a current thread.
     * @param workflowContext an execution state, including data object to process with a flow
     */
    public void resume(WorkflowContext<T> workflowContext) {
        flow.accept(workflowContext);
    }
}
