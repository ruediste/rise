package laf.component.web;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ActionPaths {

	ActionPath[] value();
}
