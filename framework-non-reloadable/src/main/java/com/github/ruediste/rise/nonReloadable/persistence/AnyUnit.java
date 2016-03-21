package com.github.ruediste.rise.nonReloadable.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

/**
 * When placed on an {@link Entity}, {@link MappedSuperclass} or
 * {@link Embeddable}, indicates that the class should be part all persistence
 * units.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface AnyUnit {

}
