package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.ruediste.rise.core.persistence.em.EntityManagerSet;

/**
 * Indicates that an {@link EntityManagerSet} should be associated with each
 * instance of a class and that this set should be made current during the
 * execution of each method of the instance (unless the method is annotated with
 * {@link SkipOfOwnEntityManagers @SkipOfOwnEntityManagers}).
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OwnEntityManagers {

}
