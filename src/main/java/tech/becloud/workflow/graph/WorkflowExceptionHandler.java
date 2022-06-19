package tech.becloud.workflow.graph;

public interface WorkflowExceptionHandler<T> {

    void handle(T context, Throwable e, String path);
}
