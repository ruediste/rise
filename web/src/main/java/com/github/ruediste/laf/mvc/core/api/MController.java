package com.github.ruediste.laf.mvc.core.api;

import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 * Qualifier for controller classes. Controller classes contain action methods
 * which are used to handler requests.
 *
 * @author ruedi
 *
 */
@Qualifier
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Deprecated
public @interface MController {
}
