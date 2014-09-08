package laf.component.core.api;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Annotation marking component controllers
 */
@Qualifier
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CController {
}
