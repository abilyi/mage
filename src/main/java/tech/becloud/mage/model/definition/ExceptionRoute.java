package tech.becloud.mage.model.definition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionRoute {
    private String exceptionClass;
    private String handler;
    private String route;
}
