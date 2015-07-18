package com.github.ruediste.rise.crud.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a property as identifying. It is shown in 'identify' and
 * 'identify-table'. Also implies {@link CrudColumn @CrudColumn}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrudIdentifying {

}
