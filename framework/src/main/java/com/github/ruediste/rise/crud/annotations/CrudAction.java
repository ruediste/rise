package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.core.persistence.NoTransaction;

/**
 * Make a method available from the display CRUD view. To make the also
 * available in the browser view, use {@link CrudQuickAction @CrudQuickAction}
 * instead.
 * <p>
 * The method can take any numbers of parameters. They will be injected.
 * 
 * Since action methods will typically invoke services, they are a major
 * violation of the dependency structure. To mitigate the issue, they must be
 * private if they accept parameters. This keeps other code from invoking them.
 * In addition keep them short and simple, typically just one service call.
 * 
 * The method will be execute in an updating transaction, unless
 * {@link NoTransaction @NoTransaction} is present.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrudAction {

}
