package com.github.ruediste.laf.core.base.configuration;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationDefault {
	String value();
}
