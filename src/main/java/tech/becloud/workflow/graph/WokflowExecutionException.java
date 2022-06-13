package tech.becloud.workflow.graph;

import lombok.Getter;

@Getter
public class WokflowExecutionException extends RuntimeException {
    private final String executionPath;

    public WokflowExecutionException(Throwable cause, String executionPath) {
        super(cause);
        this.executionPath = executionPath;
    }
}
