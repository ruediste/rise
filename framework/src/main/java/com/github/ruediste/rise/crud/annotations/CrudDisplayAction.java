package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.core.persistence.TransactionControl;
import com.github.ruediste.rise.nonReloadable.InjectorsHolder;

/**
 * Make a method available from the display CRUD view. Can be used together or
 * instead of {@link CrudBrowseAction} and {@link CrudEditAction}.
 * <p>
 * The method can take any numbers of parameters. Their value will be queried
 * from the user.
 * 
 * <p>
 * Often, action methods will use the {@link InjectorsHolder} to get a service
 * reference and invoke a method on it. Keep such methods short and simple and
 * restrict visibility as far as possible.
 * 
 * <p>
 * There is no special transaction handling performed by the framework. Use the
 * {@link TransactionControl} to do that. The view is refreshed after the method
 * returns.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrudDisplayAction {

}
