package com.github.ruediste.laf.core.base.configuration;

import java.lang.annotation.*;

/**
 * Define additional keys for use in a properties file for a
 * {@link ConfigurationParameter}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationKey {
	String[] value();
}
