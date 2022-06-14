package tech.becloud.workflow.graph;

/**
 * RuntimeException class wrapping an exception happened during flow execution.
 */
public class WokflowExecutionException extends RuntimeException {
    private final String executionPath;

    public WokflowExecutionException(Throwable cause, String executionPath) {
        super(cause);
        this.executionPath = executionPath;
    }

    /**
     *
     * @return a path to a node which caused an exception.
     * Consist of a node names that was called to reach a failing node, separated with '/'.
     */
    public String getExecutionPath() {
        return executionPath;
    }
}
