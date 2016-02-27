package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make a method available from the browser view.
 * <p>
 * See {@link CrudDisplayAction} for details on how the methods are invoked.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrudBrowseAction {

}
