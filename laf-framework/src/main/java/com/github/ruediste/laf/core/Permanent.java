package com.github.ruediste.laf.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Indicates that a dependency should be satisfied from the permanent injector
 * when beeing injected using the dynamic injector
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Permanent {

}
