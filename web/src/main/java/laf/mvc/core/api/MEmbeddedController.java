package laf.mvc.core.api;

import java.lang.annotation.*;

import javax.inject.Qualifier;

@Qualifier
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MEmbeddedController {

}
