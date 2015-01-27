package com.github.ruediste.laf.core.base.configuration;

import java.lang.annotation.*;

/**
 * Indicates that a method of a {@link ConfigurationDefiner} extends the
 * configuration of other definer. Thus the {@link ConfigurationParameter}
 * passed to the method is already configured by the other
 * {@link ConfigurationValueProvider}s
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtendConfiguration {

}
