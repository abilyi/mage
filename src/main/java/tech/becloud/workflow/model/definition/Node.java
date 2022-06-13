package tech.becloud.workflow.model.definition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Node {
    private String id;
    private List<ExceptionRoute> exceptionRoutes;
}
