package laf.mvc.api;

import java.lang.annotation.*;

/**
 * Qualifier to mark action methods that perform updates. These methods are
 * started with a serializable transaction which might be commited by the
 * controller.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Updating {

}
