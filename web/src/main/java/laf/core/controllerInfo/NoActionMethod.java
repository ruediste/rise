package laf.core.controllerInfo;

import java.lang.annotation.*;

/**
 * Marks Methods as non-action methods, or types as not containing any action
 * methods. This annotation is respected by the
 * {@link ControllerInfoCreationService}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface NoActionMethod {

}
