package com.github.ruediste.rise.core.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Skip the registration of the own entity managers for a method of a class
 * annotated with {@link OwnEntityManagers @OwnEntityManagers}.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipOfOwnEntityManagers {

}
